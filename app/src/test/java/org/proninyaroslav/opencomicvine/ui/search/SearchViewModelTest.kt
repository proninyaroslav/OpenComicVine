package org.proninyaroslav.opencomicvine.ui.search

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.types.ErrorReportInfo
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.paging.SearchSourceFactory
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    lateinit var viewModel: SearchViewModel

    @MockK
    lateinit var searchSourceFactory: SearchSourceFactory

    val dispatcher = StandardTestDispatcher()

    @MockK
    lateinit var errorReportService: ErrorReportService

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)

        viewModel = SearchViewModel(searchSourceFactory, errorReportService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Change query`() = runTest {
        val expectedStates = listOf(
            SearchState.Initial,
            SearchState.QueryChanged("1"),
            SearchState.QueryChanged("2"),
            SearchState.QueryChanged("3"),
        )
        val actualStates = mutableListOf<SearchState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        dispatcher.scheduler.run {
            viewModel.changeQuery(" 1 ")
            runCurrent()
            viewModel.changeQuery("2")
            runCurrent()
            viewModel.changeQuery("3")
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()
    }

    @Test
    fun search() = runTest {
        val expectedStates = listOf(
            SearchState.Initial,
            SearchState.QueryChanged("1"),
            SearchState.Submitted("1")
        )
        val actualStates = mutableListOf<SearchState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        dispatcher.scheduler.run {
            viewModel.changeQuery("1")
            runCurrent()
            viewModel.search()
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()
    }

    @Test
    fun `Error report`() {
        val info = ErrorReportInfo(
            error = IOException(),
            comment = "comment",
        )

        every { errorReportService.report(info) } just runs

        viewModel.errorReport(info)
        dispatcher.scheduler.runCurrent()

        verify { errorReportService.report(info) }
        confirmVerified(errorReportService)
    }
}