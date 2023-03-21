package org.proninyaroslav.opencomicvine.ui.viewmodel

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.model.network.AppConnectivityManager

@OptIn(ExperimentalCoroutinesApi::class)
class NetworkConnectionViewModelTest {
    lateinit var viewModel: NetworkConnectionViewModel

    @MockK
    lateinit var connectivityManager: AppConnectivityManager

    val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)
        every { connectivityManager.isNetworkAvailable() } returns true
        viewModel = NetworkConnectionViewModel(connectivityManager)
        verify { connectivityManager.isNetworkAvailable() }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Connection available`() = runTest {
        val expectedStates = listOf(
            NetworkState.ConnectionAvailable,
        )
        val actualStates = mutableListOf<NetworkState>()

        coEvery { connectivityManager.observeNetworkAvailability } returns MutableSharedFlow()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()
    }

    @Test
    fun `No connection`() = runTest {
        val expectedStates = listOf(
            NetworkState.NoConnection,
        )
        val actualStates = mutableListOf<NetworkState>()

        val sharedFlow = MutableSharedFlow<Boolean>()
        coEvery { connectivityManager.observeNetworkAvailability } returns sharedFlow

        dispatcher.scheduler.apply {
            runCurrent()
            sharedFlow.emit(false)
        }

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        coVerify { connectivityManager.observeNetworkAvailability }
        confirmVerified(connectivityManager)
    }

    @Test
    fun `Network reestablished`() = runTest {
        val expectedStates = listOf(
            NetworkState.ConnectionAvailable,
            NetworkState.NoConnection,
            NetworkState.ConnectionAvailable,
        )
        val expectedEffects = listOf(
            NetworkEffect.Reestablished,
        )
        val actualStates = mutableListOf<NetworkState>()
        val actualEffects = mutableListOf<NetworkEffect>()

        val sharedFlow = MutableSharedFlow<Boolean>()
        coEvery { connectivityManager.observeNetworkAvailability } returns sharedFlow

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }
        val effectJob = launch {
            viewModel.effect.toList(actualEffects)
        }
        dispatcher.scheduler.apply {
            runCurrent()
            sharedFlow.emit(false)
            sharedFlow.emit(true)
            delay(100)
        }

        assertEquals(expectedStates, actualStates)
        assertEquals(expectedEffects, actualEffects)
        stateJob.cancel()
        effectJob.cancel()

        coVerify { connectivityManager.observeNetworkAvailability }
        confirmVerified(connectivityManager)
    }
}