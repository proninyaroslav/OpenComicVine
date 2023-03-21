package org.proninyaroslav.opencomicvine.ui.about

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
    fun load() {
        assertEquals(
            AboutState.Loaded(
                appName = appInfo.appName,
                version = appInfo.version,
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun `Load failed`() {
        val error = AppInfoProvider.State.Failed(IOException())

        every { appInfoProvider.getAppInfo() } returns error

        viewModel = AboutViewModel(errorReportService, appInfoProvider)

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

        viewModel.event(AboutEvent.ErrorReport(info))
        dispatcher.scheduler.runCurrent()

        verify { errorReportService.report(info) }
        confirmVerified(errorReportService)
    }
}