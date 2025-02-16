package org.proninyaroslav.opencomicvine.model.repo

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.types.SearchHistoryInfo
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.db.SearchHistoryDao
import java.util.*

class SearchHistoryRepositoryTest {
    lateinit var repo: SearchHistoryRepository

    @RelaxedMockK
    lateinit var appDatabase: AppDatabase

    @MockK
    lateinit var searchHistoryDao: SearchHistoryDao

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(
            "androidx.room.RoomDatabaseKt"
        )
        every { appDatabase.searchHistoryDao() } returns searchHistoryDao

        repo = SearchHistoryRepositoryImpl(appDatabase)
    }

    @Test
    fun getByQuery() = runTest {
        val info = SearchHistoryInfo(
            query = "test",
            date = Calendar.getInstance().time,
        )

        coEvery { searchHistoryDao.getByQuery(info.query) } returns info

        assertEquals(
            SearchHistoryRepository.Result.Success(info),
            repo.getByQuery(info.query)
        )

        coVerify { searchHistoryDao.getByQuery(info.query) }
        confirmVerified(searchHistoryDao)
    }

    @Test
    fun observeAll() = runTest {
        val infoList = listOf(
            SearchHistoryInfo(
                query = "test",
                date = Calendar.getInstance().time,
            )
        )

        every { searchHistoryDao.observeAll() } returns flowOf(infoList)

        assertEquals(
            SearchHistoryRepository.Result.Success(infoList),
            repo.observeAll().first()
        )

        verify { searchHistoryDao.observeAll() }
        confirmVerified(searchHistoryDao)
    }

    @Test
    fun delete() = runTest {
        val info = SearchHistoryInfo(
            query = "test",
            date = Calendar.getInstance().time,
        )

        coEvery { searchHistoryDao.delete(info) } just runs

        assertEquals(
            SearchHistoryRepository.Result.Success(Unit),
            repo.delete(info)
        )

        coVerify { searchHistoryDao.delete(info) }
        confirmVerified(searchHistoryDao)
    }

    @Test
    fun deleteList() = runTest {
        val infoList = listOf(
            SearchHistoryInfo(
                query = "test",
                date = Calendar.getInstance().time,
            )
        )

        coEvery { searchHistoryDao.deleteList(infoList) } just runs

        assertEquals(
            SearchHistoryRepository.Result.Success(Unit),
            repo.deleteList(infoList)
        )

        coVerify { searchHistoryDao.deleteList(infoList) }
        confirmVerified(searchHistoryDao)
    }

    @Test
    fun insert() = runTest {
        val info = SearchHistoryInfo(
            query = "test",
            date = Calendar.getInstance().time,
        )

        coEvery { searchHistoryDao.insert(info) } just runs

        assertEquals(
            SearchHistoryRepository.Result.Success(Unit),
            repo.insert(info)
        )

        coVerify { searchHistoryDao.insert(info) }
        confirmVerified(searchHistoryDao)
    }
}