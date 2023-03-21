package org.proninyaroslav.opencomicvine.ui.search

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

@OptIn(ExperimentalCoroutinesApi::class)
class SearchFilterViewModelTest {
    lateinit var viewModel: SearchFilterViewModel

    @MockK
    lateinit var pref: AppPreferences

    val dispatcher = StandardTestDispatcher()

    private val initialFilter = PrefSearchFilterBundle(
        resources = PrefSearchFilter.Resources.Unknown
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)

        every { pref.searchFilter } returns flowOf(initialFilter)
        viewModel = SearchFilterViewModel(pref)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun load() = runTest {
        val expectedStates = listOf(
            SearchFilterState.Initial,
            SearchFilterState.Loaded(
                filterBundle = initialFilter,
            ),
        )
        val actualStates = mutableListOf<SearchFilterState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }
        dispatcher.scheduler.runCurrent()

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        verify { pref.searchFilter }
        confirmVerified(pref)
    }

    @Test
    fun `Change filter`() = runTest {
        val filter = initialFilter.copy(
            resources = PrefSearchFilter.Resources.Selected(
                setOf(
                    PrefSearchResourceType.Character,
                    PrefSearchResourceType.Issue,
                )
            )
        )
        val expectedStates = listOf(
            SearchFilterState.Initial,
            SearchFilterState.Loaded(
                filterBundle = initialFilter,
            ),
            SearchFilterState.FiltersChanged(
                filterBundle = filter,
                isNeedApply = true,
            ),
        )
        val actualStates = mutableListOf<SearchFilterState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }
        dispatcher.scheduler.apply {
            viewModel.event(SearchFilterEvent.ChangeFilters(filter))
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()
    }

    @Test
    fun apply() = runTest {
        val filter = initialFilter.copy(
            resources = PrefSearchFilter.Resources.Selected(
                setOf(
                    PrefSearchResourceType.Character,
                    PrefSearchResourceType.Issue,
                )
            )
        )
        val expectedStates = listOf(
            SearchFilterState.Initial,
            SearchFilterState.Loaded(
                filterBundle = initialFilter,
            ),
            SearchFilterState.FiltersChanged(
                filterBundle = filter,
                isNeedApply = true,
            ),
            SearchFilterState.Applied(
                filterBundle = filter,
            ),
            SearchFilterState.FiltersChanged(
                filterBundle = filter,
                isNeedApply = false,
            ),
        )
        val expectedEffects = listOf(
            SearchFilterEffect.Applied,
        )
        val actualStates = mutableListOf<SearchFilterState>()
        val actualEffects = mutableListOf<SearchFilterEffect>()

        coEvery { pref.setSearchFilter(filter) } just runs

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }
        val effectJob = launch {
            viewModel.effect.toList(actualEffects)
        }
        dispatcher.scheduler.apply {
            viewModel.event(SearchFilterEvent.ChangeFilters(filter))
            runCurrent()
            viewModel.event(SearchFilterEvent.Apply)
            runCurrent()
            viewModel.event(SearchFilterEvent.ChangeFilters(filter))
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        assertEquals(expectedEffects, actualEffects)
        stateJob.cancel()
        effectJob.cancel()

        verify { pref.searchFilter }
        coVerify { pref.setSearchFilter(filter) }
        confirmVerified(pref)
    }
}