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
import org.proninyaroslav.opencomicvine.types.preferences.PrefWikiVolumesFilter
import org.proninyaroslav.opencomicvine.types.preferences.PrefWikiVolumesFilterBundle
import org.proninyaroslav.opencomicvine.types.preferences.PrefWikiVolumesSort
import org.proninyaroslav.opencomicvine.ui.wiki.category.filter.VolumesFilterState
import org.proninyaroslav.opencomicvine.ui.wiki.category.filter.VolumesFilterViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class VolumesFilterViewModelTest {
    lateinit var viewModel: VolumesFilterViewModel

    @MockK
    lateinit var pref: AppPreferences

    val dispatcher = StandardTestDispatcher()

    private val initialSort = PrefWikiVolumesSort.Alphabetical(
        direction = PrefSortDirection.Asc,
    )
    private val initialFilter = PrefWikiVolumesFilterBundle(
        name = PrefWikiVolumesFilter.Name.Unknown,
        dateAdded = PrefWikiVolumesFilter.DateAdded.Unknown,
        dateLastUpdated = PrefWikiVolumesFilter.DateLastUpdated.Unknown,
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)

        every { pref.wikiVolumesSort } returns flowOf(initialSort)
        every { pref.wikiVolumesFilters } returns flowOf(initialFilter)
        viewModel = VolumesFilterViewModel(pref)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun load() = runTest {
        val expectedStates = listOf(
            VolumesFilterState.Loaded(
                sort = initialSort,
                filterBundle = initialFilter,
            ),
        )
        val actualStates = mutableListOf<VolumesFilterState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }
        dispatcher.scheduler.runCurrent()

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        verify { pref.wikiVolumesSort }
        verify { pref.wikiVolumesFilters }
        confirmVerified(pref)
    }

    @Test
    fun `Change sort`() = runTest {
        val sort = PrefWikiVolumesSort.Alphabetical(
            direction = PrefSortDirection.Desc,
        )
        val expectedStates = listOf(
            VolumesFilterState.Loaded(
                sort = initialSort,
                filterBundle = initialFilter,
            ),
            VolumesFilterState.SortChanged(
                sort = sort,
                filterBundle = initialFilter,
                isNeedApply = true,
            ),
        )
        val actualStates = mutableListOf<VolumesFilterState>()

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
        val filter = initialFilter.copy(name = PrefWikiVolumesFilter.Name.Contains("test"))
        val expectedStates = listOf(
            VolumesFilterState.Loaded(
                sort = initialSort,
                filterBundle = initialFilter,
            ),
            VolumesFilterState.FiltersChanged(
                sort = initialSort,
                filterBundle = filter,
                isNeedApply = true,
            ),
        )
        val actualStates = mutableListOf<VolumesFilterState>()

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
        val sort = PrefWikiVolumesSort.Alphabetical(
            direction = PrefSortDirection.Desc,
        )
        val filter = initialFilter.copy(name = PrefWikiVolumesFilter.Name.Contains("test"))
        val expectedStates = listOf(
            VolumesFilterState.Loaded(
                sort = initialSort,
                filterBundle = initialFilter,
            ),
            VolumesFilterState.SortChanged(
                sort = sort,
                filterBundle = initialFilter,
                isNeedApply = true,
            ),
            VolumesFilterState.FiltersChanged(
                sort = sort,
                filterBundle = filter,
                isNeedApply = true,
            ),
            VolumesFilterState.Applied(
                sort = sort,
                filterBundle = filter,
            ),
            VolumesFilterState.SortChanged(
                sort = sort,
                filterBundle = filter,
                isNeedApply = false,
            ),
            VolumesFilterState.FiltersChanged(
                sort = sort,
                filterBundle = filter,
                isNeedApply = false,
            ),
        )
        val actualStates = mutableListOf<VolumesFilterState>()

        coEvery { pref.setWikiVolumesSort(sort) } just runs
        coEvery { pref.setWikiVolumesFilters(filter) } just runs

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

        verify { pref.wikiVolumesSort }
        verify { pref.wikiVolumesFilters }
        coVerify { pref.setWikiVolumesSort(sort) }
        coVerify { pref.setWikiVolumesFilters(filter) }
        confirmVerified(pref)
    }
}