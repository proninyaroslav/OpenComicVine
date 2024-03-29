package org.proninyaroslav.opencomicvine.ui.home.filter

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.getDaysOfCurrentWeek
import org.proninyaroslav.opencomicvine.types.preferences.PrefRecentVolumesFilter
import org.proninyaroslav.opencomicvine.types.preferences.PrefRecentVolumesFilterBundle
import org.proninyaroslav.opencomicvine.ui.home.category.filter.VolumesFilterState
import org.proninyaroslav.opencomicvine.ui.home.category.filter.VolumesFilterViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class VolumesFilterViewModelTest {
    lateinit var viewModel: VolumesFilterViewModel

    @MockK
    lateinit var pref: AppPreferences

    val dispatcher = StandardTestDispatcher()

    private val initialFilter = PrefRecentVolumesFilterBundle(
        dateAdded = PrefRecentVolumesFilter.DateAdded.Unknown,
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)

        every { pref.recentVolumesFilters } returns flowOf(initialFilter)
        viewModel = VolumesFilterViewModel(pref)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun load() = runTest {
        val expectedStates = listOf(
            VolumesFilterState.Loaded(
                filterBundle = initialFilter,
            ),
        )
        val actualStates = mutableListOf<VolumesFilterState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }
        dispatcher.scheduler.runCurrent()

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        verify { pref.recentVolumesFilters }
        confirmVerified(pref)
    }

    @Test
    fun `Change filter`() = runTest {
        val filter = initialFilter.copy(
            dateAdded = getDaysOfCurrentWeek().run {
                PrefRecentVolumesFilter.DateAdded.InRange(
                    start = first,
                    end = second,
                )
            },
        )
        val expectedStates = listOf(
            VolumesFilterState.Loaded(
                filterBundle = initialFilter,
            ),
            VolumesFilterState.FiltersChanged(
                filterBundle = filter,
                isNeedApply = true,
            ),
        )
        val actualStates = mutableListOf<VolumesFilterState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }
        dispatcher.scheduler.apply {
            runCurrent()
            viewModel.changeFilters(filter)
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()
    }

    @Test
    fun apply() = runTest {
        val filter = initialFilter.copy(
            getDaysOfCurrentWeek().run {
                PrefRecentVolumesFilter.DateAdded.InRange(
                    start = first,
                    end = second,
                )
            },
        )
        val expectedStates = listOf(
            VolumesFilterState.Loaded(
                filterBundle = initialFilter,
            ),
            VolumesFilterState.FiltersChanged(
                filterBundle = filter,
                isNeedApply = true,
            ),
            VolumesFilterState.Applied(
                filterBundle = filter,
            ),
            VolumesFilterState.FiltersChanged(
                filterBundle = filter,
                isNeedApply = false,
            ),
        )
        val actualStates = mutableListOf<VolumesFilterState>()

        coEvery { pref.setRecentVolumesFilters(filter) } just runs

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }
        dispatcher.scheduler.apply {
            runCurrent()
            viewModel.changeFilters(filter)
            runCurrent()
            viewModel.apply()
            runCurrent()
            viewModel.changeFilters(filter)
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        verify { pref.recentVolumesFilters }
        coVerify { pref.setRecentVolumesFilters(filter) }
        confirmVerified(pref)
    }
}