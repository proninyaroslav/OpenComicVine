package org.proninyaroslav.opencomicvine.ui.settings

import io.mockk.*
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
import org.proninyaroslav.opencomicvine.data.ErrorReportInfo
import org.proninyaroslav.opencomicvine.data.preferences.PrefTheme
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.repo.ApiKeyRepository
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    lateinit var viewModel: SettingsViewModel

    @MockK
    lateinit var apiKeyRepo: ApiKeyRepository

    @MockK
    lateinit var pref: AppPreferences

    val dispatcher = StandardTestDispatcher()

    private val apiKeyFlow = MutableStateFlow<ApiKeyRepository.GetResult<String>>(
        ApiKeyRepository.GetResult.Failed.NoApiKey
    )

    private val themeFlow = MutableStateFlow<PrefTheme>(PrefTheme.Unknown)

    private val searchHistorySizeFlow = MutableStateFlow(0)

    @MockK
    lateinit var errorReportService: ErrorReportService

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)

        every { apiKeyRepo.get() } returns apiKeyFlow
        every { pref.theme } returns themeFlow
        every { pref.searchHistorySize } returns searchHistorySizeFlow

        viewModel = SettingsViewModel(apiKeyRepo, pref, errorReportService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun load() = runTest {
        val apiKey = "key"
        val theme = PrefTheme.System
        val searchHistorySize = 10

        val expectedStates = listOf(
            SettingsState.Initial,
            SettingsState.Loaded(
                apiKey = apiKey,
                theme = theme,
                searchHistorySize = searchHistorySize
            )
        )
        val actualStates = mutableListOf<SettingsState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        dispatcher.scheduler.run {
            apiKeyFlow.emit(ApiKeyRepository.GetResult.Success(apiKey))
            themeFlow.emit(theme)
            searchHistorySizeFlow.emit(searchHistorySize)
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        verify { apiKeyRepo.get() }
        verify { pref.theme }
        verify { pref.searchHistorySize }
        confirmVerified(pref, apiKeyRepo)
    }

    @Test
    fun `Change API key`() = runTest {
        val apiKey = "key"

        val expectedEffects = listOf(
            SettingsEffect.ApiKeyChanged,
        )
        val actualEffects = mutableListOf<SettingsEffect>()

        val effectJob = launch {
            viewModel.effect.toList(actualEffects)
        }

        coEvery { apiKeyRepo.set(apiKey) } returns ApiKeyRepository.SaveResult.Success(Unit)

        dispatcher.scheduler.run {
            viewModel.event(SettingsEvent.ChangeApiKey(apiKey))
            runCurrent()
        }

        assertEquals(expectedEffects, actualEffects)
        effectJob.cancel()

        verify { apiKeyRepo.get() }
        verify { pref.theme }
        verify { pref.searchHistorySize }
        coVerify { apiKeyRepo.set(apiKey) }
        confirmVerified(pref, apiKeyRepo)
    }

    @Test
    fun `Change API key failed`() = runTest {
        val apiKey = "key"
        val error = ApiKeyRepository.SaveResult.Failed.IO(IOException())

        val expectedEffects = listOf(
            SettingsEffect.ChangeApiKeyFailed.SaveError(error),
        )
        val actualEffects = mutableListOf<SettingsEffect>()

        val effectJob = launch {
            viewModel.effect.toList(actualEffects)
        }

        coEvery { apiKeyRepo.set(apiKey) } returns error

        dispatcher.scheduler.run {
            viewModel.event(SettingsEvent.ChangeApiKey(apiKey))
            runCurrent()
        }

        assertEquals(expectedEffects, actualEffects)
        effectJob.cancel()

        verify { apiKeyRepo.get() }
        verify { pref.theme }
        verify { pref.searchHistorySize }
        coVerify { apiKeyRepo.set(apiKey) }
        confirmVerified(pref, apiKeyRepo)
    }

    @Test
    fun `Empty API key`() = runTest {
        val apiKey = "key"
        val theme = PrefTheme.System
        val searchHistorySize = 10

        val expectedStates = listOf(
            SettingsState.Initial,
            SettingsState.Loaded(
                apiKey = apiKey,
                theme = theme,
                searchHistorySize = searchHistorySize
            ),
            SettingsState.ChangeApiKeyFailed.EmptyKey(
                apiKey = "",
                theme = theme,
                searchHistorySize = searchHistorySize,
            ),
        )
        val actualStates = mutableListOf<SettingsState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        dispatcher.scheduler.run {
            apiKeyFlow.emit(ApiKeyRepository.GetResult.Success(apiKey))
            themeFlow.emit(theme)
            searchHistorySizeFlow.emit(searchHistorySize)
            runCurrent()

            viewModel.event(SettingsEvent.ChangeApiKey(""))
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        verify { apiKeyRepo.get() }
        verify { pref.theme }
        verify { pref.searchHistorySize }
        confirmVerified(pref, apiKeyRepo)
    }

    @Test
    fun `Change theme`() = runTest {
        val theme = PrefTheme.System

        val expectedEffects = listOf(
            SettingsEffect.ThemeChanged,
        )
        val actualEffects = mutableListOf<SettingsEffect>()

        val effectJob = launch {
            viewModel.effect.toList(actualEffects)
        }

        coEvery { pref.setTheme(theme) } just runs

        dispatcher.scheduler.run {
            viewModel.event(SettingsEvent.ChangeTheme(theme))
            runCurrent()
        }

        assertEquals(expectedEffects, actualEffects)
        effectJob.cancel()

        verify { apiKeyRepo.get() }
        verify { pref.theme }
        verify { pref.searchHistorySize }
        coVerify { pref.setTheme(theme) }
        confirmVerified(pref, apiKeyRepo)
    }

    @Test
    fun `Change search history size`() = runTest {
        val searchHistorySize = 10

        val expectedEffects = listOf(
            SettingsEffect.SearchHistorySizeChanged,
        )
        val actualEffects = mutableListOf<SettingsEffect>()

        val effectJob = launch {
            viewModel.effect.toList(actualEffects)
        }

        coEvery { pref.setSearchHistorySize(searchHistorySize) } just runs

        dispatcher.scheduler.run {
            viewModel.event(SettingsEvent.ChangeSearchHistorySize(searchHistorySize))
            runCurrent()
        }

        assertEquals(expectedEffects, actualEffects)
        effectJob.cancel()

        verify { apiKeyRepo.get() }
        verify { pref.theme }
        verify { pref.searchHistorySize }
        coVerify { pref.setSearchHistorySize(searchHistorySize) }
        confirmVerified(pref, apiKeyRepo)
    }

    @Test
    fun `Error report`() {
        val info = ErrorReportInfo(
            error = IOException(),
            comment = "comment",
        )

        every { errorReportService.report(info) } just runs

        viewModel.event(SettingsEvent.ErrorReport(info))
        dispatcher.scheduler.runCurrent()

        verify { errorReportService.report(info) }
        confirmVerified(errorReportService)
    }
}