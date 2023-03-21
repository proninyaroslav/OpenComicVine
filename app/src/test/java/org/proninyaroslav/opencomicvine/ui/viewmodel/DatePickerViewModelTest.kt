package org.proninyaroslav.opencomicvine.ui.viewmodel

import androidx.core.util.toAndroidXPair
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class DatePickerViewModelTest {
    lateinit var viewModel: DatePickerViewModel

    val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = DatePickerViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun show() = runTest {
        val range = (
                GregorianCalendar(2022, 0, 1).timeInMillis to
                        GregorianCalendar(2022, 0, 2).timeInMillis
                ).toAndroidXPair()
        val expectedStates = listOf(
            DatePickerState.Initial,
            DatePickerState.Show(
                dialogType = 1,
                range = range,
            ),
        )
        val actualStates = mutableListOf<DatePickerState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        dispatcher.scheduler.apply {
            viewModel.event(
                DatePickerEvent.Show(
                    dialogType = 1,
                    range = range,
                )
            )
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()
    }

    @Test
    fun hide() = runTest {
        val expectedStates = listOf(
            DatePickerState.Initial,
            DatePickerState.Hide,
        )
        val actualStates = mutableListOf<DatePickerState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        dispatcher.scheduler.apply {
            viewModel.event(DatePickerEvent.Hide)
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()
    }
}