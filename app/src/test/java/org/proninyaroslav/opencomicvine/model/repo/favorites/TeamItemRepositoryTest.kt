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
import org.proninyaroslav.opencomicvine.types.paging.favorites.FavoritesTeamItemRemoteKeys
import org.proninyaroslav.opencomicvine.types.paging.favorites.PagingFavoritesTeamItem
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.db.favorites.FavoritesTeamsDao
import org.proninyaroslav.opencomicvine.model.db.favorites.FavoritesTeamsRemoteKeysDao
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingTeamRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingTeamRepositoryImpl

class TeamItemRepositoryTest {
    lateinit var repo: PagingTeamRepository

    @RelaxedMockK
    lateinit var appDatabase: AppDatabase

    @MockK
    lateinit var favoriteTeamsDao: FavoritesTeamsDao

    @MockK
    lateinit var favoriteTeamsRemoteKeysDao: FavoritesTeamsRemoteKeysDao

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(
            "androidx.room.RoomDatabaseKt"
        )
        every { appDatabase.favoritesTeamsDao() } returns favoriteTeamsDao
        every { appDatabase.favoritesTeamsRemoteKeysDao() } returns favoriteTeamsRemoteKeysDao

        repo = PagingTeamRepositoryImpl(appDatabase)
    }

    @Test
    fun `Save teams`() = runTest {
        val teams = mockk<List<PagingFavoritesTeamItem>>()
        val remoteKeys = mockk<List<FavoritesTeamItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { favoriteTeamsDao.insertList(teams) } just runs
        coEvery { favoriteTeamsRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = teams,
            remoteKeys = remoteKeys,
            clearBeforeSave = false,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            favoriteTeamsDao.insertList(teams)
            favoriteTeamsRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(favoriteTeamsDao, favoriteTeamsRemoteKeysDao)
    }

    @Test
    fun `Save teams and clear old`() = runTest {
        val teams = mockk<List<PagingFavoritesTeamItem>>()
        val remoteKeys = mockk<List<FavoritesTeamItemRemoteKeys>>()

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { appDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { favoriteTeamsDao.deleteAll() } just runs
        coEvery { favoriteTeamsRemoteKeysDao.deleteAll() } just runs
        coEvery { favoriteTeamsDao.insertList(teams) } just runs
        coEvery { favoriteTeamsRemoteKeysDao.insertList(remoteKeys) } just runs

        val res = repo.saveItems(
            items = teams,
            remoteKeys = remoteKeys,
            clearBeforeSave = true,
        )
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerifyAll {
            favoriteTeamsDao.deleteAll()
            favoriteTeamsRemoteKeysDao.deleteAll()
            favoriteTeamsDao.insertList(teams)
            favoriteTeamsRemoteKeysDao.insertList(remoteKeys)
        }
        confirmVerified(favoriteTeamsDao, favoriteTeamsRemoteKeysDao)
    }

    @Test
    fun getAllSavedTeams() = runTest {
        val source = mockk<PagingSource<Int, PagingFavoritesTeamItem>>()

        coEvery { favoriteTeamsDao.getAll() } returns source

        assertEquals(source, repo.getAllSavedItems())

        coVerify { favoriteTeamsDao.getAll() }
        confirmVerified(favoriteTeamsDao)
    }

    @Test
    fun getRemoteKeyById() = runTest {
        val id = 1
        val remoteKeys = mockk<FavoritesTeamItemRemoteKeys>()

        coEvery { favoriteTeamsRemoteKeysDao.getById(id) } returns remoteKeys

        assertEquals(
            ComicVinePagingRepository.Result.Success(remoteKeys),
            repo.getRemoteKeysById(id)
        )

        coVerify { favoriteTeamsRemoteKeysDao.getById(id) }
        confirmVerified(favoriteTeamsRemoteKeysDao)
    }

    @Test
    fun getSavedTeams() = runTest {
        val source = mockk<PagingSource<Int, PagingFavoritesTeamItem>>()
        val count = 10

        coEvery { favoriteTeamsDao.get(count) } returns source

        assertEquals(source, repo.getSavedItems(count))

        coVerify { favoriteTeamsDao.get(count) }
        confirmVerified(favoriteTeamsDao)
    }

    @Test
    fun getTeamById() = runTest {
        val id = 1
        val item = mockk<PagingFavoritesTeamItem>()

        coEvery { favoriteTeamsDao.getById(id) } returns item

        assertEquals(
            ComicVinePagingRepository.Result.Success(item),
            repo.getItemById(id)
        )

        coVerify { favoriteTeamsDao.getById(id) }
        confirmVerified(favoriteTeamsDao)
    }

    @Test
    fun `Delete teams`() = runTest {
        val idList = listOf(1, 2, 3)

        coEvery { favoriteTeamsDao.deleteList(idList) } just runs

        val res = repo.deleteByIdList(idList)
        assertEquals(ComicVinePagingRepository.Result.Success(Unit), res)

        coVerify { favoriteTeamsDao.deleteList(idList) }
        confirmVerified(favoriteTeamsDao)
    }
}