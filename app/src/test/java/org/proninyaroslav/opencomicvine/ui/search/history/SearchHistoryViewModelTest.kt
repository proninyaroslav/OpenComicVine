package org.proninyaroslav.opencomicvine.ui.search.history

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.data.SearchHistoryInfo
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.DateProvider
import org.proninyaroslav.opencomicvine.model.repo.SearchHistoryRepository
import java.io.IOException
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class SearchHistoryViewModelTest {
    lateinit var viewModel: SearchHistoryViewModel

    @MockK
    lateinit var dateProvider: DateProvider

    @MockK
    lateinit var pref: AppPreferences

    @MockK
    lateinit var searchHistoryRepo: SearchHistoryRepository

    private val historyListFlow = MutableStateFlow<SearchHistoryRepository.Result<List<SearchHistoryInfo>>>(
        SearchHistoryRepository.Result.Success(emptyList())
    )

    private val historySize = 10

    val dispatcher = StandardTestDispatcher()

    private val nowDate = Date(GregorianCalendar(2023, 0, 1).timeInMillis)

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)

        every { dateProvider.now } returns nowDate
        every { searchHistoryRepo.observeAll() } returns historyListFlow
        every { pref.searchHistorySize } returns flowOf(historySize)

        viewModel = SearchHistoryViewModel(
            searchHistoryRepo,
            dispatcher,
            dateProvider,
            pref,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Search history list`() = runTest {
        val res = SearchHistoryRepository.Result.Success(
            listOf(
                SearchHistoryInfo(
                    query = "test",
                    date = nowDate,
                )
            )
        )

        val listJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.searchHistoryList.collect()
        }

        assertEquals(
            SearchHistoryRepository.Result.Success(emptyList<SearchHistoryInfo>()),
            viewModel.searchHistoryList.value,
        )

        historyListFlow.emit(res)
        dispatcher.scheduler.runCurrent()

        assertEquals(res, viewModel.searchHistoryList.value)

        listJob.cancel()

        verify { searchHistoryRepo.observeAll() }
        confirmVerified(searchHistoryRepo)
    }

    @Test
    fun `Search history size limit`() = runTest {
        val res = SearchHistoryRepository.Result.Success(
            List(historySize * 2) {
                SearchHistoryInfo(
                    query = "test$it",
                    date = nowDate,
                )
            }
        )
        val listForDelete = res.data.subList(historySize, res.data.size)

        coEvery { searchHistoryRepo.deleteList(listForDelete) } returns
                SearchHistoryRepository.Result.Success(Unit)

        val listJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.searchHistoryList.collect()
        }

        assertEquals(
            SearchHistoryRepository.Result.Success(emptyList<SearchHistoryInfo>()),
            viewModel.searchHistoryList.value,
        )

        historyListFlow.emit(res)
        dispatcher.scheduler.runCurrent()

        assertEquals(res, viewModel.searchHistoryList.value)

        listJob.cancel()

        verify { searchHistoryRepo.observeAll() }
        verify { pref.searchHistorySize }
        coVerify { searchHistoryRepo.deleteList(listForDelete) }
        confirmVerified(searchHistoryRepo, pref)
    }

    @Test
    fun `Add to history failed`() = runTest {
        val info = SearchHistoryInfo(
            query = "test",
            date = nowDate,
        )
        val error = SearchHistoryRepository.Result.Failed.IO(IOException())

        val expectedEffects = listOf(
            SearchHistoryEffect.AddToHistoryFailed(error)
        )
        val actualEffects = mutableListOf<SearchHistoryEffect>()

        coEvery { searchHistoryRepo.insert(info) } returns error

        val effectJob = launch {
            viewModel.effect.toList(actualEffects)
        }

        viewModel.event(SearchHistoryEvent.AddToHistory(info.query))
        dispatcher.scheduler.runCurrent()

        assertEquals(expectedEffects, actualEffects)
        effectJob.cancel()

        verify { dateProvider.now }
        coVerify { searchHistoryRepo.observeAll() }
        coVerify { searchHistoryRepo.insert(info) }
        confirmVerified(searchHistoryRepo, dateProvider)
    }

    @Test
    fun `Delete to history failed`() = runTest {
        val info = SearchHistoryInfo(
            query = "test",
            date = nowDate,
        )
        val error = SearchHistoryRepository.Result.Failed.IO(IOException())

        val expectedEffects = listOf(
            SearchHistoryEffect.DeleteFromHistoryFailed(error)
        )
        val actualEffects = mutableListOf<SearchHistoryEffect>()

        coEvery { searchHistoryRepo.delete(info) } returns error

        val effectJob = launch {
            viewModel.effect.toList(actualEffects)
        }

        viewModel.event(SearchHistoryEvent.DeleteFromHistory(info))
        dispatcher.scheduler.runCurrent()

        assertEquals(expectedEffects, actualEffects)
        effectJob.cancel()

        coVerify { searchHistoryRepo.observeAll() }
        coVerify { searchHistoryRepo.delete(info) }
        confirmVerified(searchHistoryRepo)
    }

    @Test
    fun `Add to history`() = runTest {
        val info = SearchHistoryInfo(
            query = "test",
            date = nowDate,
        )

        val expectedEffects = listOf(
            SearchHistoryEffect.AddedToHistory(info)
        )
        val actualEffects = mutableListOf<SearchHistoryEffect>()

        coEvery { searchHistoryRepo.insert(info) } returns
                SearchHistoryRepository.Result.Success(Unit)

        val effectJob = launch {
            viewModel.effect.toList(actualEffects)
        }

        viewModel.event(SearchHistoryEvent.AddToHistory(info.query))
        dispatcher.scheduler.runCurrent()

        assertEquals(expectedEffects, actualEffects)
        effectJob.cancel()

        verify { dateProvider.now }
        coVerify { searchHistoryRepo.observeAll() }
        coVerify { searchHistoryRepo.insert(info) }
        confirmVerified(searchHistoryRepo, dateProvider)
    }

    @Test
    fun `Delete from history`() = runTest {
        val info = SearchHistoryInfo(
            query = "test",
            date = nowDate,
        )

        val expectedEffects = listOf(
            SearchHistoryEffect.RemovedFromHistory(info)
        )
        val actualEffects = mutableListOf<SearchHistoryEffect>()

        coEvery { searchHistoryRepo.delete(info) } returns
                SearchHistoryRepository.Result.Success(Unit)

        val effectJob = launch {
            viewModel.effect.toList(actualEffects)
        }

        viewModel.event(SearchHistoryEvent.DeleteFromHistory(info))
        dispatcher.scheduler.runCurrent()

        assertEquals(expectedEffects, actualEffects)
        effectJob.cancel()

        coVerify { searchHistoryRepo.observeAll() }
        coVerify { searchHistoryRepo.delete(info) }
        confirmVerified(searchHistoryRepo)
    }
}