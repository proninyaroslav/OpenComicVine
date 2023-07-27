package org.proninyaroslav.opencomicvine.ui.favorites.filter

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.data.preferences.PrefFavoritesSort
import org.proninyaroslav.opencomicvine.data.preferences.PrefSortDirection
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.ui.favorites.category.filter.FavoritesFilterState
import org.proninyaroslav.opencomicvine.ui.favorites.category.filter.FavoritesFilterViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesFilterViewModelTest {
    lateinit var viewModel: FavoritesFilterViewModel

    @MockK
    lateinit var pref: AppPreferences

    private val initialSort = PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Desc)

    val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)

        every { pref.favoriteCharactersSort } returns flowOf(initialSort)

        viewModel = FavoritesFilterViewModel(pref)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun load() = runTest {
        val expectedStates = listOf(
            FavoritesFilterState.Initial,
            FavoritesFilterState.Loaded(
                sort = initialSort,
            ),
        )
        val actualStates = mutableListOf<FavoritesFilterState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }
        dispatcher.scheduler.runCurrent()

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        verify { pref.favoriteCharactersSort }
        confirmVerified(pref)
    }

    @Test
    fun `Change sort`() = runTest {
        val sort = PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Asc)
        val expectedStates = listOf(
            FavoritesFilterState.Initial,
            FavoritesFilterState.Loaded(
                sort = initialSort,
            ),
            FavoritesFilterState.SortChanged(
                sort = sort,
                isNeedApply = true,
            ),
        )
        val actualStates = mutableListOf<FavoritesFilterState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }
        dispatcher.scheduler.apply {
            runCurrent()
            viewModel.changeSort(sort)
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()
    }

    @Test
    fun apply() = runTest {
        val sort = PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Asc)
        val expectedStates = listOf(
            FavoritesFilterState.Initial,
            FavoritesFilterState.Loaded(
                sort = initialSort,
            ),
            FavoritesFilterState.SortChanged(
                sort = sort,
                isNeedApply = true,
            ),
            FavoritesFilterState.Applied(
                sort = sort,
            ),
            FavoritesFilterState.SortChanged(
                sort = sort,
                isNeedApply = false,
            ),
        )
        val actualStates = mutableListOf<FavoritesFilterState>()

        coEvery { pref.setFavoriteCharactersSort(sort) } just runs

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }
        dispatcher.scheduler.apply {
            runCurrent()
            viewModel.changeSort(sort)
            runCurrent()
            viewModel.apply()
            runCurrent()
            viewModel.changeSort(sort)
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        verify { pref.favoriteCharactersSort }
        coVerify { pref.setFavoriteCharactersSort(sort) }
        confirmVerified(pref)
    }
}