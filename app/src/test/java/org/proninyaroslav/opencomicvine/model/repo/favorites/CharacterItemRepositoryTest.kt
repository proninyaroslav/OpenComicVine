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
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesCharacterItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesCharacterItem
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.db.favorites.FavoritesCharactersDao
import org.proninyaroslav.opencomicvine.model.db.favorites.FavoritesCharactersRemoteKeysDao
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingCharacterRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingCharacterRepositoryImpl

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterItemRepositoryTest {
    lateinit var repo: PagingCharacterRepository

    @RelaxedMockK
    lateinit var appDatabase: AppDatabase

    @MockK
    lateinit var favoriteCharactersDao: FavoritesCharactersDao

    @MockK
    lateinit var favoriteCharactersRemoteKeysDao: FavoritesCharactersRemoteKeysDao

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(
            "androidx.room.RoomDatabaseKt"
        )
        every { appDatabase.favoritesCharactersDao() } returns favoriteCharactersDao
        every { appDatabase.favoritesCharactersRemoteKeysDao() } returns favoriteCharactersRemoteKeysDao

        repo = PagingCharacterRepositoryImpl(appDatabase)
    }

    @Test
    fun `Save characters`() = runTest {
        val characters = mockk<List<PagingFavoritesCharacterItem>>()
        val remoteKeys = mockk<List<FavoritesCharacterItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { favoriteCharactersDao.insertList(characters) } just runs
        coEvery { favoriteCharactersRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = characters,
            remoteKeys = remoteKeys,
            clearBeforeSave = false,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            favoriteCharactersDao.insertList(characters)
            favoriteCharactersRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(favoriteCharactersDao, favoriteCharactersRemoteKeysDao)
    }

    @Test
    fun `Save characters and clear old`() = runTest {
        val characters = mockk<List<PagingFavoritesCharacterItem>>()
        val remoteKeys = mockk<List<FavoritesCharacterItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { favoriteCharactersDao.deleteAll() } just runs
        coEvery { favoriteCharactersRemoteKeysDao.deleteAll() } just runs
        coEvery { favoriteCharactersDao.insertList(characters) } just runs
        coEvery { favoriteCharactersRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = characters,
            remoteKeys = remoteKeys,
            clearBeforeSave = true,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            favoriteCharactersDao.deleteAll()
            favoriteCharactersRemoteKeysDao.deleteAll()
            favoriteCharactersDao.insertList(characters)
            favoriteCharactersRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(favoriteCharactersDao, favoriteCharactersRemoteKeysDao)
    }

    @Test
    fun getAllSavedCharacters() = runTest {
        val source = mockk<PagingSource<Int, PagingFavoritesCharacterItem>>()

        coEvery { favoriteCharactersDao.getAll() } returns source

        assertEquals(source, repo.getAllSavedItems())

        coVerify { favoriteCharactersDao.getAll() }
        confirmVerified(favoriteCharactersDao)
    }

    @Test
    fun getRemoteKeyById() = runTest {
        val id = 1
        val remoteKeys = mockk<FavoritesCharacterItemRemoteKeys>()

        coEvery { favoriteCharactersRemoteKeysDao.getById(id) } returns remoteKeys

        assertEquals(
            ComicVinePagingRepository.Result.Success(remoteKeys),
            repo.getRemoteKeysById(id)
        )

        coVerify { favoriteCharactersRemoteKeysDao.getById(id) }
        confirmVerified(favoriteCharactersRemoteKeysDao)
    }

    @Test
    fun getSavedCharacters() = runTest {
        val source = mockk<PagingSource<Int, PagingFavoritesCharacterItem>>()
        val count = 10

        coEvery { favoriteCharactersDao.get(count) } returns source

        assertEquals(source, repo.getSavedItems(count))

        coVerify { favoriteCharactersDao.get(count) }
        confirmVerified(favoriteCharactersDao)
    }

    @Test
    fun getCharacterById() = runTest {
        val id = 1
        val item = mockk<PagingFavoritesCharacterItem>()

        coEvery { favoriteCharactersDao.getById(id) } returns item

        assertEquals(
            ComicVinePagingRepository.Result.Success(item),
            repo.getItemById(id)
        )

        coVerify { favoriteCharactersDao.getById(id) }
        confirmVerified(favoriteCharactersDao)
    }

    @Test
    fun `Delete characters`() = runTest {
        val idList = listOf(1, 2, 3)

        coEvery { favoriteCharactersDao.deleteList(idList) } just runs

        val res = repo.deleteByIdList(idList)
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerify { favoriteCharactersDao.deleteList(idList) }
        confirmVerified(favoriteCharactersDao)
    }
}