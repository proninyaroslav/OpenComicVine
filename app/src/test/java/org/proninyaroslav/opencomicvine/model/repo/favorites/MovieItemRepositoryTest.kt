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
import org.proninyaroslav.opencomicvine.types.paging.favorites.FavoritesMovieItemRemoteKeys
import org.proninyaroslav.opencomicvine.types.paging.favorites.PagingFavoritesMovieItem
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.db.favorites.FavoritesMoviesDao
import org.proninyaroslav.opencomicvine.model.db.favorites.FavoritesMoviesRemoteKeysDao
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingMovieRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingMovieRepositoryImpl

@OptIn(ExperimentalCoroutinesApi::class)
class MovieItemRepositoryTest {
    lateinit var repo: PagingMovieRepository

    @RelaxedMockK
    lateinit var appDatabase: AppDatabase

    @MockK
    lateinit var favoriteMoviesDao: FavoritesMoviesDao

    @MockK
    lateinit var favoriteMoviesRemoteKeysDao: FavoritesMoviesRemoteKeysDao

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(
            "androidx.room.RoomDatabaseKt"
        )
        every { appDatabase.favoritesMoviesDao() } returns favoriteMoviesDao
        every { appDatabase.favoritesMoviesRemoteKeysDao() } returns favoriteMoviesRemoteKeysDao

        repo = PagingMovieRepositoryImpl(appDatabase)
    }

    @Test
    fun `Save movies`() = runTest {
        val movies = mockk<List<PagingFavoritesMovieItem>>()
        val remoteKeys = mockk<List<FavoritesMovieItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { favoriteMoviesDao.insertList(movies) } just runs
        coEvery { favoriteMoviesRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = movies,
            remoteKeys = remoteKeys,
            clearBeforeSave = false,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            favoriteMoviesDao.insertList(movies)
            favoriteMoviesRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(favoriteMoviesDao, favoriteMoviesRemoteKeysDao)
    }

    @Test
    fun `Save movies and clear old`() = runTest {
        val movies = mockk<List<PagingFavoritesMovieItem>>()
        val remoteKeys = mockk<List<FavoritesMovieItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { favoriteMoviesDao.deleteAll() } just runs
        coEvery { favoriteMoviesRemoteKeysDao.deleteAll() } just runs
        coEvery { favoriteMoviesDao.insertList(movies) } just runs
        coEvery { favoriteMoviesRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = movies,
            remoteKeys = remoteKeys,
            clearBeforeSave = true,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            favoriteMoviesDao.deleteAll()
            favoriteMoviesRemoteKeysDao.deleteAll()
            favoriteMoviesDao.insertList(movies)
            favoriteMoviesRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(favoriteMoviesDao, favoriteMoviesRemoteKeysDao)
    }

    @Test
    fun getAllSavedMovies() = runTest {
        val source = mockk<PagingSource<Int, PagingFavoritesMovieItem>>()

        coEvery { favoriteMoviesDao.getAll() } returns source

        assertEquals(source, repo.getAllSavedItems())

        coVerify { favoriteMoviesDao.getAll() }
        confirmVerified(favoriteMoviesDao)
    }

    @Test
    fun getRemoteKeyById() = runTest {
        val id = 1
        val remoteKeys = mockk<FavoritesMovieItemRemoteKeys>()

        coEvery { favoriteMoviesRemoteKeysDao.getById(id) } returns remoteKeys

        assertEquals(
            ComicVinePagingRepository.Result.Success(remoteKeys),
            repo.getRemoteKeysById(id)
        )

        coVerify { favoriteMoviesRemoteKeysDao.getById(id) }
        confirmVerified(favoriteMoviesRemoteKeysDao)
    }

    @Test
    fun getSavedMovies() = runTest {
        val source = mockk<PagingSource<Int, PagingFavoritesMovieItem>>()
        val count = 10

        coEvery { favoriteMoviesDao.get(count) } returns source

        assertEquals(source, repo.getSavedItems(count))

        coVerify { favoriteMoviesDao.get(count) }
        confirmVerified(favoriteMoviesDao)
    }

    @Test
    fun getMovieById() = runTest {
        val id = 1
        val item = mockk<PagingFavoritesMovieItem>()

        coEvery { favoriteMoviesDao.getById(id) } returns item

        assertEquals(
            ComicVinePagingRepository.Result.Success(item),
            repo.getItemById(id)
        )

        coVerify { favoriteMoviesDao.getById(id) }
        confirmVerified(favoriteMoviesDao)
    }

    @Test
    fun `Delete movies`() = runTest {
        val idList = listOf(1, 2, 3)

        coEvery { favoriteMoviesDao.deleteList(idList) } just runs

        val res = repo.deleteByIdList(idList)
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerify { favoriteMoviesDao.deleteList(idList) }
        confirmVerified(favoriteMoviesDao)
    }
}