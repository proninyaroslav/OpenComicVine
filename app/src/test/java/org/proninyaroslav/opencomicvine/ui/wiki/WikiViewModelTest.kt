package org.proninyaroslav.opencomicvine.ui.wiki

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.data.ErrorReportInfo
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class WikiViewModelTest {
    lateinit var viewModel: WikiViewModel

    val dispatcher = StandardTestDispatcher()

    @MockK
    lateinit var errorReportService: ErrorReportService

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)

        viewModel = WikiViewModel(
            characterItemRepo = mockk(relaxed = true),
            issueItemRepo = mockk(relaxed = true),
            volumeItemRepo = mockk(relaxed = true),
            favoritesRepo = mockk(relaxed = true),
            errorReportService = errorReportService,
            charactersRemoteMediatorFactory = mockk(relaxed = true),
            issuesRemoteMediatorFactory = mockk(relaxed = true),
            volumesRemoteMediatorFactory = mockk(relaxed = true),
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
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