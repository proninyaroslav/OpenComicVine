package org.proninyaroslav.opencomicvine.ui.theme

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.data.preferences.PrefTheme
import org.proninyaroslav.opencomicvine.model.AppPreferences

@OptIn(ExperimentalCoroutinesApi::class)
class ThemeViewModelTest {
    lateinit var viewModel: ThemeViewModel

    @MockK
    lateinit var pref: AppPreferences

    val dispatcher = StandardTestDispatcher()

    private val themeFlow = MutableStateFlow<PrefTheme>(PrefTheme.Unknown)

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)

        every { pref.theme } returns themeFlow

        viewModel = ThemeViewModel(pref)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun theme() = runTest {
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.theme.collect()
        }

        themeFlow.emit(PrefTheme.System)
        dispatcher.scheduler.runCurrent()

        assertEquals(PrefTheme.System, viewModel.theme.value)
        job.cancel()
    }
}