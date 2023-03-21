package org.proninyaroslav.opencomicvine.ui.home.filter

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.data.preferences.*
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.getDaysOfCurrentWeek
import org.proninyaroslav.opencomicvine.ui.home.category.filter.CharactersFilterEffect
import org.proninyaroslav.opencomicvine.ui.home.category.filter.CharactersFilterEvent
import org.proninyaroslav.opencomicvine.ui.home.category.filter.CharactersFilterState
import org.proninyaroslav.opencomicvine.ui.home.category.filter.CharactersFilterViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class CharactersFilterViewModelTest {
    lateinit var viewModel: CharactersFilterViewModel

    @MockK
    lateinit var pref: AppPreferences

    val dispatcher = StandardTestDispatcher()

    private val initialFilter = PrefRecentCharactersFilterBundle(
        dateAdded = PrefRecentCharactersFilter.DateAdded.Unknown,
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)

        every { pref.recentCharactersFilters } returns flowOf(initialFilter)
        viewModel = CharactersFilterViewModel(pref)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun load() = runTest {
        val expectedStates = listOf(
            CharactersFilterState.Initial,
            CharactersFilterState.Loaded(
                filterBundle = initialFilter,
            ),
        )
        val actualStates = mutableListOf<CharactersFilterState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }
        dispatcher.scheduler.runCurrent()

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        verify { pref.recentCharactersFilters }
        confirmVerified(pref)
    }

    @Test
    fun `Change filter`() = runTest {
        val filter = initialFilter.copy(
            dateAdded = getDaysOfCurrentWeek().run {
                PrefRecentCharactersFilter.DateAdded.InRange(
                    start = first,
                    end = second,
                )
            },
        )
        val expectedStates = listOf(
            CharactersFilterState.Initial,
            CharactersFilterState.Loaded(
                filterBundle = initialFilter,
            ),
            CharactersFilterState.FiltersChanged(
                filterBundle = filter,
                isNeedApply = true,
            ),
        )
        val actualStates = mutableListOf<CharactersFilterState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }
        dispatcher.scheduler.apply {
            viewModel.event(CharactersFilterEvent.ChangeFilters(filter))
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()
    }

    @Test
    fun apply() = runTest {
        val filter = initialFilter.copy(
            getDaysOfCurrentWeek().run {
                PrefRecentCharactersFilter.DateAdded.InRange(
                    start = first,
                    end = second,
                )
            },
        )
        val expectedStates = listOf(
            CharactersFilterState.Initial,
            CharactersFilterState.Loaded(
                filterBundle = initialFilter,
            ),
            CharactersFilterState.FiltersChanged(
                filterBundle = filter,
                isNeedApply = true,
            ),
            CharactersFilterState.Applied(
                filterBundle = filter,
            ),
            CharactersFilterState.FiltersChanged(
                filterBundle = filter,
                isNeedApply = false,
            ),
        )
        val expectedEffects = listOf(
            CharactersFilterEffect.Applied,
        )
        val actualStates = mutableListOf<CharactersFilterState>()
        val actualEffects = mutableListOf<CharactersFilterEffect>()

        coEvery { pref.setRecentCharactersFilters(filter) } just runs

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }
        val effectJob = launch {
            viewModel.effect.toList(actualEffects)
        }
        dispatcher.scheduler.apply {
            viewModel.event(CharactersFilterEvent.ChangeFilters(filter))
            runCurrent()
            viewModel.event(CharactersFilterEvent.Apply)
            runCurrent()
            viewModel.event(CharactersFilterEvent.ChangeFilters(filter))
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        assertEquals(expectedEffects, actualEffects)
        stateJob.cancel()
        effectJob.cancel()

        verify { pref.recentCharactersFilters }
        coVerify { pref.setRecentCharactersFilters(filter) }
        confirmVerified(pref)
    }
}