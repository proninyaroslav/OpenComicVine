package org.proninyaroslav.opencomicvine.model.paging.favorites

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesListFetchResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.StoryArcsRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingStoryArcRepository
import org.proninyaroslav.opencomicvine.types.FavoriteInfo
import org.proninyaroslav.opencomicvine.types.StatusCode
import org.proninyaroslav.opencomicvine.types.StoryArcInfo
import org.proninyaroslav.opencomicvine.types.StoryArcsResponse
import org.proninyaroslav.opencomicvine.types.filter.StoryArcsFilter
import org.proninyaroslav.opencomicvine.types.item.favorites.FavoritesStoryArcItem
import org.proninyaroslav.opencomicvine.types.paging.favorites.FavoritesStoryArcItemRemoteKeys
import org.proninyaroslav.opencomicvine.types.paging.favorites.PagingFavoritesStoryArcItem
import org.proninyaroslav.opencomicvine.types.preferences.PrefFavoritesSort
import org.proninyaroslav.opencomicvine.types.preferences.PrefSortDirection
import java.util.Date
import java.util.GregorianCalendar

@OptIn(ExperimentalCoroutinesApi::class)
class StoryArcsRemoteMediatorTest {
    lateinit var mediator: StoryArcsRemoteMediator

    @MockK
    lateinit var storyArcsRepo: StoryArcsRepository

    @MockK
    lateinit var storyArcItemRepo: PagingStoryArcRepository

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

        every { pref.favoriteStoryArcsSort } returns flowOf(sort)

        mediator =
            StoryArcsRemoteMediator(
                scope,
                onRefresh,
                storyArcsRepo,
                storyArcItemRepo,
                pref,
                favoritesRepo,
                dispatcher,
            )
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Load success result when more data is present`() = runTest {
        val pageSize = 10
        val response = mockk<StoryArcsResponse>()
        val storyArcsList = List(pageSize) { mockk<StoryArcInfo>() }
        val favoritesList = storyArcsList.mapIndexed { index, it ->
            every { it.id } returns index
            FavoritesStoryArcItem(
                info = it,
                dateAdded = dateAdded,
            )
        }
        val favoriteInfoList = List(pageSize * 2) {
            FavoriteInfo(
                id = it,
                entityId = it,
                entityType = FavoriteInfo.EntityType.StoryArc,
                dateAdded = dateAdded,
            )
        }
        val itemsList = favoritesList.mapIndexed { i, info ->
            PagingFavoritesStoryArcItem(
                index = i,
                item = info,
            )
        }
        val remoteKeysList = itemsList.map { storyArc ->
            FavoritesStoryArcItemRemoteKeys(
                id = storyArc.index,
                prevOffset = null,
                nextOffset = pageSize,
            )
        }
        val pagingState = PagingState<Int, PagingFavoritesStoryArcItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )

        every {
            favoritesRepo.observeByType(
                entityType = FavoriteInfo.EntityType.StoryArc,
                sort = sort,
            )
        } returns flowOf(FavoritesListFetchResult.Success(favoriteInfoList))

        every { response.statusCode } returns StatusCode.OK
        every { response.results } returns storyArcsList
        every { response.numberOfPageResults } returns pageSize
        every { response.numberOfTotalResults } returns favoritesList.size
        every { response.error } returns "OK"
        every { response.offset } returns 0
        every { response.limit } returns pageSize
        coEvery {
            storyArcsRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = null,
                filters = listOf(
                    StoryArcsFilter.Id(
                        favoriteInfoList.subList(0, pageSize).map { it.entityId }
                    )
                ),
            )
        } returns ComicVineResult.Success(response)
        coEvery {
            storyArcItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        } returns ComicVinePagingRepository.Result.Success(Unit)
        itemsList.forEach { storyArc ->
            coEvery {
                storyArcItemRepo.getRemoteKeysById(storyArc.index)
            } returns ComicVinePagingRepository.Result.Success(remoteKeysList[storyArc.index])
        }

        scope.runCurrent()
        val result = mediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        coVerify { storyArcItemRepo.deleteByIdList(emptyList()) }
        verify { pref.favoriteStoryArcsSort }
        verify {
            favoritesRepo.observeByType(
                entityType = FavoriteInfo.EntityType.StoryArc,
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
            storyArcsRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = null,
                filters = listOf(
                    StoryArcsFilter.Id(
                        favoriteInfoList.subList(0, pageSize).map { it.entityId }
                    )
                ),
            )
        }
        coVerify {
            storyArcItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        }
        verify { storyArcItemRepo.toString() }
        confirmVerified(storyArcsRepo, storyArcItemRepo, favoritesRepo, pref, response)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Load success result and endOfPaginationReached when no more data`() = runTest {
        val pageSize = 10
        val response = mockk<StoryArcsResponse>()
        val favoriteInfoList = emptyList<FavoriteInfo>()
        val itemsList = emptyList<PagingFavoritesStoryArcItem>()
        val pagingState = PagingState<Int, PagingFavoritesStoryArcItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )

        every {
            favoritesRepo.observeByType(
                entityType = FavoriteInfo.EntityType.StoryArc,
                sort = sort,
            )
        } returns flowOf(FavoritesListFetchResult.Success(favoriteInfoList))

        itemsList.forEach { storyArc ->
            coEvery {
                storyArcItemRepo.getRemoteKeysById(storyArc.index)
            } returns ComicVinePagingRepository.Result.Success(null)
        }

        scope.runCurrent()
        val result = mediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        coVerify { storyArcItemRepo.deleteByIdList(emptyList()) }
        verify { pref.favoriteStoryArcsSort }
        verify {
            favoritesRepo.observeByType(
                entityType = FavoriteInfo.EntityType.StoryArc,
                sort = sort,
            )
        }
        confirmVerified(storyArcsRepo, storyArcItemRepo, favoritesRepo, pref, response)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Service error`() = runTest {
        val pageSize = 10
        val response = mockk<StoryArcsResponse>()
        val pagingState = PagingState<Int, PagingFavoritesStoryArcItem>(
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
                entityType = FavoriteInfo.EntityType.StoryArc,
                dateAdded = dateAdded,
            )
        }

        every {
            favoritesRepo.observeByType(
                entityType = FavoriteInfo.EntityType.StoryArc,
                sort = sort,
            )
        } returns flowOf(FavoritesListFetchResult.Success(favoriteInfoList))

        every { response.statusCode } returns StatusCode.InvalidAPIKey
        every { response.error } returns "Invalid API Key"
        coEvery {
            storyArcsRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = null,
                filters = listOf(
                    StoryArcsFilter.Id(
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

        coVerify { storyArcItemRepo.deleteByIdList(emptyList()) }
        verify { pref.favoriteStoryArcsSort }
        verify {
            favoritesRepo.observeByType(
                entityType = FavoriteInfo.EntityType.StoryArc,
                sort = sort,
            )
        }
        verifyAll {
            response.statusCode
            response.error
        }
        coVerify {
            storyArcsRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = null,
                filters = listOf(
                    StoryArcsFilter.Id(
                        favoriteInfoList.subList(0, pageSize).map { it.entityId }
                    )
                ),
            )
        }
        confirmVerified(storyArcsRepo, favoritesRepo, pref, response)
    }
}