package org.proninyaroslav.opencomicvine.model.repo.recent

import androidx.paging.PagingSource
import androidx.room.withTransaction
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyAll
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.db.recent.RecentIssuesDao
import org.proninyaroslav.opencomicvine.model.db.recent.RecentIssuesRemoteKeysDao
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingIssueRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingIssueRepositoryImpl
import org.proninyaroslav.opencomicvine.types.paging.recent.PagingRecentIssueItem
import org.proninyaroslav.opencomicvine.types.paging.recent.RecentIssueItemRemoteKeys

class IssueItemRepositoryTest {
    lateinit var repo: PagingIssueRepository

    @RelaxedMockK
    lateinit var appDatabase: AppDatabase

    @MockK
    lateinit var recentIssuesDao: RecentIssuesDao

    @MockK
    lateinit var recentIssuesRemoteKeysDao: RecentIssuesRemoteKeysDao

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(
            "androidx.room.RoomDatabaseKt"
        )
        every { appDatabase.recentIssuesDao() } returns recentIssuesDao
        every { appDatabase.recentIssuesRemoteKeysDao() } returns recentIssuesRemoteKeysDao

        repo = PagingIssueRepositoryImpl(appDatabase)
    }

    @Test
    fun `Save issues`() = runTest {
        val issues = mockk<List<PagingRecentIssueItem>>()
        val remoteKeys = mockk<List<RecentIssueItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { recentIssuesDao.insertList(issues) } just runs
        coEvery { recentIssuesRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = issues,
            remoteKeys = remoteKeys,
            clearBeforeSave = false,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            recentIssuesDao.insertList(issues)
            recentIssuesRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(recentIssuesDao, recentIssuesRemoteKeysDao)
    }

    @Test
    fun `Save issues and clear old`() = runTest {
        val issues = mockk<List<PagingRecentIssueItem>>()
        val remoteKeys = mockk<List<RecentIssueItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { recentIssuesDao.deleteAll() } just runs
        coEvery { recentIssuesRemoteKeysDao.deleteAll() } just runs
        coEvery { recentIssuesDao.insertList(issues) } just runs
        coEvery { recentIssuesRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = issues,
            remoteKeys = remoteKeys,
            clearBeforeSave = true,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            recentIssuesDao.deleteAll()
            recentIssuesRemoteKeysDao.deleteAll()
            recentIssuesDao.insertList(issues)
            recentIssuesRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(recentIssuesDao, recentIssuesRemoteKeysDao)
    }

    @Test
    fun getAllSavedIssues() = runTest {
        val source = mockk<PagingSource<Int, PagingRecentIssueItem>>()

        coEvery { recentIssuesDao.getAll() } returns source

        assertEquals(source, repo.getAllSavedItems())

        coVerify { recentIssuesDao.getAll() }
        confirmVerified(recentIssuesDao)
    }

    @Test
    fun getRemoteKeyById() = runTest {
        val id = 1
        val remoteKeys = mockk<RecentIssueItemRemoteKeys>()

        coEvery { recentIssuesRemoteKeysDao.getById(id) } returns remoteKeys

        assertEquals(
            ComicVinePagingRepository.Result.Success(remoteKeys),
            repo.getRemoteKeysById(id)
        )

        coVerify { recentIssuesRemoteKeysDao.getById(id) }
        confirmVerified(recentIssuesRemoteKeysDao)
    }

    @Test
    fun getSavedIssues() = runTest {
        val source = mockk<PagingSource<Int, PagingRecentIssueItem>>()
        val count = 10

        coEvery { recentIssuesDao.get(count) } returns source

        assertEquals(source, repo.getSavedItems(count))

        coVerify { recentIssuesDao.get(count) }
        confirmVerified(recentIssuesDao)
    }

    @Test
    fun getIssueById() = runTest {
        val id = 1
        val item = mockk<PagingRecentIssueItem>()

        coEvery { recentIssuesDao.getById(id) } returns item

        assertEquals(
            ComicVinePagingRepository.Result.Success(item),
            repo.getItemById(id)
        )

        coVerify { recentIssuesDao.getById(id) }
        confirmVerified(recentIssuesDao)
    }
}