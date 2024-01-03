package org.proninyaroslav.opencomicvine.ui.wiki.filter

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.types.preferences.PrefSortDirection
import org.proninyaroslav.opencomicvine.types.preferences.PrefWikiCharactersFilter
import org.proninyaroslav.opencomicvine.types.preferences.PrefWikiCharactersFilterBundle
import org.proninyaroslav.opencomicvine.types.preferences.PrefWikiCharactersSort
import org.proninyaroslav.opencomicvine.ui.wiki.category.filter.CharactersFilterState
import org.proninyaroslav.opencomicvine.ui.wiki.category.filter.CharactersFilterViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class CharactersFilterViewModelTest {
    lateinit var viewModel: CharactersFilterViewModel

    @MockK
    lateinit var pref: AppPreferences

    val dispatcher = StandardTestDispatcher()

    private val initialSort = PrefWikiCharactersSort.Alphabetical(
        direction = PrefSortDirection.Asc,
    )
    private val initialFilter = PrefWikiCharactersFilterBundle(
        gender = PrefWikiCharactersFilter.Gender.Unknown,
        name = PrefWikiCharactersFilter.Name.Unknown,
        dateAdded = PrefWikiCharactersFilter.DateAdded.Unknown,
        dateLastUpdated = PrefWikiCharactersFilter.DateLastUpdated.Unknown,
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)

        every { pref.wikiCharactersSort } returns flowOf(initialSort)
        every { pref.wikiCharactersFilters } returns flowOf(initialFilter)
        viewModel = CharactersFilterViewModel(pref)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun load() = runTest {
        val expectedStates = listOf(
            CharactersFilterState.Loaded(
                sort = initialSort,
                filterBundle = initialFilter,
            ),
        )
        val actualStates = mutableListOf<CharactersFilterState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }
        dispatcher.scheduler.runCurrent()

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        verify { pref.wikiCharactersSort }
        verify { pref.wikiCharactersFilters }
        confirmVerified(pref)
    }

    @Test
    fun `Change sort`() = runTest {
        val sort = PrefWikiCharactersSort.Alphabetical(
            direction = PrefSortDirection.Desc,
        )
        val expectedStates = listOf(
            CharactersFilterState.Loaded(
                sort = initialSort,
                filterBundle = initialFilter,
            ),
            CharactersFilterState.SortChanged(
                sort = sort,
                filterBundle = initialFilter,
                isNeedApply = true,
            ),
        )
        val actualStates = mutableListOf<CharactersFilterState>()

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
    fun `Change filter`() = runTest {
        val filter = initialFilter.copy(gender = PrefWikiCharactersFilter.Gender.Male)
        val expectedStates = listOf(
            CharactersFilterState.Loaded(
                sort = initialSort,
                filterBundle = initialFilter,
            ),
            CharactersFilterState.FiltersChanged(
                sort = initialSort,
                filterBundle = filter,
                isNeedApply = true,
            ),
        )
        val actualStates = mutableListOf<CharactersFilterState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }
        dispatcher.scheduler.apply {
            runCurrent()
            viewModel.changeFilters(filter)
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()
    }

    @Test
    fun apply() = runTest {
        val sort = PrefWikiCharactersSort.Alphabetical(
            direction = PrefSortDirection.Desc,
        )
        val filter = initialFilter.copy(gender = PrefWikiCharactersFilter.Gender.Male)
        val expectedStates = listOf(
            CharactersFilterState.Loaded(
                sort = initialSort,
                filterBundle = initialFilter,
            ),
            CharactersFilterState.SortChanged(
                sort = sort,
                filterBundle = initialFilter,
                isNeedApply = true,
            ),
            CharactersFilterState.FiltersChanged(
                sort = sort,
                filterBundle = filter,
                isNeedApply = true,
            ),
            CharactersFilterState.Applied(
                sort = sort,
                filterBundle = filter,
            ),
            CharactersFilterState.SortChanged(
                sort = sort,
                filterBundle = filter,
                isNeedApply = false,
            ),
            CharactersFilterState.FiltersChanged(
                sort = sort,
                filterBundle = filter,
                isNeedApply = false,
            ),
        )
        val actualStates = mutableListOf<CharactersFilterState>()

        coEvery { pref.setWikiCharactersSort(sort) } just runs
        coEvery { pref.setWikiCharactersFilters(filter) } just runs

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }
        dispatcher.scheduler.apply {
            runCurrent()
            viewModel.changeSort(sort)
            runCurrent()
            viewModel.changeFilters(filter)
            runCurrent()
            viewModel.apply()
            runCurrent()
            viewModel.changeSort(sort)
            runCurrent()
            viewModel.changeFilters(filter)
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        verify { pref.wikiCharactersSort }
        verify { pref.wikiCharactersFilters }
        coVerify { pref.setWikiCharactersSort(sort) }
        coVerify { pref.setWikiCharactersFilters(filter) }
        confirmVerified(pref)
    }
}