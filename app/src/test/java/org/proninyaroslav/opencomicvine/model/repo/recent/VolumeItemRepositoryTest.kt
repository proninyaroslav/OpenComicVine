package org.proninyaroslav.opencomicvine.model.repo.recent

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
import org.proninyaroslav.opencomicvine.data.paging.recent.PagingRecentVolumeItem
import org.proninyaroslav.opencomicvine.data.paging.recent.RecentVolumeItemRemoteKeys
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.db.recent.RecentVolumesDao
import org.proninyaroslav.opencomicvine.model.db.recent.RecentVolumesRemoteKeysDao
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingVolumeRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingVolumeRepositoryImpl

@OptIn(ExperimentalCoroutinesApi::class)
class VolumeItemRepositoryTest {
    lateinit var repo: PagingVolumeRepository

    @RelaxedMockK
    lateinit var appDatabase: AppDatabase

    @MockK
    lateinit var recentVolumesDao: RecentVolumesDao

    @MockK
    lateinit var recentVolumesRemoteKeysDao: RecentVolumesRemoteKeysDao

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(
            "androidx.room.RoomDatabaseKt"
        )
        every { appDatabase.recentVolumesDao() } returns recentVolumesDao
        every { appDatabase.recentVolumesRemoteKeysDao() } returns recentVolumesRemoteKeysDao

        repo = PagingVolumeRepositoryImpl(appDatabase)
    }

    @Test
    fun `Save volumes`() = runTest {
        val volumes = mockk<List<PagingRecentVolumeItem>>()
        val remoteKeys = mockk<List<RecentVolumeItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { recentVolumesDao.insertList(volumes) } just runs
        coEvery { recentVolumesRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = volumes,
            remoteKeys = remoteKeys,
            clearBeforeSave = false,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            recentVolumesDao.insertList(volumes)
            recentVolumesRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(recentVolumesDao, recentVolumesRemoteKeysDao)
    }

    @Test
    fun `Save volumes and clear old`() = runTest {
        val volumes = mockk<List<PagingRecentVolumeItem>>()
        val remoteKeys = mockk<List<RecentVolumeItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { recentVolumesDao.deleteAll() } just runs
        coEvery { recentVolumesRemoteKeysDao.deleteAll() } just runs
        coEvery { recentVolumesDao.insertList(volumes) } just runs
        coEvery { recentVolumesRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = volumes,
            remoteKeys = remoteKeys,
            clearBeforeSave = true,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            recentVolumesDao.deleteAll()
            recentVolumesRemoteKeysDao.deleteAll()
            recentVolumesDao.insertList(volumes)
            recentVolumesRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(recentVolumesDao, recentVolumesRemoteKeysDao)
    }

    @Test
    fun getAllSavedVolumes() = runTest {
        val source = mockk<PagingSource<Int, PagingRecentVolumeItem>>()

        coEvery { recentVolumesDao.getAll() } returns source

        assertEquals(source, repo.getAllSavedItems())

        coVerify { recentVolumesDao.getAll() }
        confirmVerified(recentVolumesDao)
    }

    @Test
    fun getRemoteKeyById() = runTest {
        val id = 1
        val remoteKeys = mockk<RecentVolumeItemRemoteKeys>()

        coEvery { recentVolumesRemoteKeysDao.getById(id) } returns remoteKeys

        assertEquals(
            ComicVinePagingRepository.Result.Success(remoteKeys),
            repo.getRemoteKeysById(id)
        )

        coVerify { recentVolumesRemoteKeysDao.getById(id) }
        confirmVerified(recentVolumesRemoteKeysDao)
    }

    @Test
    fun getSavedVolumes() = runTest {
        val source = mockk<PagingSource<Int, PagingRecentVolumeItem>>()
        val count = 10

        coEvery { recentVolumesDao.get(count) } returns source

        assertEquals(source, repo.getSavedItems(count))

        coVerify { recentVolumesDao.get(count) }
        confirmVerified(recentVolumesDao)
    }

    @Test
    fun getVolumeById() = runTest {
        val id = 1
        val item = mockk<PagingRecentVolumeItem>()

        coEvery { recentVolumesDao.getById(id) } returns item

        assertEquals(
            ComicVinePagingRepository.Result.Success(item),
            repo.getItemById(id)
        )

        coVerify { recentVolumesDao.getById(id) }
        confirmVerified(recentVolumesDao)
    }
}