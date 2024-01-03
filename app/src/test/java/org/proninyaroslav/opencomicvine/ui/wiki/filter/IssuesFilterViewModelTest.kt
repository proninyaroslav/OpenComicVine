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
import org.proninyaroslav.opencomicvine.types.preferences.PrefWikiIssuesFilter
import org.proninyaroslav.opencomicvine.types.preferences.PrefWikiIssuesFilterBundle
import org.proninyaroslav.opencomicvine.types.preferences.PrefWikiIssuesSort
import org.proninyaroslav.opencomicvine.ui.wiki.category.filter.IssuesFilterState
import org.proninyaroslav.opencomicvine.ui.wiki.category.filter.IssuesFilterViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class IssuesFilterViewModelTest {
    lateinit var viewModel: IssuesFilterViewModel

    @MockK
    lateinit var pref: AppPreferences

    val dispatcher = StandardTestDispatcher()

    private val initialSort = PrefWikiIssuesSort.Unknown
    private val initialFilter = PrefWikiIssuesFilterBundle(
        name = PrefWikiIssuesFilter.Name.Unknown,
        dateAdded = PrefWikiIssuesFilter.DateAdded.Unknown,
        dateLastUpdated = PrefWikiIssuesFilter.DateLastUpdated.Unknown,
        coverDate = PrefWikiIssuesFilter.CoverDate.Unknown,
        storeDate = PrefWikiIssuesFilter.StoreDate.Unknown,
        issueNumber = PrefWikiIssuesFilter.IssueNumber.Unknown,
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)

        every { pref.wikiIssuesSort } returns flowOf(initialSort)
        every { pref.wikiIssuesFilters } returns flowOf(initialFilter)
        viewModel = IssuesFilterViewModel(pref)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun load() = runTest {
        val expectedStates = listOf(
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

        verify { pref.wikiIssuesSort }
        verify { pref.wikiIssuesFilters }
        confirmVerified(pref)
    }

    @Test
    fun `Change sort`() = runTest {
        val sort = PrefWikiIssuesSort.Alphabetical(
            direction = PrefSortDirection.Desc,
        )
        val expectedStates = listOf(
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
        val filter =
            initialFilter.copy(issueNumber = PrefWikiIssuesFilter.IssueNumber.Contains("1"))
        val expectedStates = listOf(
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
        val sort = PrefWikiIssuesSort.Alphabetical(
            direction = PrefSortDirection.Desc,
        )
        val filter =
            initialFilter.copy(issueNumber = PrefWikiIssuesFilter.IssueNumber.Contains("1"))
        val expectedStates = listOf(
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

        coEvery { pref.setWikiIssuesSort(sort) } just runs
        coEvery { pref.setWikiIssuesFilters(filter) } just runs

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

        verify { pref.wikiIssuesSort }
        verify { pref.wikiIssuesFilters }
        coVerify { pref.setWikiIssuesSort(sort) }
        coVerify { pref.setWikiIssuesFilters(filter) }
        confirmVerified(pref)
    }
}