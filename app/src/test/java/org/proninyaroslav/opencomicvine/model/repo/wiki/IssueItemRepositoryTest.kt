package org.proninyaroslav.opencomicvine.model.repo.wiki

import androidx.paging.PagingSource
import androidx.room.withTransaction
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.data.paging.wiki.PagingWikiIssueItem
import org.proninyaroslav.opencomicvine.data.paging.wiki.WikiIssueItemRemoteKeys
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.db.wiki.WikiIssuesDao
import org.proninyaroslav.opencomicvine.model.db.wiki.WikiIssuesRemoteKeysDao
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingIssueRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingIssueRepositoryImpl

@OptIn(ExperimentalCoroutinesApi::class)
class IssueItemRepositoryTest {
    lateinit var repo: PagingIssueRepository

    @RelaxedMockK
    lateinit var appDatabase: AppDatabase

    @MockK
    lateinit var wikiIssuesDao: WikiIssuesDao

    @MockK
    lateinit var wikiIssuesRemoteKeysDao: WikiIssuesRemoteKeysDao

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(
            "androidx.room.RoomDatabaseKt"
        )
        every { appDatabase.wikiIssuesDao() } returns wikiIssuesDao
        every { appDatabase.wikiIssuesRemoteKeysDao() } returns wikiIssuesRemoteKeysDao

        repo = PagingIssueRepositoryImpl(appDatabase)
    }

    @Test
    fun `Save issues`() = runTest {
        val issues = mockk<List<PagingWikiIssueItem>>()
        val remoteKeys = mockk<List<WikiIssueItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { wikiIssuesDao.insertList(issues) } just runs
        coEvery { wikiIssuesRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = issues,
            remoteKeys = remoteKeys,
            clearBeforeSave = false,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            wikiIssuesDao.insertList(issues)
            wikiIssuesRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(wikiIssuesDao, wikiIssuesRemoteKeysDao)
    }

    @Test
    fun `Save issues and clear old`() = runTest {
        val issues = mockk<List<PagingWikiIssueItem>>()
        val remoteKeys = mockk<List<WikiIssueItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { wikiIssuesDao.deleteAll() } just runs
        coEvery { wikiIssuesRemoteKeysDao.deleteAll() } just runs
        coEvery { wikiIssuesDao.insertList(issues) } just runs
        coEvery { wikiIssuesRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = issues,
            remoteKeys = remoteKeys,
            clearBeforeSave = true,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            wikiIssuesDao.deleteAll()
            wikiIssuesRemoteKeysDao.deleteAll()
            wikiIssuesDao.insertList(issues)
            wikiIssuesRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(wikiIssuesDao, wikiIssuesRemoteKeysDao)
    }

    @Test
    fun getAllSavedIssues() = runTest {
        val source = mockk<PagingSource<Int, PagingWikiIssueItem>>()

        coEvery { wikiIssuesDao.getAll() } returns source

        assertEquals(source, repo.getAllSavedItems())

        coVerify { wikiIssuesDao.getAll() }
        confirmVerified(wikiIssuesDao)
    }

    @Test
    fun getRemoteKeyById() = runTest {
        val id = 1
        val remoteKeys = mockk<WikiIssueItemRemoteKeys>()

        coEvery { wikiIssuesRemoteKeysDao.getById(id) } returns remoteKeys

        assertEquals(
            ComicVinePagingRepository.Result.Success(remoteKeys),
            repo.getRemoteKeysById(id)
        )

        coVerify { wikiIssuesRemoteKeysDao.getById(id) }
        confirmVerified(wikiIssuesRemoteKeysDao)
    }

    @Test
    fun getSavedIssues() = runTest {
        val source = mockk<PagingSource<Int, PagingWikiIssueItem>>()
        val count = 10

        coEvery { wikiIssuesDao.get(count) } returns source

        assertEquals(source, repo.getSavedItems(count))

        coVerify { wikiIssuesDao.get(count) }
        confirmVerified(wikiIssuesDao)
    }

    @Test
    fun getIssueById() = runTest {
        val id = 1
        val item = mockk<PagingWikiIssueItem>()

        coEvery { wikiIssuesDao.getById(id) } returns item

        assertEquals(
            ComicVinePagingRepository.Result.Success(item),
            repo.getItemById(id)
        )

        coVerify { wikiIssuesDao.getById(id) }
        confirmVerified(wikiIssuesDao)
    }
}