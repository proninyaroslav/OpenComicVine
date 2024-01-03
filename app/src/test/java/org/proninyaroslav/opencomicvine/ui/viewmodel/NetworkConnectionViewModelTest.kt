package org.proninyaroslav.opencomicvine.ui.viewmodel

import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
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
import org.proninyaroslav.opencomicvine.model.network.AppConnectivityManager

@OptIn(ExperimentalCoroutinesApi::class)
class NetworkConnectionViewModelTest {
    lateinit var viewModel: NetworkConnectionViewModel

    @MockK
    lateinit var connectivityManager: AppConnectivityManager

    val dispatcher = StandardTestDispatcher()

    lateinit var sharedFlow: MutableSharedFlow<Boolean>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)

        sharedFlow = MutableSharedFlow()

        every { connectivityManager.isNetworkAvailable() } returns true
        every { connectivityManager.observeNetworkAvailability } returns sharedFlow

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
            NetworkState.Reestablished
        )
        val actualStates = mutableListOf<NetworkState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }
        dispatcher.scheduler.apply {
            runCurrent()
            sharedFlow.emit(false)
            sharedFlow.emit(true)
            delay(100)
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        coVerify { connectivityManager.observeNetworkAvailability }
        confirmVerified(connectivityManager)
    }
}