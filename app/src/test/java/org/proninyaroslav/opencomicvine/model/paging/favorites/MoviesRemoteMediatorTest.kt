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
import org.proninyaroslav.opencomicvine.types.FavoriteInfo
import org.proninyaroslav.opencomicvine.types.MovieInfo
import org.proninyaroslav.opencomicvine.types.MoviesResponse
import org.proninyaroslav.opencomicvine.types.StatusCode
import org.proninyaroslav.opencomicvine.types.filter.MoviesFilter
import org.proninyaroslav.opencomicvine.types.item.favorites.FavoritesMovieItem
import org.proninyaroslav.opencomicvine.types.paging.favorites.FavoritesMovieItemRemoteKeys
import org.proninyaroslav.opencomicvine.types.paging.favorites.PagingFavoritesMovieItem
import org.proninyaroslav.opencomicvine.types.preferences.PrefFavoritesSort
import org.proninyaroslav.opencomicvine.types.preferences.PrefSortDirection
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesListFetchResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.MoviesRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingMovieRepository
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesRemoteMediatorTest {
    lateinit var mediator: MoviesRemoteMediator

    @MockK
    lateinit var moviesRepo: MoviesRepository

    @MockK
    lateinit var movieItemRepo: PagingMovieRepository

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

        every { pref.favoriteMoviesSort } returns flowOf(sort)

        mediator =
            MoviesRemoteMediator(
                scope,
                onRefresh,
                moviesRepo,
                movieItemRepo,
                pref,
                favoritesRepo,
                dispatcher,
            )
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Load success result when more data is present`() = runTest {
        val pageSize = 10
        val response = mockk<MoviesResponse>()
        val moviesList = List(pageSize) { mockk<MovieInfo>() }
        val favoritesList = moviesList.mapIndexed { index, it ->
            every { it.id } returns index
            FavoritesMovieItem(
                info = it,
                dateAdded = dateAdded,
            )
        }
        val favoriteInfoList = List(pageSize * 2) {
            FavoriteInfo(
                id = it,
                entityId = it,
                entityType = FavoriteInfo.EntityType.Movie,
                dateAdded = dateAdded,
            )
        }
        val itemsList = favoritesList.mapIndexed { i, info ->
            PagingFavoritesMovieItem(
                index = i,
                item = info,
            )
        }
        val remoteKeysList = itemsList.map { movie ->
            FavoritesMovieItemRemoteKeys(
                id = movie.index,
                prevOffset = null,
                nextOffset = pageSize,
            )
        }
        val pagingState = PagingState<Int, PagingFavoritesMovieItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )

        every {
            favoritesRepo.observeByType(
                entityType = FavoriteInfo.EntityType.Movie,
                sort = sort,
            )
        } returns flowOf(FavoritesListFetchResult.Success(favoriteInfoList))

        every { response.statusCode } returns StatusCode.OK
        every { response.results } returns moviesList
        every { response.numberOfPageResults } returns pageSize
        every { response.numberOfTotalResults } returns favoritesList.size
        every { response.error } returns "OK"
        every { response.offset } returns 0
        every { response.limit } returns pageSize
        coEvery {
            moviesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = null,
                filters = listOf(
                    MoviesFilter.Id(
                        favoriteInfoList.subList(0, pageSize).map { it.entityId }
                    )
                ),
            )
        } returns ComicVineResult.Success(response)
        coEvery {
            movieItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        } returns ComicVinePagingRepository.Result.Success(Unit)
        itemsList.forEach { movie ->
            coEvery {
                movieItemRepo.getRemoteKeysById(movie.index)
            } returns ComicVinePagingRepository.Result.Success(remoteKeysList[movie.index])
        }

        scope.runCurrent()
        val result = mediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        coVerify { movieItemRepo.deleteByIdList(emptyList()) }
        verify { pref.favoriteMoviesSort }
        verify {
            favoritesRepo.observeByType(
                entityType = FavoriteInfo.EntityType.Movie,
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
            moviesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = null,
                filters = listOf(
                    MoviesFilter.Id(
                        favoriteInfoList.subList(0, pageSize).map { it.entityId }
                    )
                ),
            )
        }
        coVerify {
            movieItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        }
        confirmVerified(moviesRepo, movieItemRepo, favoritesRepo, pref, response)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Load success result and endOfPaginationReached when no more data`() = runTest {
        val pageSize = 10
        val response = mockk<MoviesResponse>()
        val favoriteInfoList = emptyList<FavoriteInfo>()
        val itemsList = emptyList<PagingFavoritesMovieItem>()
        val pagingState = PagingState<Int, PagingFavoritesMovieItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )

        every {
            favoritesRepo.observeByType(
                entityType = FavoriteInfo.EntityType.Movie,
                sort = sort,
            )
        } returns flowOf(FavoritesListFetchResult.Success(favoriteInfoList))

        itemsList.forEach { movie ->
            coEvery {
                movieItemRepo.getRemoteKeysById(movie.index)
            } returns ComicVinePagingRepository.Result.Success(null)
        }

        scope.runCurrent()
        val result = mediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        coVerify { movieItemRepo.deleteByIdList(emptyList()) }
        verify { pref.favoriteMoviesSort }
        verify {
            favoritesRepo.observeByType(
                entityType = FavoriteInfo.EntityType.Movie,
                sort = sort,
            )
        }
        confirmVerified(moviesRepo, movieItemRepo, favoritesRepo, pref, response)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Service error`() = runTest {
        val pageSize = 10
        val response = mockk<MoviesResponse>()
        val pagingState = PagingState<Int, PagingFavoritesMovieItem>(
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
                entityType = FavoriteInfo.EntityType.Movie,
                dateAdded = dateAdded,
            )
        }

        every {
            favoritesRepo.observeByType(
                entityType = FavoriteInfo.EntityType.Movie,
                sort = sort,
            )
        } returns flowOf(FavoritesListFetchResult.Success(favoriteInfoList))

        every { response.statusCode } returns StatusCode.InvalidAPIKey
        every { response.error } returns "Invalid API Key"
        coEvery {
            moviesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = null,
                filters = listOf(
                    MoviesFilter.Id(
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

        coVerify { movieItemRepo.deleteByIdList(emptyList()) }
        verify { pref.favoriteMoviesSort }
        verify {
            favoritesRepo.observeByType(
                entityType = FavoriteInfo.EntityType.Movie,
                sort = sort,
            )
        }
        verifyAll {
            response.statusCode
            response.error
        }
        coVerify {
            moviesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = null,
                filters = listOf(
                    MoviesFilter.Id(
                        favoriteInfoList.subList(0, pageSize).map { it.entityId }
                    )
                ),
            )
        }
        confirmVerified(moviesRepo, favoritesRepo, pref, response)
    }
}