package org.proninyaroslav.opencomicvine.model.paging.favorites

import androidx.paging.*
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.types.TeamInfo
import org.proninyaroslav.opencomicvine.types.TeamsResponse
import org.proninyaroslav.opencomicvine.types.FavoriteInfo
import org.proninyaroslav.opencomicvine.types.StatusCode
import org.proninyaroslav.opencomicvine.types.filter.TeamsFilter
import org.proninyaroslav.opencomicvine.types.item.favorites.FavoritesTeamItem
import org.proninyaroslav.opencomicvine.types.paging.favorites.FavoritesTeamItemRemoteKeys
import org.proninyaroslav.opencomicvine.types.paging.favorites.PagingFavoritesTeamItem
import org.proninyaroslav.opencomicvine.types.preferences.PrefFavoritesSort
import org.proninyaroslav.opencomicvine.types.preferences.PrefSortDirection
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.repo.TeamsRepository
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesListFetchResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingTeamRepository
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class TeamsRemoteMediatorTest {
    lateinit var mediator: TeamsRemoteMediator

    @MockK
    lateinit var teamsRepo: TeamsRepository

    @MockK
    lateinit var teamItemRepo: PagingTeamRepository

    @MockK
    lateinit var favoritesRepo: FavoritesRepository

    @MockK
    lateinit var pref: AppPreferences

    @MockK
    lateinit var onRefresh: () -> Unit

    val dispatcher = StandardTestDispatcher()

    val scope = TestScope(dispatcher)

    val sort = PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Desc)

    val dateAdded = Date(GregorianCalendar(2022, 0, 1).timeInMillis)

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { pref.favoriteTeamsSort } returns flowOf(sort)

        mediator =
            TeamsRemoteMediator(
                scope,
                onRefresh,
                teamsRepo,
                teamItemRepo,
                pref,
                favoritesRepo,
                dispatcher,
            )
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Load success result when more data is present`() = runTest {
        val pageSize = 10
        val response = mockk<TeamsResponse>()
        val teamsList = List(pageSize) { mockk<TeamInfo>() }
        val favoritesList = teamsList.mapIndexed { index, it ->
            every { it.id } returns index
            FavoritesTeamItem(
                info = it,
                dateAdded = dateAdded,
            )
        }
        val favoriteInfoList = List(pageSize * 2) {
            FavoriteInfo(
                id = it,
                entityId = it,
                entityType = FavoriteInfo.EntityType.Team,
                dateAdded = dateAdded,
            )
        }
        val itemsList = favoritesList.mapIndexed { i, info ->
            PagingFavoritesTeamItem(
                index = i,
                item = info,
            )
        }
        val remoteKeysList = itemsList.map { team ->
            FavoritesTeamItemRemoteKeys(
                id = team.index,
                prevOffset = null,
                nextOffset = pageSize,
            )
        }
        val pagingState = PagingState<Int, PagingFavoritesTeamItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )

        every {
            favoritesRepo.observeByType(
                entityType = FavoriteInfo.EntityType.Team,
                sort = sort,
            )
        } returns flowOf(FavoritesListFetchResult.Success(favoriteInfoList))

        every { response.statusCode } returns StatusCode.OK
        every { response.results } returns teamsList
        every { response.numberOfPageResults } returns pageSize
        every { response.numberOfTotalResults } returns favoritesList.size
        every { response.error } returns "OK"
        every { response.offset } returns 0
        every { response.limit } returns pageSize
        coEvery {
            teamsRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = null,
                filters = listOf(
                    TeamsFilter.Id(
                        favoriteInfoList.subList(0, pageSize).map { it.entityId }
                    )
                ),
            )
        } returns ComicVineResult.Success(response)
        coEvery {
            teamItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        } returns ComicVinePagingRepository.Result.Success(Unit)
        itemsList.forEach { team ->
            coEvery {
                teamItemRepo.getRemoteKeysById(team.index)
            } returns ComicVinePagingRepository.Result.Success(remoteKeysList[team.index])
        }

        scope.runCurrent()
        val result = mediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        coVerify { teamItemRepo.deleteByIdList(emptyList()) }
        verify { pref.favoriteTeamsSort }
        verify {
            favoritesRepo.observeByType(
                entityType = FavoriteInfo.EntityType.Team,
                sort = sort,
            )
        }
        verifyAll {
            response.statusCode
            response.results
            response.numberOfPageResults
            response.numberOfTotalResults
            response.error
            response.offset
            response.limit
        }
        coVerify {
            teamsRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = null,
                filters = listOf(
                    TeamsFilter.Id(
                        favoriteInfoList.subList(0, pageSize).map { it.entityId }
                    )
                ),
            )
        }
        coVerify {
            teamItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        }
        confirmVerified(teamsRepo, teamItemRepo, favoritesRepo, pref, response)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Load success result and endOfPaginationReached when no more data`() = runTest {
        val pageSize = 10
        val response = mockk<TeamsResponse>()
        val favoriteInfoList = emptyList<FavoriteInfo>()
        val itemsList = emptyList<PagingFavoritesTeamItem>()
        val pagingState = PagingState<Int, PagingFavoritesTeamItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )

        every {
            favoritesRepo.observeByType(
                entityType = FavoriteInfo.EntityType.Team,
                sort = sort,
            )
        } returns flowOf(FavoritesListFetchResult.Success(favoriteInfoList))

        itemsList.forEach { team ->
            coEvery {
                teamItemRepo.getRemoteKeysById(team.index)
            } returns ComicVinePagingRepository.Result.Success(null)
        }

        scope.runCurrent()
        val result = mediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        coVerify { teamItemRepo.deleteByIdList(emptyList()) }
        verify { pref.favoriteTeamsSort }
        verify {
            favoritesRepo.observeByType(
                entityType = FavoriteInfo.EntityType.Team,
                sort = sort,
            )
        }
        confirmVerified(teamsRepo, teamItemRepo, favoritesRepo, pref, response)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Service error`() = runTest {
        val pageSize = 10
        val response = mockk<TeamsResponse>()
        val pagingState = PagingState<Int, PagingFavoritesTeamItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )
        val favoriteInfoList = List(pageSize * 2) {
            FavoriteInfo(
                id = it,
                entityId = it,
                entityType = FavoriteInfo.EntityType.Team,
                dateAdded = dateAdded,
            )
        }

        every {
            favoritesRepo.observeByType(
                entityType = FavoriteInfo.EntityType.Team,
                sort = sort,
            )
        } returns flowOf(FavoritesListFetchResult.Success(favoriteInfoList))

        every { response.statusCode } returns StatusCode.InvalidAPIKey
        every { response.error } returns "Invalid API Key"
        coEvery {
            teamsRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = null,
                filters = listOf(
                    TeamsFilter.Id(
                        favoriteInfoList.subList(0, pageSize).map { it.entityId }
                    )
                ),
            )
        } returns ComicVineResult.Success(response)

        scope.runCurrent()
        val result = mediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Error)
        (result as RemoteMediator.MediatorResult.Error).run {
            val error = throwable as FavoritesEntityRemoteMediator.Error.Service
            assertEquals(StatusCode.InvalidAPIKey, error.statusCode)
            assertEquals("Invalid API Key", error.errorMessage)
        }

        coVerify { teamItemRepo.deleteByIdList(emptyList()) }
        verify { pref.favoriteTeamsSort }
        verify {
            favoritesRepo.observeByType(
                entityType = FavoriteInfo.EntityType.Team,
                sort = sort,
            )
        }
        verifyAll {
            response.statusCode
            response.error
        }
        coVerify {
            teamsRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = null,
                filters = listOf(
                    TeamsFilter.Id(
                        favoriteInfoList.subList(0, pageSize).map { it.entityId }
                    )
                ),
            )
        }
        confirmVerified(teamsRepo, favoritesRepo, pref, response)
    }
}