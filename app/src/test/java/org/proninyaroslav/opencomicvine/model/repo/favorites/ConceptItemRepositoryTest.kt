package org.proninyaroslav.opencomicvine.model.repo.favorites

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
import org.proninyaroslav.opencomicvine.model.db.favorites.FavoritesConceptsDao
import org.proninyaroslav.opencomicvine.model.db.favorites.FavoritesConceptsRemoteKeysDao
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingConceptRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingConceptRepositoryImpl
import org.proninyaroslav.opencomicvine.types.paging.favorites.FavoritesConceptItemRemoteKeys
import org.proninyaroslav.opencomicvine.types.paging.favorites.PagingFavoritesConceptItem

class ConceptItemRepositoryTest {
    lateinit var repo: PagingConceptRepository

    @RelaxedMockK
    lateinit var appDatabase: AppDatabase

    @MockK
    lateinit var favoriteConceptsDao: FavoritesConceptsDao

    @MockK
    lateinit var favoriteConceptsRemoteKeysDao: FavoritesConceptsRemoteKeysDao

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(
            "androidx.room.RoomDatabaseKt"
        )
        every { appDatabase.favoritesConceptsDao() } returns favoriteConceptsDao
        every { appDatabase.favoritesConceptsRemoteKeysDao() } returns favoriteConceptsRemoteKeysDao

        repo = PagingConceptRepositoryImpl(appDatabase)
    }

    @Test
    fun `Save concepts`() = runTest {
        val concepts = mockk<List<PagingFavoritesConceptItem>>()
        val remoteKeys = mockk<List<FavoritesConceptItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { favoriteConceptsDao.insertList(concepts) } just runs
        coEvery { favoriteConceptsRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = concepts,
            remoteKeys = remoteKeys,
            clearBeforeSave = false,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            favoriteConceptsDao.insertList(concepts)
            favoriteConceptsRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(favoriteConceptsDao, favoriteConceptsRemoteKeysDao)
    }

    @Test
    fun `Save concepts and clear old`() = runTest {
        val concepts = mockk<List<PagingFavoritesConceptItem>>()
        val remoteKeys = mockk<List<FavoritesConceptItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { favoriteConceptsDao.deleteAll() } just runs
        coEvery { favoriteConceptsRemoteKeysDao.deleteAll() } just runs
        coEvery { favoriteConceptsDao.insertList(concepts) } just runs
        coEvery { favoriteConceptsRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = concepts,
            remoteKeys = remoteKeys,
            clearBeforeSave = true,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            favoriteConceptsDao.deleteAll()
            favoriteConceptsRemoteKeysDao.deleteAll()
            favoriteConceptsDao.insertList(concepts)
            favoriteConceptsRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(favoriteConceptsDao, favoriteConceptsRemoteKeysDao)
    }

    @Test
    fun getAllSavedConcepts() = runTest {
        val source = mockk<PagingSource<Int, PagingFavoritesConceptItem>>()

        coEvery { favoriteConceptsDao.getAll() } returns source

        assertEquals(source, repo.getAllSavedItems())

        coVerify { favoriteConceptsDao.getAll() }
        confirmVerified(favoriteConceptsDao)
    }

    @Test
    fun getRemoteKeyById() = runTest {
        val id = 1
        val remoteKeys = mockk<FavoritesConceptItemRemoteKeys>()

        coEvery { favoriteConceptsRemoteKeysDao.getById(id) } returns remoteKeys

        assertEquals(
            ComicVinePagingRepository.Result.Success(remoteKeys),
            repo.getRemoteKeysById(id)
        )

        coVerify { favoriteConceptsRemoteKeysDao.getById(id) }
        confirmVerified(favoriteConceptsRemoteKeysDao)
    }

    @Test
    fun getSavedConcepts() = runTest {
        val source = mockk<PagingSource<Int, PagingFavoritesConceptItem>>()
        val count = 10

        coEvery { favoriteConceptsDao.get(count) } returns source

        assertEquals(source, repo.getSavedItems(count))

        coVerify { favoriteConceptsDao.get(count) }
        confirmVerified(favoriteConceptsDao)
    }

    @Test
    fun getConceptById() = runTest {
        val id = 1
        val item = mockk<PagingFavoritesConceptItem>()

        coEvery { favoriteConceptsDao.getById(id) } returns item

        assertEquals(
            ComicVinePagingRepository.Result.Success(item),
            repo.getItemById(id)
        )

        coVerify { favoriteConceptsDao.getById(id) }
        confirmVerified(favoriteConceptsDao)
    }

    @Test
    fun `Delete concepts`() = runTest {
        val idList = listOf(1, 2, 3)

        coEvery { favoriteConceptsDao.deleteList(idList) } just runs

        val res = repo.deleteByIdList(idList)
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerify { favoriteConceptsDao.deleteList(idList) }
        confirmVerified(favoriteConceptsDao)
    }
}