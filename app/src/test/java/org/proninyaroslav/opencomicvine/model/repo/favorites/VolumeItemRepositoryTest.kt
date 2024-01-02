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
import org.proninyaroslav.opencomicvine.types.paging.favorites.FavoritesVolumeItemRemoteKeys
import org.proninyaroslav.opencomicvine.types.paging.favorites.PagingFavoritesVolumeItem
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.db.favorites.FavoritesVolumesDao
import org.proninyaroslav.opencomicvine.model.db.favorites.FavoritesVolumesRemoteKeysDao
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingVolumeRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingVolumeRepositoryImpl

@OptIn(ExperimentalCoroutinesApi::class)
class VolumeItemRepositoryTest {
    lateinit var repo: PagingVolumeRepository

    @RelaxedMockK
    lateinit var appDatabase: AppDatabase

    @MockK
    lateinit var favoriteVolumesDao: FavoritesVolumesDao

    @MockK
    lateinit var favoriteVolumesRemoteKeysDao: FavoritesVolumesRemoteKeysDao

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(
            "androidx.room.RoomDatabaseKt"
        )
        every { appDatabase.favoritesVolumesDao() } returns favoriteVolumesDao
        every { appDatabase.favoritesVolumesRemoteKeysDao() } returns favoriteVolumesRemoteKeysDao

        repo = PagingVolumeRepositoryImpl(appDatabase)
    }

    @Test
    fun `Save volumes`() = runTest {
        val volumes = mockk<List<PagingFavoritesVolumeItem>>()
        val remoteKeys = mockk<List<FavoritesVolumeItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { favoriteVolumesDao.insertList(volumes) } just runs
        coEvery { favoriteVolumesRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = volumes,
            remoteKeys = remoteKeys,
            clearBeforeSave = false,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            favoriteVolumesDao.insertList(volumes)
            favoriteVolumesRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(favoriteVolumesDao, favoriteVolumesRemoteKeysDao)
    }

    @Test
    fun `Save volumes and clear old`() = runTest {
        val volumes = mockk<List<PagingFavoritesVolumeItem>>()
        val remoteKeys = mockk<List<FavoritesVolumeItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { favoriteVolumesDao.deleteAll() } just runs
        coEvery { favoriteVolumesRemoteKeysDao.deleteAll() } just runs
        coEvery { favoriteVolumesDao.insertList(volumes) } just runs
        coEvery { favoriteVolumesRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = volumes,
            remoteKeys = remoteKeys,
            clearBeforeSave = true,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            favoriteVolumesDao.deleteAll()
            favoriteVolumesRemoteKeysDao.deleteAll()
            favoriteVolumesDao.insertList(volumes)
            favoriteVolumesRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(favoriteVolumesDao, favoriteVolumesRemoteKeysDao)
    }

    @Test
    fun getAllSavedVolumes() = runTest {
        val source = mockk<PagingSource<Int, PagingFavoritesVolumeItem>>()

        coEvery { favoriteVolumesDao.getAll() } returns source

        assertEquals(source, repo.getAllSavedItems())

        coVerify { favoriteVolumesDao.getAll() }
        confirmVerified(favoriteVolumesDao)
    }

    @Test
    fun getRemoteKeyById() = runTest {
        val id = 1
        val remoteKeys = mockk<FavoritesVolumeItemRemoteKeys>()

        coEvery { favoriteVolumesRemoteKeysDao.getById(id) } returns remoteKeys

        assertEquals(
            ComicVinePagingRepository.Result.Success(remoteKeys),
            repo.getRemoteKeysById(id)
        )

        coVerify { favoriteVolumesRemoteKeysDao.getById(id) }
        confirmVerified(favoriteVolumesRemoteKeysDao)
    }

    @Test
    fun getSavedVolumes() = runTest {
        val source = mockk<PagingSource<Int, PagingFavoritesVolumeItem>>()
        val count = 10

        coEvery { favoriteVolumesDao.get(count) } returns source

        assertEquals(source, repo.getSavedItems(count))

        coVerify { favoriteVolumesDao.get(count) }
        confirmVerified(favoriteVolumesDao)
    }

    @Test
    fun getVolumeById() = runTest {
        val id = 1
        val item = mockk<PagingFavoritesVolumeItem>()

        coEvery { favoriteVolumesDao.getById(id) } returns item

        assertEquals(
            ComicVinePagingRepository.Result.Success(item),
            repo.getItemById(id)
        )

        coVerify { favoriteVolumesDao.getById(id) }
        confirmVerified(favoriteVolumesDao)
    }

    @Test
    fun `Delete volumes`() = runTest {
        val idList = listOf(1, 2, 3)

        coEvery { favoriteVolumesDao.deleteList(idList) } just runs

        val res = repo.deleteByIdList(idList)
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerify { favoriteVolumesDao.deleteList(idList) }
        confirmVerified(favoriteVolumesDao)
    }
}