package org.proninyaroslav.opencomicvine.ui.about

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.types.ErrorReportInfo
import org.proninyaroslav.opencomicvine.model.AppInfoProvider
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class AboutViewModelTest {
    lateinit var viewModel: AboutViewModel

    @MockK
    lateinit var appInfoProvider: AppInfoProvider

    @MockK
    lateinit var errorReportService: ErrorReportService

    val dispatcher = StandardTestDispatcher()

    private val appInfo = AppInfoProvider.State.Success(
        appName = "OpenComicVine",
        version = "1.0",
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)

        every { appInfoProvider.getAppInfo() } returns appInfo

        viewModel = AboutViewModel(errorReportService, appInfoProvider)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun load() = runTest {
        dispatcher.scheduler.runCurrent()
        assertEquals(
            AboutState.Initial,
            viewModel.state.first(),
        )

        dispatcher.scheduler.runCurrent()
        assertEquals(
            AboutState.Loaded(
                appName = appInfo.appName,
                version = appInfo.version,
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun `Load failed`() = runTest {
        val error = AppInfoProvider.State.Failed(IOException())

        every { appInfoProvider.getAppInfo() } returns error

        viewModel = AboutViewModel(errorReportService, appInfoProvider)

        dispatcher.scheduler.runCurrent()
        assertEquals(
            AboutState.Initial,
            viewModel.state.first(),
        )

        dispatcher.scheduler.runCurrent()
        assertEquals(
            AboutState.LoadFailed(error),
            viewModel.state.value,
        )
    }

    @Test
    fun `Error report`() {
        val info = ErrorReportInfo(
            error = IOException(),
            comment = "comment",
        )

        every { errorReportService.report(info) } just runs

        viewModel.errorReport(info)

        verify { errorReportService.report(info) }
        confirmVerified(errorReportService)
    }
}