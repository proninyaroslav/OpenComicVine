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
import org.proninyaroslav.opencomicvine.types.paging.wiki.PagingWikiCharacterItem
import org.proninyaroslav.opencomicvine.types.paging.wiki.WikiCharacterItemRemoteKeys
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.db.wiki.WikiCharactersDao
import org.proninyaroslav.opencomicvine.model.db.wiki.WikiCharactersRemoteKeysDao
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingCharacterRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingCharacterRepositoryImpl

class CharacterItemRepositoryTest {
    lateinit var repo: PagingCharacterRepository

    @RelaxedMockK
    lateinit var appDatabase: AppDatabase

    @MockK
    lateinit var wikiCharactersDao: WikiCharactersDao

    @MockK
    lateinit var wikiCharactersRemoteKeysDao: WikiCharactersRemoteKeysDao

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(
            "androidx.room.RoomDatabaseKt"
        )
        every { appDatabase.wikiCharactersDao() } returns wikiCharactersDao
        every { appDatabase.wikiCharactersRemoteKeysDao() } returns wikiCharactersRemoteKeysDao

        repo = PagingCharacterRepositoryImpl(appDatabase)
    }

    @Test
    fun `Save characters`() = runTest {
        val characters = mockk<List<PagingWikiCharacterItem>>()
        val remoteKeys = mockk<List<WikiCharacterItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { wikiCharactersDao.insertList(characters) } just runs
        coEvery { wikiCharactersRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = characters,
            remoteKeys = remoteKeys,
            clearBeforeSave = false,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            wikiCharactersDao.insertList(characters)
            wikiCharactersRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(wikiCharactersDao, wikiCharactersRemoteKeysDao)
    }

    @Test
    fun `Save characters and clear old`() = runTest {
        val characters = mockk<List<PagingWikiCharacterItem>>()
        val remoteKeys = mockk<List<WikiCharacterItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { wikiCharactersDao.deleteAll() } just runs
        coEvery { wikiCharactersRemoteKeysDao.deleteAll() } just runs
        coEvery { wikiCharactersDao.insertList(characters) } just runs
        coEvery { wikiCharactersRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = characters,
            remoteKeys = remoteKeys,
            clearBeforeSave = true,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            wikiCharactersDao.deleteAll()
            wikiCharactersRemoteKeysDao.deleteAll()
            wikiCharactersDao.insertList(characters)
            wikiCharactersRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(wikiCharactersDao, wikiCharactersRemoteKeysDao)
    }

    @Test
    fun getAllSavedCharacters() = runTest {
        val source = mockk<PagingSource<Int, PagingWikiCharacterItem>>()

        coEvery { wikiCharactersDao.getAll() } returns source

        assertEquals(source, repo.getAllSavedItems())

        coVerify { wikiCharactersDao.getAll() }
        confirmVerified(wikiCharactersDao)
    }

    @Test
    fun getRemoteKeyById() = runTest {
        val id = 1
        val remoteKeys = mockk<WikiCharacterItemRemoteKeys>()

        coEvery { wikiCharactersRemoteKeysDao.getById(id) } returns remoteKeys

        assertEquals(
            ComicVinePagingRepository.Result.Success(remoteKeys),
            repo.getRemoteKeysById(id)
        )

        coVerify { wikiCharactersRemoteKeysDao.getById(id) }
        confirmVerified(wikiCharactersRemoteKeysDao)
    }

    @Test
    fun getSavedCharacters() = runTest {
        val source = mockk<PagingSource<Int, PagingWikiCharacterItem>>()
        val count = 10

        coEvery { wikiCharactersDao.get(count) } returns source

        assertEquals(source, repo.getSavedItems(count))

        coVerify { wikiCharactersDao.get(count) }
        confirmVerified(wikiCharactersDao)
    }

    @Test
    fun getCharacterById() = runTest {
        val id = 1
        val item = mockk<PagingWikiCharacterItem>()

        coEvery { wikiCharactersDao.getById(id) } returns item

        assertEquals(
            ComicVinePagingRepository.Result.Success(item),
            repo.getItemById(id)
        )

        coVerify { wikiCharactersDao.getById(id) }
        confirmVerified(wikiCharactersDao)
    }
}