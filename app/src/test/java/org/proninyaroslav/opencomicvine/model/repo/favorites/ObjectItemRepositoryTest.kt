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
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesObjectItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesObjectItem
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.db.favorites.FavoritesObjectsDao
import org.proninyaroslav.opencomicvine.model.db.favorites.FavoritesObjectsRemoteKeysDao
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingObjectRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingObjectRepositoryImpl

@OptIn(ExperimentalCoroutinesApi::class)
class ObjectItemRepositoryTest {
    lateinit var repo: PagingObjectRepository

    @RelaxedMockK
    lateinit var appDatabase: AppDatabase

    @MockK
    lateinit var favoriteObjectsDao: FavoritesObjectsDao

    @MockK
    lateinit var favoriteObjectsRemoteKeysDao: FavoritesObjectsRemoteKeysDao

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(
            "androidx.room.RoomDatabaseKt"
        )
        every { appDatabase.favoritesObjectsDao() } returns favoriteObjectsDao
        every { appDatabase.favoritesObjectsRemoteKeysDao() } returns favoriteObjectsRemoteKeysDao

        repo = PagingObjectRepositoryImpl(appDatabase)
    }

    @Test
    fun `Save objects`() = runTest {
        val objects = mockk<List<PagingFavoritesObjectItem>>()
        val remoteKeys = mockk<List<FavoritesObjectItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { favoriteObjectsDao.insertList(objects) } just runs
        coEvery { favoriteObjectsRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = objects,
            remoteKeys = remoteKeys,
            clearBeforeSave = false,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            favoriteObjectsDao.insertList(objects)
            favoriteObjectsRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(favoriteObjectsDao, favoriteObjectsRemoteKeysDao)
    }

    @Test
    fun `Save objects and clear old`() = runTest {
        val objects = mockk<List<PagingFavoritesObjectItem>>()
        val remoteKeys = mockk<List<FavoritesObjectItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { favoriteObjectsDao.deleteAll() } just runs
        coEvery { favoriteObjectsRemoteKeysDao.deleteAll() } just runs
        coEvery { favoriteObjectsDao.insertList(objects) } just runs
        coEvery { favoriteObjectsRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = objects,
            remoteKeys = remoteKeys,
            clearBeforeSave = true,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            favoriteObjectsDao.deleteAll()
            favoriteObjectsRemoteKeysDao.deleteAll()
            favoriteObjectsDao.insertList(objects)
            favoriteObjectsRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(favoriteObjectsDao, favoriteObjectsRemoteKeysDao)
    }

    @Test
    fun getAllSavedObjects() = runTest {
        val source = mockk<PagingSource<Int, PagingFavoritesObjectItem>>()

        coEvery { favoriteObjectsDao.getAll() } returns source

        assertEquals(source, repo.getAllSavedItems())

        coVerify { favoriteObjectsDao.getAll() }
        confirmVerified(favoriteObjectsDao)
    }

    @Test
    fun getRemoteKeyById() = runTest {
        val id = 1
        val remoteKeys = mockk<FavoritesObjectItemRemoteKeys>()

        coEvery { favoriteObjectsRemoteKeysDao.getById(id) } returns remoteKeys

        assertEquals(
            ComicVinePagingRepository.Result.Success(remoteKeys),
            repo.getRemoteKeysById(id)
        )

        coVerify { favoriteObjectsRemoteKeysDao.getById(id) }
        confirmVerified(favoriteObjectsRemoteKeysDao)
    }

    @Test
    fun getSavedObjects() = runTest {
        val source = mockk<PagingSource<Int, PagingFavoritesObjectItem>>()
        val count = 10

        coEvery { favoriteObjectsDao.get(count) } returns source

        assertEquals(source, repo.getSavedItems(count))

        coVerify { favoriteObjectsDao.get(count) }
        confirmVerified(favoriteObjectsDao)
    }

    @Test
    fun getObjectById() = runTest {
        val id = 1
        val item = mockk<PagingFavoritesObjectItem>()

        coEvery { favoriteObjectsDao.getById(id) } returns item

        assertEquals(
            ComicVinePagingRepository.Result.Success(item),
            repo.getItemById(id)
        )

        coVerify { favoriteObjectsDao.getById(id) }
        confirmVerified(favoriteObjectsDao)
    }

    @Test
    fun `Delete objects`() = runTest {
        val idList = listOf(1, 2, 3)

        coEvery { favoriteObjectsDao.deleteList(idList) } just runs

        val res = repo.deleteByIdList(idList)
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerify { favoriteObjectsDao.deleteList(idList) }
        confirmVerified(favoriteObjectsDao)
    }
}