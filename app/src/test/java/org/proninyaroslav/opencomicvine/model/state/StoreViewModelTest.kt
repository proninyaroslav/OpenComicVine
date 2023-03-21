package org.proninyaroslav.opencomicvine.model.state

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StoreViewModelTest {
    val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun emit() = runTest {
        val store = TestStore()
        val expectedStates = listOf(
            TestState.Initial,
            TestState.Loading,
            TestState.Loaded,
            TestState.Unloaded,
        )
        val expectedEffects = listOf(TestEffect.Loaded, TestEffect.Unloaded)

        val actualStates = mutableListOf<TestState>()
        val actualEffects = mutableListOf<TestEffect>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            store.state.toList(actualStates)
        }
        val effectJob = launch {
            store.effect.toList(actualEffects)
        }
        dispatcher.scheduler.apply {
            store.event(TestEvent.Load)
            runCurrent()
            store.event(TestEvent.Unload)
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        assertEquals(expectedEffects, actualEffects)
        stateJob.cancel()
        effectJob.cancel()
    }
}

sealed interface TestState {
    object Initial : TestState
    object Loading : TestState
    object Loaded : TestState
    object Unloaded : TestState
}

sealed interface TestEvent {
    object Load : TestEvent
    object Unload : TestEvent
}

sealed interface TestEffect {
    object Loaded : TestEffect
    object Unloaded : TestEffect
}

class TestStore : StoreViewModel<
        TestEvent,
        TestState,
        TestEffect>(
    TestState.Initial
) {
    init {
        on<TestEvent.Load> { event ->
            assertEquals(TestEvent.Load, event)

            emitState(TestState.Loading)
            viewModelScope.launch {
                emitState(TestState.Loaded)
                emitEffect(TestEffect.Loaded)
            }
        }
        on<TestEvent.Unload> { event ->
            assertEquals(TestEvent.Unload, event)

            emitState(TestState.Unloaded)
            emitEffect(TestEffect.Unloaded)
        }
    }
}