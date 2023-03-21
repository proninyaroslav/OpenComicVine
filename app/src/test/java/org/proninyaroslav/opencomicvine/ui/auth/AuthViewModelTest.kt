package org.proninyaroslav.opencomicvine.ui.auth

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
import org.proninyaroslav.opencomicvine.data.ErrorReportInfo
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.repo.ApiKeyRepository
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    lateinit var viewModel: AuthViewModel

    @MockK
    lateinit var apiKeyRepo: ApiKeyRepository

    @MockK
    lateinit var errorReportService: ErrorReportService

    val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)

        viewModel = AuthViewModel(apiKeyRepo, errorReportService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Change API key`() = runTest {
        val key = "key"
        val expectedStates = listOf(
            AuthState.Initial,
            AuthState.ApiKeyChanged(key),
        )
        val actualStates = mutableListOf<AuthState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        dispatcher.scheduler.run {
            viewModel.event(AuthEvent.ChangeApiKey(key))
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()
    }

    @Test
    fun `Submit API key`() = runTest {
        val key = "key"
        val expectedStates = listOf(
            AuthState.Initial,
            AuthState.ApiKeyChanged(key),
            AuthState.SubmitInProgress(key),
            AuthState.Submitted(key)
        )
        val actualStates = mutableListOf<AuthState>()

        coEvery { apiKeyRepo.set(key) } returns ApiKeyRepository.SaveResult.Success(Unit)

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        dispatcher.scheduler.run {
            viewModel.event(AuthEvent.ChangeApiKey(key))
            runCurrent()
            viewModel.event(AuthEvent.Submit)
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        coVerify { apiKeyRepo.set(key) }
        confirmVerified(apiKeyRepo)
    }

    @Test
    fun `Submit API key failed`() = runTest {
        val key = "key"
        val error = ApiKeyRepository.SaveResult.Failed.IO(IOException())
        val expectedStates = listOf(
            AuthState.Initial,
            AuthState.ApiKeyChanged(key),
            AuthState.SubmitInProgress(key),
            AuthState.SubmitFailed.SaveError(key, error)
        )
        val actualStates = mutableListOf<AuthState>()

        coEvery { apiKeyRepo.set(key) } returns error

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        dispatcher.scheduler.run {
            viewModel.event(AuthEvent.ChangeApiKey(key))
            runCurrent()
            viewModel.event(AuthEvent.Submit)
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        coVerify { apiKeyRepo.set(key) }
        confirmVerified(apiKeyRepo)
    }

    @Test
    fun `Empty API key error`() = runTest {
        val expectedStates = listOf(
            AuthState.Initial,
            AuthState.SubmitInProgress(""),
            AuthState.SubmitFailed.EmptyApiKey,
        )
        val actualStates = mutableListOf<AuthState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        dispatcher.scheduler.run {
            viewModel.event(AuthEvent.Submit)
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()
    }

    @Test
    fun `Submit API key after fail`() = runTest {
        val key = "key"
        val error = ApiKeyRepository.SaveResult.Failed.IO(IOException())
        val expectedStates = listOf(
            AuthState.Initial,
            AuthState.ApiKeyChanged(key),
            AuthState.SubmitInProgress(key),
            AuthState.SubmitFailed.SaveError(key, error),
            AuthState.SubmitInProgress(key),
            AuthState.Submitted(key),
        )
        val actualStates = mutableListOf<AuthState>()

        coEvery { apiKeyRepo.set(key) } returns error

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        dispatcher.scheduler.run {
            viewModel.event(AuthEvent.ChangeApiKey(key))
            runCurrent()
            viewModel.event(AuthEvent.Submit)
            runCurrent()

            coEvery { apiKeyRepo.set(key) } returns ApiKeyRepository.SaveResult.Success(Unit)
            viewModel.event(AuthEvent.Submit)
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        coVerify(exactly = 2) { apiKeyRepo.set(key) }
        confirmVerified(apiKeyRepo)
    }

    @Test
    fun `Error report`() {
        val info = ErrorReportInfo(
            error = IOException(),
            comment = "comment",
        )

        every { errorReportService.report(info) } just runs

        viewModel.event(AuthEvent.ErrorReport(info))
        dispatcher.scheduler.runCurrent()

        verify { errorReportService.report(info) }
        confirmVerified(errorReportService)
    }
}