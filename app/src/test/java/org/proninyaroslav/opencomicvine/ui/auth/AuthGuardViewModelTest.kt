package org.proninyaroslav.opencomicvine.ui.auth

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.model.repo.ApiKeyRepository
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class AuthGuardViewModelTest {
    lateinit var viewModel: AuthGuardViewModel

    @MockK
    lateinit var apiKeyRepo: ApiKeyRepository

    private val apiKeyFlow = MutableStateFlow<ApiKeyRepository.GetResult<String>>(
        ApiKeyRepository.GetResult.Failed.NoApiKey
    )

    val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)

        every { apiKeyRepo.get() } returns apiKeyFlow

        viewModel = AuthGuardViewModel(apiKeyRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun get() = runTest {
        val expectedStates = listOf(
            AuthGuardState.Initial,
            AuthGuardState.NotAuthorized,
            AuthGuardState.Authorized,
        )
        val actualStates = mutableListOf<AuthGuardState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        dispatcher.scheduler.run {
            runCurrent()
            apiKeyFlow.emit(ApiKeyRepository.GetResult.Success("key"))
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()
    }

    @Test
    fun `Get failed`() = runTest {
        val error = ApiKeyRepository.GetResult.Failed.IO(IOException())
        val expectedStates = listOf(
            AuthGuardState.Initial,
            AuthGuardState.GetStatusError(error),
        )
        val actualStates = mutableListOf<AuthGuardState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        dispatcher.scheduler.run {
            apiKeyFlow.emit(error)
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()
    }
}