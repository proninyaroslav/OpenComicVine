package org.proninyaroslav.opencomicvine.ui.home.filter

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.data.preferences.*
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.ui.home.category.filter.IssuesFilterState
import org.proninyaroslav.opencomicvine.ui.home.category.filter.IssuesFilterViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class IssuesFilterViewModelTest {
    lateinit var viewModel: IssuesFilterViewModel

    @MockK
    lateinit var pref: AppPreferences

    val dispatcher = StandardTestDispatcher()

    private val initialSort = PrefRecentIssuesSort.Unknown
    private val initialFilter = PrefRecentIssuesFilterBundle(
        dateAdded = PrefRecentIssuesFilter.DateAdded.Unknown,
        storeDate = PrefRecentIssuesFilter.StoreDate.Unknown,
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)

        every { pref.recentIssuesSort } returns flowOf(initialSort)
        every { pref.recentIssuesFilters } returns flowOf(initialFilter)
        viewModel = IssuesFilterViewModel(pref)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun load() = runTest {
        val expectedStates = listOf(
            IssuesFilterState.Initial,
            IssuesFilterState.Loaded(
                sort = initialSort,
                filterBundle = initialFilter,
            ),
        )
        val actualStates = mutableListOf<IssuesFilterState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }
        dispatcher.scheduler.runCurrent()

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        verify { pref.recentIssuesSort }
        verify { pref.recentIssuesFilters }
        confirmVerified(pref)
    }

    @Test
    fun `Change sort`() = runTest {
        val sort = PrefRecentIssuesSort.StoreDate(
            direction = PrefSortDirection.Desc,
        )
        val expectedStates = listOf(
            IssuesFilterState.Initial,
            IssuesFilterState.Loaded(
                sort = initialSort,
                filterBundle = initialFilter,
            ),
            IssuesFilterState.SortChanged(
                sort = sort,
                filterBundle = initialFilter,
                isNeedApply = true,
            ),
        )
        val actualStates = mutableListOf<IssuesFilterState>()

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
        val filter = initialFilter.copy(dateAdded = PrefRecentIssuesFilter.DateAdded.ThisWeek)
        val expectedStates = listOf(
            IssuesFilterState.Initial,
            IssuesFilterState.Loaded(
                sort = initialSort,
                filterBundle = initialFilter,
            ),
            IssuesFilterState.FiltersChanged(
                sort = initialSort,
                filterBundle = filter,
                isNeedApply = true,
            ),
        )
        val actualStates = mutableListOf<IssuesFilterState>()

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
        val sort = PrefRecentIssuesSort.StoreDate(
            direction = PrefSortDirection.Desc,
        )
        val filter = initialFilter.copy(dateAdded = PrefRecentIssuesFilter.DateAdded.ThisWeek)
        val expectedStates = listOf(
            IssuesFilterState.Initial,
            IssuesFilterState.Loaded(
                sort = initialSort,
                filterBundle = initialFilter,
            ),
            IssuesFilterState.SortChanged(
                sort = sort,
                filterBundle = initialFilter,
                isNeedApply = true,
            ),
            IssuesFilterState.FiltersChanged(
                sort = sort,
                filterBundle = filter,
                isNeedApply = true,
            ),
            IssuesFilterState.Applied(
                sort = sort,
                filterBundle = filter,
            ),
            IssuesFilterState.SortChanged(
                sort = sort,
                filterBundle = filter,
                isNeedApply = false,
            ),
            IssuesFilterState.FiltersChanged(
                sort = sort,
                filterBundle = filter,
                isNeedApply = false,
            ),
        )
        val actualStates = mutableListOf<IssuesFilterState>()

        coEvery { pref.setRecentIssuesSort(sort) } just runs
        coEvery { pref.setRecentIssuesFilters(filter) } just runs

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

        verify { pref.recentIssuesSort }
        verify { pref.recentIssuesFilters }
        coVerify { pref.setRecentIssuesSort(sort) }
        coVerify { pref.setRecentIssuesFilters(filter) }
        confirmVerified(pref)
    }
}