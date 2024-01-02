package org.proninyaroslav.opencomicvine.ui.favorites

import io.mockk.MockKAnnotations
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.types.ErrorReportInfo
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesPageViewModelTest {
    lateinit var viewModel: FavoritesPageViewModel

    val dispatcher = StandardTestDispatcher()

    @MockK
    lateinit var errorReportService: ErrorReportService

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)

        viewModel = FavoritesPageViewModel(
            characterItemRepo = mockk(relaxed = true),
            issueItemRepo = mockk(relaxed = true),
            volumeItemRepo = mockk(relaxed = true),
            conceptItemRepo = mockk(relaxed = true),
            locationItemRepo = mockk(relaxed = true),
            moviesItemRepo = mockk(relaxed = true),
            objectsItemRepo = mockk(relaxed = true),
            peopleItemRepo = mockk(relaxed = true),
            storyArcsItemRepo = mockk(relaxed = true),
            teamsItemRepo = mockk(relaxed = true),
            errorReportService = errorReportService,
            charactersRemoteMediatorFactory = mockk(relaxed = true),
            issuesRemoteMediatorFactory = mockk(relaxed = true),
            volumesRemoteMediatorFactory = mockk(relaxed = true),
            conceptsRemoteMediatorFactory = mockk(relaxed = true),
            locationsRemoteMediatorFactory = mockk(relaxed = true),
            moviesRemoteMediatorFactory = mockk(relaxed = true),
            objectsRemoteMediatorFactory = mockk(relaxed = true),
            peopleRemoteMediatorFactory = mockk(relaxed = true),
            storyArcsRemoteMediatorFactory = mockk(relaxed = true),
            teamsRemoteMediatorFactory = mockk(relaxed = true),
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