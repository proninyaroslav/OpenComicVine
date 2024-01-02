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
import org.proninyaroslav.opencomicvine.types.paging.wiki.PagingWikiVolumeItem
import org.proninyaroslav.opencomicvine.types.paging.wiki.WikiVolumeItemRemoteKeys
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.db.wiki.WikiVolumesDao
import org.proninyaroslav.opencomicvine.model.db.wiki.WikiVolumesRemoteKeysDao
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingVolumeRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingVolumeRepositoryImpl

@OptIn(ExperimentalCoroutinesApi::class)
class VolumeItemRepositoryTest {
    lateinit var repo: PagingVolumeRepository

    @RelaxedMockK
    lateinit var appDatabase: AppDatabase

    @MockK
    lateinit var wikiVolumesDao: WikiVolumesDao

    @MockK
    lateinit var wikiVolumesRemoteKeysDao: WikiVolumesRemoteKeysDao

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(
            "androidx.room.RoomDatabaseKt"
        )
        every { appDatabase.wikiVolumesDao() } returns wikiVolumesDao
        every { appDatabase.wikiVolumesRemoteKeysDao() } returns wikiVolumesRemoteKeysDao

        repo = PagingVolumeRepositoryImpl(appDatabase)
    }

    @Test
    fun `Save volumes`() = runTest {
        val volumes = mockk<List<PagingWikiVolumeItem>>()
        val remoteKeys = mockk<List<WikiVolumeItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { wikiVolumesDao.insertList(volumes) } just runs
        coEvery { wikiVolumesRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = volumes,
            remoteKeys = remoteKeys,
            clearBeforeSave = false,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            wikiVolumesDao.insertList(volumes)
            wikiVolumesRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(wikiVolumesDao, wikiVolumesRemoteKeysDao)
    }

    @Test
    fun `Save volumes and clear old`() = runTest {
        val volumes = mockk<List<PagingWikiVolumeItem>>()
        val remoteKeys = mockk<List<WikiVolumeItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { wikiVolumesDao.deleteAll() } just runs
        coEvery { wikiVolumesRemoteKeysDao.deleteAll() } just runs
        coEvery { wikiVolumesDao.insertList(volumes) } just runs
        coEvery { wikiVolumesRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = volumes,
            remoteKeys = remoteKeys,
            clearBeforeSave = true,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            wikiVolumesDao.deleteAll()
            wikiVolumesRemoteKeysDao.deleteAll()
            wikiVolumesDao.insertList(volumes)
            wikiVolumesRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(wikiVolumesDao, wikiVolumesRemoteKeysDao)
    }

    @Test
    fun getAllSavedVolumes() = runTest {
        val source = mockk<PagingSource<Int, PagingWikiVolumeItem>>()

        coEvery { wikiVolumesDao.getAll() } returns source

        assertEquals(source, repo.getAllSavedItems())

        coVerify { wikiVolumesDao.getAll() }
        confirmVerified(wikiVolumesDao)
    }

    @Test
    fun getRemoteKeyById() = runTest {
        val id = 1
        val remoteKeys = mockk<WikiVolumeItemRemoteKeys>()

        coEvery { wikiVolumesRemoteKeysDao.getById(id) } returns remoteKeys

        assertEquals(
            ComicVinePagingRepository.Result.Success(remoteKeys),
            repo.getRemoteKeysById(id)
        )

        coVerify { wikiVolumesRemoteKeysDao.getById(id) }
        confirmVerified(wikiVolumesRemoteKeysDao)
    }


    @Test
    fun getSavedVolumes() = runTest {
        val source = mockk<PagingSource<Int, PagingWikiVolumeItem>>()
        val count = 10

        coEvery { wikiVolumesDao.get(count) } returns source

        assertEquals(source, repo.getSavedItems(count))

        coVerify { wikiVolumesDao.get(count) }
        confirmVerified(wikiVolumesDao)
    }

    @Test
    fun getVolumeById() = runTest {
        val id = 1
        val item = mockk<PagingWikiVolumeItem>()

        coEvery { wikiVolumesDao.getById(id) } returns item

        assertEquals(
            ComicVinePagingRepository.Result.Success(item),
            repo.getItemById(id)
        )

        coVerify { wikiVolumesDao.getById(id) }
        confirmVerified(wikiVolumesDao)
    }
}