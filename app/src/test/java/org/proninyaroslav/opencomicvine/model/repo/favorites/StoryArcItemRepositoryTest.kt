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
import org.proninyaroslav.opencomicvine.types.paging.favorites.FavoritesStoryArcItemRemoteKeys
import org.proninyaroslav.opencomicvine.types.paging.favorites.PagingFavoritesStoryArcItem
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.db.favorites.FavoritesStoryArcsDao
import org.proninyaroslav.opencomicvine.model.db.favorites.FavoritesStoryArcsRemoteKeysDao
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingStoryArcRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingStoryArcRepositoryImpl

@OptIn(ExperimentalCoroutinesApi::class)
class StoryArcItemRepositoryTest {
    lateinit var repo: PagingStoryArcRepository

    @RelaxedMockK
    lateinit var appDatabase: AppDatabase

    @MockK
    lateinit var favoriteStoryArcsDao: FavoritesStoryArcsDao

    @MockK
    lateinit var favoriteStoryArcsRemoteKeysDao: FavoritesStoryArcsRemoteKeysDao

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(
            "androidx.room.RoomDatabaseKt"
        )
        every { appDatabase.favoritesStoryArcsDao() } returns favoriteStoryArcsDao
        every { appDatabase.favoritesStoryArcsRemoteKeysDao() } returns favoriteStoryArcsRemoteKeysDao

        repo = PagingStoryArcRepositoryImpl(appDatabase)
    }

    @Test
    fun `Save story arcs`() = runTest {
        val storyArcs = mockk<List<PagingFavoritesStoryArcItem>>()
        val remoteKeys = mockk<List<FavoritesStoryArcItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { favoriteStoryArcsDao.insertList(storyArcs) } just runs
        coEvery { favoriteStoryArcsRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = storyArcs,
            remoteKeys = remoteKeys,
            clearBeforeSave = false,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            favoriteStoryArcsDao.insertList(storyArcs)
            favoriteStoryArcsRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(favoriteStoryArcsDao, favoriteStoryArcsRemoteKeysDao)
    }

    @Test
    fun `Save story arcs and clear old`() = runTest {
        val storyArcs = mockk<List<PagingFavoritesStoryArcItem>>()
        val remoteKeys = mockk<List<FavoritesStoryArcItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { favoriteStoryArcsDao.deleteAll() } just runs
        coEvery { favoriteStoryArcsRemoteKeysDao.deleteAll() } just runs
        coEvery { favoriteStoryArcsDao.insertList(storyArcs) } just runs
        coEvery { favoriteStoryArcsRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = storyArcs,
            remoteKeys = remoteKeys,
            clearBeforeSave = true,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            favoriteStoryArcsDao.deleteAll()
            favoriteStoryArcsRemoteKeysDao.deleteAll()
            favoriteStoryArcsDao.insertList(storyArcs)
            favoriteStoryArcsRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(favoriteStoryArcsDao, favoriteStoryArcsRemoteKeysDao)
    }

    @Test
    fun getAllSavedStoryArcs() = runTest {
        val source = mockk<PagingSource<Int, PagingFavoritesStoryArcItem>>()

        coEvery { favoriteStoryArcsDao.getAll() } returns source

        assertEquals(source, repo.getAllSavedItems())

        coVerify { favoriteStoryArcsDao.getAll() }
        confirmVerified(favoriteStoryArcsDao)
    }

    @Test
    fun getRemoteKeyById() = runTest {
        val id = 1
        val remoteKeys = mockk<FavoritesStoryArcItemRemoteKeys>()

        coEvery { favoriteStoryArcsRemoteKeysDao.getById(id) } returns remoteKeys

        assertEquals(
            ComicVinePagingRepository.Result.Success(remoteKeys),
            repo.getRemoteKeysById(id)
        )

        coVerify { favoriteStoryArcsRemoteKeysDao.getById(id) }
        confirmVerified(favoriteStoryArcsRemoteKeysDao)
    }

    @Test
    fun getSavedStoryArcs() = runTest {
        val source = mockk<PagingSource<Int, PagingFavoritesStoryArcItem>>()
        val count = 10

        coEvery { favoriteStoryArcsDao.get(count) } returns source

        assertEquals(source, repo.getSavedItems(count))

        coVerify { favoriteStoryArcsDao.get(count) }
        confirmVerified(favoriteStoryArcsDao)
    }

    @Test
    fun getStoryArcById() = runTest {
        val id = 1
        val item = mockk<PagingFavoritesStoryArcItem>()

        coEvery { favoriteStoryArcsDao.getById(id) } returns item

        assertEquals(
            ComicVinePagingRepository.Result.Success(item),
            repo.getItemById(id)
        )

        coVerify { favoriteStoryArcsDao.getById(id) }
        confirmVerified(favoriteStoryArcsDao)
    }

    @Test
    fun `Delete story arcs`() = runTest {
        val idList = listOf(1, 2, 3)

        coEvery { favoriteStoryArcsDao.deleteList(idList) } just runs

        val res = repo.deleteByIdList(idList)
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerify { favoriteStoryArcsDao.deleteList(idList) }
        confirmVerified(favoriteStoryArcsDao)
    }
}