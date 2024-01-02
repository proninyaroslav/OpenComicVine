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
import org.proninyaroslav.opencomicvine.types.paging.favorites.FavoritesLocationItemRemoteKeys
import org.proninyaroslav.opencomicvine.types.paging.favorites.PagingFavoritesLocationItem
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.db.favorites.FavoritesLocationsDao
import org.proninyaroslav.opencomicvine.model.db.favorites.FavoritesLocationsRemoteKeysDao
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingLocationRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingLocationRepositoryImpl

@OptIn(ExperimentalCoroutinesApi::class)
class LocationItemRepositoryTest {
    lateinit var repo: PagingLocationRepository

    @RelaxedMockK
    lateinit var appDatabase: AppDatabase

    @MockK
    lateinit var favoriteLocationsDao: FavoritesLocationsDao

    @MockK
    lateinit var favoriteLocationsRemoteKeysDao: FavoritesLocationsRemoteKeysDao

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(
            "androidx.room.RoomDatabaseKt"
        )
        every { appDatabase.favoritesLocationsDao() } returns favoriteLocationsDao
        every { appDatabase.favoritesLocationsRemoteKeysDao() } returns favoriteLocationsRemoteKeysDao

        repo = PagingLocationRepositoryImpl(appDatabase)
    }

    @Test
    fun `Save locations`() = runTest {
        val locations = mockk<List<PagingFavoritesLocationItem>>()
        val remoteKeys = mockk<List<FavoritesLocationItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { favoriteLocationsDao.insertList(locations) } just runs
        coEvery { favoriteLocationsRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = locations,
            remoteKeys = remoteKeys,
            clearBeforeSave = false,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            favoriteLocationsDao.insertList(locations)
            favoriteLocationsRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(favoriteLocationsDao, favoriteLocationsRemoteKeysDao)
    }

    @Test
    fun `Save locations and clear old`() = runTest {
        val locations = mockk<List<PagingFavoritesLocationItem>>()
        val remoteKeys = mockk<List<FavoritesLocationItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { favoriteLocationsDao.deleteAll() } just runs
        coEvery { favoriteLocationsRemoteKeysDao.deleteAll() } just runs
        coEvery { favoriteLocationsDao.insertList(locations) } just runs
        coEvery { favoriteLocationsRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = locations,
            remoteKeys = remoteKeys,
            clearBeforeSave = true,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            favoriteLocationsDao.deleteAll()
            favoriteLocationsRemoteKeysDao.deleteAll()
            favoriteLocationsDao.insertList(locations)
            favoriteLocationsRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(favoriteLocationsDao, favoriteLocationsRemoteKeysDao)
    }

    @Test
    fun getAllSavedLocations() = runTest {
        val source = mockk<PagingSource<Int, PagingFavoritesLocationItem>>()

        coEvery { favoriteLocationsDao.getAll() } returns source

        assertEquals(source, repo.getAllSavedItems())

        coVerify { favoriteLocationsDao.getAll() }
        confirmVerified(favoriteLocationsDao)
    }

    @Test
    fun getRemoteKeyById() = runTest {
        val id = 1
        val remoteKeys = mockk<FavoritesLocationItemRemoteKeys>()

        coEvery { favoriteLocationsRemoteKeysDao.getById(id) } returns remoteKeys

        assertEquals(
            ComicVinePagingRepository.Result.Success(remoteKeys),
            repo.getRemoteKeysById(id)
        )

        coVerify { favoriteLocationsRemoteKeysDao.getById(id) }
        confirmVerified(favoriteLocationsRemoteKeysDao)
    }

    @Test
    fun getSavedLocations() = runTest {
        val source = mockk<PagingSource<Int, PagingFavoritesLocationItem>>()
        val count = 10

        coEvery { favoriteLocationsDao.get(count) } returns source

        assertEquals(source, repo.getSavedItems(count))

        coVerify { favoriteLocationsDao.get(count) }
        confirmVerified(favoriteLocationsDao)
    }

    @Test
    fun getLocationById() = runTest {
        val id = 1
        val item = mockk<PagingFavoritesLocationItem>()

        coEvery { favoriteLocationsDao.getById(id) } returns item

        assertEquals(
            ComicVinePagingRepository.Result.Success(item),
            repo.getItemById(id)
        )

        coVerify { favoriteLocationsDao.getById(id) }
        confirmVerified(favoriteLocationsDao)
    }

    @Test
    fun `Delete locations`() = runTest {
        val idList = listOf(1, 2, 3)

        coEvery { favoriteLocationsDao.deleteList(idList) } just runs

        val res = repo.deleteByIdList(idList)
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerify { favoriteLocationsDao.deleteList(idList) }
        confirmVerified(favoriteLocationsDao)
    }
}