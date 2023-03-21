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
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.ObjectInfo
import org.proninyaroslav.opencomicvine.data.ObjectsResponse
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.filter.ObjectsFilter
import org.proninyaroslav.opencomicvine.data.item.favorites.FavoritesObjectItem
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesObjectItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesObjectItem
import org.proninyaroslav.opencomicvine.data.preferences.PrefFavoritesSort
import org.proninyaroslav.opencomicvine.data.preferences.PrefSortDirection
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesListFetchResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.ObjectsRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingObjectRepository
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class ObjectsRemoteMediatorTest {
    lateinit var mediator: ObjectsRemoteMediator

    @MockK
    lateinit var objectsRepo: ObjectsRepository

    @MockK
    lateinit var objectItemRepo: PagingObjectRepository

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

        every { pref.favoriteObjectsSort } returns flowOf(sort)

        mediator =
            ObjectsRemoteMediator(
                scope,
                onRefresh,
                objectsRepo,
                objectItemRepo,
                pref,
                favoritesRepo,
                dispatcher,
            )
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Load success result when more data is present`() = runTest {
        val pageSize = 10
        val response = mockk<ObjectsResponse>()
        val objectsList = List(pageSize) { mockk<ObjectInfo>() }
        val favoritesList = objectsList.mapIndexed { index, it ->
            every { it.id } returns index
            FavoritesObjectItem(
                info = it,
                dateAdded = dateAdded,
            )
        }
        val favoriteInfoList = List(pageSize * 2) {
            FavoriteInfo(
                id = it,
                entityId = it,
                entityType = FavoriteInfo.EntityType.Object,
                dateAdded = dateAdded,
            )
        }
        val itemsList = favoritesList.mapIndexed { i, info ->
            PagingFavoritesObjectItem(
                index = i,
                item = info,
            )
        }
        val remoteKeysList = itemsList.map {
            FavoritesObjectItemRemoteKeys(
                id = it.index,
                prevOffset = null,
                nextOffset = pageSize,
            )
        }
        val pagingState = PagingState<Int, PagingFavoritesObjectItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )

        every {
            favoritesRepo.observeByType(
                entityType = FavoriteInfo.EntityType.Object,
                sort = sort,
            )
        } returns flowOf(FavoritesListFetchResult.Success(favoriteInfoList))

        every { response.statusCode } returns StatusCode.OK
        every { response.results } returns objectsList
        every { response.numberOfPageResults } returns pageSize
        every { response.numberOfTotalResults } returns favoritesList.size
        every { response.error } returns "OK"
        every { response.offset } returns 0
        every { response.limit } returns pageSize
        coEvery {
            objectsRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = null,
                filters = listOf(
                    ObjectsFilter.Id(
                        favoriteInfoList.subList(0, pageSize).map { it.entityId }
                    )
                ),
            )
        } returns ComicVineResult.Success(response)
        coEvery {
            objectItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        } returns ComicVinePagingRepository.Result.Success(Unit)
        itemsList.forEach {
            coEvery {
                objectItemRepo.getRemoteKeysById(it.index)
            } returns ComicVinePagingRepository.Result.Success(remoteKeysList[it.index])
        }

        scope.runCurrent()
        val result = mediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        coVerify { objectItemRepo.deleteByIdList(emptyList()) }
        verify { pref.favoriteObjectsSort }
        verify {
            favoritesRepo.observeByType(
                entityType = FavoriteInfo.EntityType.Object,
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
            objectsRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = null,
                filters = listOf(
                    ObjectsFilter.Id(
                        favoriteInfoList.subList(0, pageSize).map { it.entityId }
                    )
                ),
            )
        }
        coVerify {
            objectItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        }
        confirmVerified(objectsRepo, objectItemRepo, favoritesRepo, pref, response)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Load success result and endOfPaginationReached when no more data`() = runTest {
        val pageSize = 10
        val response = mockk<ObjectsResponse>()
        val favoriteInfoList = emptyList<FavoriteInfo>()
        val itemsList = emptyList<PagingFavoritesObjectItem>()
        val pagingState = PagingState<Int, PagingFavoritesObjectItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )

        every {
            favoritesRepo.observeByType(
                entityType = FavoriteInfo.EntityType.Object,
                sort = sort,
            )
        } returns flowOf(FavoritesListFetchResult.Success(favoriteInfoList))

        itemsList.forEach {
            coEvery {
                objectItemRepo.getRemoteKeysById(it.index)
            } returns ComicVinePagingRepository.Result.Success(null)
        }

        scope.runCurrent()
        val result = mediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        coVerify { objectItemRepo.deleteByIdList(emptyList()) }
        verify { pref.favoriteObjectsSort }
        verify {
            favoritesRepo.observeByType(
                entityType = FavoriteInfo.EntityType.Object,
                sort = sort,
            )
        }
        confirmVerified(objectsRepo, objectItemRepo, favoritesRepo, pref, response)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Service error`() = runTest {
        val pageSize = 10
        val response = mockk<ObjectsResponse>()
        val pagingState = PagingState<Int, PagingFavoritesObjectItem>(
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
                entityType = FavoriteInfo.EntityType.Object,
                dateAdded = dateAdded,
            )
        }

        every {
            favoritesRepo.observeByType(
                entityType = FavoriteInfo.EntityType.Object,
                sort = sort,
            )
        } returns flowOf(FavoritesListFetchResult.Success(favoriteInfoList))

        every { response.statusCode } returns StatusCode.InvalidAPIKey
        every { response.error } returns "Invalid API Key"
        coEvery {
            objectsRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = null,
                filters = listOf(
                    ObjectsFilter.Id(
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

        coVerify { objectItemRepo.deleteByIdList(emptyList()) }
        verify { pref.favoriteObjectsSort }
        verify {
            favoritesRepo.observeByType(
                entityType = FavoriteInfo.EntityType.Object,
                sort = sort,
            )
        }
        verifyAll {
            response.statusCode
            response.error
        }
        coVerify {
            objectsRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = null,
                filters = listOf(
                    ObjectsFilter.Id(
                        favoriteInfoList.subList(0, pageSize).map { it.entityId }
                    )
                ),
            )
        }
        confirmVerified(objectsRepo, favoritesRepo, pref, response)
    }
}