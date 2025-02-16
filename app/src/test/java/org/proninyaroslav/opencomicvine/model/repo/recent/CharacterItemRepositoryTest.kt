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
import org.proninyaroslav.opencomicvine.model.db.recent.RecentCharactersDao
import org.proninyaroslav.opencomicvine.model.db.recent.RecentCharactersRemoteKeysDao
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingCharacterRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingCharacterRepositoryImpl
import org.proninyaroslav.opencomicvine.types.paging.recent.PagingRecentCharacterItem
import org.proninyaroslav.opencomicvine.types.paging.recent.RecentCharacterItemRemoteKeys

class CharacterItemRepositoryTest {
    lateinit var repo: PagingCharacterRepository

    @RelaxedMockK
    lateinit var appDatabase: AppDatabase

    @MockK
    lateinit var recentCharactersDao: RecentCharactersDao

    @MockK
    lateinit var recentCharactersRemoteKeysDao: RecentCharactersRemoteKeysDao

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(
            "androidx.room.RoomDatabaseKt"
        )
        every { appDatabase.recentCharactersDao() } returns recentCharactersDao
        every { appDatabase.recentCharactersRemoteKeysDao() } returns recentCharactersRemoteKeysDao

        repo = PagingCharacterRepositoryImpl(appDatabase)
    }

    @Test
    fun `Save characters`() = runTest {
        val characters = mockk<List<PagingRecentCharacterItem>>()
        val remoteKeys = mockk<List<RecentCharacterItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { recentCharactersDao.insertList(characters) } just runs
        coEvery { recentCharactersRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = characters,
            remoteKeys = remoteKeys,
            clearBeforeSave = false,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            recentCharactersDao.insertList(characters)
            recentCharactersRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(recentCharactersDao, recentCharactersRemoteKeysDao)
    }

    @Test
    fun `Save characters and clear old`() = runTest {
        val characters = mockk<List<PagingRecentCharacterItem>>()
        val remoteKeys = mockk<List<RecentCharacterItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { recentCharactersDao.deleteAll() } just runs
        coEvery { recentCharactersRemoteKeysDao.deleteAll() } just runs
        coEvery { recentCharactersDao.insertList(characters) } just runs
        coEvery { recentCharactersRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = characters,
            remoteKeys = remoteKeys,
            clearBeforeSave = true,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            recentCharactersDao.deleteAll()
            recentCharactersRemoteKeysDao.deleteAll()
            recentCharactersDao.insertList(characters)
            recentCharactersRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(recentCharactersDao, recentCharactersRemoteKeysDao)
    }

    @Test
    fun getAllSavedCharacters() = runTest {
        val source = mockk<PagingSource<Int, PagingRecentCharacterItem>>()

        coEvery { recentCharactersDao.getAll() } returns source

        assertEquals(source, repo.getAllSavedItems())

        coVerify { recentCharactersDao.getAll() }
        confirmVerified(recentCharactersDao)
    }

    @Test
    fun getRemoteKeyById() = runTest {
        val id = 1
        val remoteKeys = mockk<RecentCharacterItemRemoteKeys>()

        coEvery { recentCharactersRemoteKeysDao.getById(id) } returns remoteKeys

        assertEquals(
            ComicVinePagingRepository.Result.Success(remoteKeys),
            repo.getRemoteKeysById(id)
        )

        coVerify { recentCharactersRemoteKeysDao.getById(id) }
        confirmVerified(recentCharactersRemoteKeysDao)
    }

    @Test
    fun getSavedCharacters() = runTest {
        val source = mockk<PagingSource<Int, PagingRecentCharacterItem>>()
        val count = 10

        coEvery { recentCharactersDao.get(count) } returns source

        assertEquals(source, repo.getSavedItems(count))

        coVerify { recentCharactersDao.get(count) }
        confirmVerified(recentCharactersDao)
    }

    @Test
    fun getCharacterById() = runTest {
        val id = 1
        val item = mockk<PagingRecentCharacterItem>()

        coEvery { recentCharactersDao.getById(id) } returns item

        assertEquals(
            ComicVinePagingRepository.Result.Success(item),
            repo.getItemById(id)
        )

        coVerify { recentCharactersDao.getById(id) }
        confirmVerified(recentCharactersDao)
    }
}