package org.proninyaroslav.opencomicvine.model.repo.favorites

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
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesIssueItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesIssueItem
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.db.favorites.FavoritesIssuesDao
import org.proninyaroslav.opencomicvine.model.db.favorites.FavoritesIssuesRemoteKeysDao
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingIssueRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingIssueRepositoryImpl

@OptIn(ExperimentalCoroutinesApi::class)
class IssueItemRepositoryTest {
    lateinit var repo: PagingIssueRepository

    @RelaxedMockK
    lateinit var appDatabase: AppDatabase

    @MockK
    lateinit var favoriteIssuesDao: FavoritesIssuesDao

    @MockK
    lateinit var favoriteIssuesRemoteKeysDao: FavoritesIssuesRemoteKeysDao

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(
            "androidx.room.RoomDatabaseKt"
        )
        every { appDatabase.favoritesIssuesDao() } returns favoriteIssuesDao
        every { appDatabase.favoritesIssuesRemoteKeysDao() } returns favoriteIssuesRemoteKeysDao

        repo = PagingIssueRepositoryImpl(appDatabase)
    }

    @Test
    fun `Save issues`() = runTest {
        val issues = mockk<List<PagingFavoritesIssueItem>>()
        val remoteKeys = mockk<List<FavoritesIssueItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { favoriteIssuesDao.insertList(issues) } just runs
        coEvery { favoriteIssuesRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = issues,
            remoteKeys = remoteKeys,
            clearBeforeSave = false,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            favoriteIssuesDao.insertList(issues)
            favoriteIssuesRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(favoriteIssuesDao, favoriteIssuesRemoteKeysDao)
    }

    @Test
    fun `Save issues and clear old`() = runTest {
        val issues = mockk<List<PagingFavoritesIssueItem>>()
        val remoteKeys = mockk<List<FavoritesIssueItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { favoriteIssuesDao.deleteAll() } just runs
        coEvery { favoriteIssuesRemoteKeysDao.deleteAll() } just runs
        coEvery { favoriteIssuesDao.insertList(issues) } just runs
        coEvery { favoriteIssuesRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = issues,
            remoteKeys = remoteKeys,
            clearBeforeSave = true,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            favoriteIssuesDao.deleteAll()
            favoriteIssuesRemoteKeysDao.deleteAll()
            favoriteIssuesDao.insertList(issues)
            favoriteIssuesRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(favoriteIssuesDao, favoriteIssuesRemoteKeysDao)
    }

    @Test
    fun getAllSavedIssues() = runTest {
        val source = mockk<PagingSource<Int, PagingFavoritesIssueItem>>()

        coEvery { favoriteIssuesDao.getAll() } returns source

        assertEquals(source, repo.getAllSavedItems())

        coVerify { favoriteIssuesDao.getAll() }
        confirmVerified(favoriteIssuesDao)
    }

    @Test
    fun getRemoteKeyById() = runTest {
        val id = 1
        val remoteKeys = mockk<FavoritesIssueItemRemoteKeys>()

        coEvery { favoriteIssuesRemoteKeysDao.getById(id) } returns remoteKeys

        assertEquals(
            ComicVinePagingRepository.Result.Success(remoteKeys),
            repo.getRemoteKeysById(id)
        )

        coVerify { favoriteIssuesRemoteKeysDao.getById(id) }
        confirmVerified(favoriteIssuesRemoteKeysDao)
    }

    @Test
    fun getSavedIssues() = runTest {
        val source = mockk<PagingSource<Int, PagingFavoritesIssueItem>>()
        val count = 10

        coEvery { favoriteIssuesDao.get(count) } returns source

        assertEquals(source, repo.getSavedItems(count))

        coVerify { favoriteIssuesDao.get(count) }
        confirmVerified(favoriteIssuesDao)
    }

    @Test
    fun getIssueById() = runTest {
        val id = 1
        val item = mockk<PagingFavoritesIssueItem>()

        coEvery { favoriteIssuesDao.getById(id) } returns item

        assertEquals(
            ComicVinePagingRepository.Result.Success(item),
            repo.getItemById(id)
        )

        coVerify { favoriteIssuesDao.getById(id) }
        confirmVerified(favoriteIssuesDao)
    }

    @Test
    fun `Delete issues`() = runTest {
        val idList = listOf(1, 2, 3)

        coEvery { favoriteIssuesDao.deleteList(idList) } just runs

        val res = repo.deleteByIdList(idList)
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerify { favoriteIssuesDao.deleteList(idList) }
        confirmVerified(favoriteIssuesDao)
    }
}