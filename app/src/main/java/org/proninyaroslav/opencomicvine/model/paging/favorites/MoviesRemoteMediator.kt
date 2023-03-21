package org.proninyaroslav.opencomicvine.model.paging.favorites

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.copyResults
import org.proninyaroslav.opencomicvine.data.filter.MoviesFilter
import org.proninyaroslav.opencomicvine.data.item.favorites.FavoritesMovieItem
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesMovieItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesMovieItem
import org.proninyaroslav.opencomicvine.di.IoDispatcher
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.MoviesRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingMovieRepository

@AssistedFactory
interface MoviesRemoteMediatorFactory {
    fun create(
        scope: CoroutineScope,
        onRefresh: () -> Unit,
    ): MoviesRemoteMediator
}

@OptIn(FlowPreview::class)
class MoviesRemoteMediator @AssistedInject constructor(
    @Assisted private val scope: CoroutineScope,
    @Assisted private val onRefresh: () -> Unit,
    private val moviesRepo: MoviesRepository,
    moviePagingRepo: PagingMovieRepository,
    private val pref: AppPreferences,
    private val favoritesRepo: FavoritesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : FavoritesEntityRemoteMediator<PagingFavoritesMovieItem, FavoritesMovieItem, FavoritesMovieItemRemoteKeys>(
    favoritesFlow = pref.favoriteMoviesSort.flatMapConcat { sort ->
        favoritesRepo.observeByType(
            entityType = FavoriteInfo.EntityType.Movie,
            sort = sort,
        )
    },
    pagingRepo = moviePagingRepo,
    scope = scope,
    onRefresh = onRefresh,
    ioDispatcher = ioDispatcher,
) {
    override suspend fun fetch(
        offset: Int,
        limit: Int,
        idListRange: List<Int>,
        favoritesItemMap: Flow<Map<Int, FavoriteInfo>>,
    ): FetchResult<FavoritesMovieItem> {
        val res = moviesRepo.getItems(
            offset = offset,
            limit = limit,
            sort = null,
            filters = listOf(MoviesFilter.Id(idListRange)),
        )
        return when (res) {
            is ComicVineResult.Success -> res.response.run {
                when (statusCode) {
                    StatusCode.OK -> {
                        val map = favoritesItemMap.first()
                        val items = results.mapNotNull {
                            map[it.id]?.let { info ->
                                FavoritesMovieItem(
                                    info = it,
                                    dateAdded = info.dateAdded,
                                )
                            }
                        }.sort(pref.favoriteMoviesSort.first())
                        FetchResult.Success(copyResults(items))
                    }
                    else -> FetchResult.Failed(
                        Error.Service(
                            statusCode = statusCode,
                            errorMessage = error,
                        )
                    )
                }
            }
            else -> FetchResult.Failed(
                Error.Fetching(
                    error = res as ComicVineResult.Failed
                )
            )
        }
    }

    override fun buildRemoteKeys(
        values: List<PagingFavoritesMovieItem>,
        prevOffset: Int?,
        nextOffset: Int?
    ): List<FavoritesMovieItemRemoteKeys> = values.map { value ->
        FavoritesMovieItemRemoteKeys(
            id = value.index,
            prevOffset = prevOffset,
            nextOffset = nextOffset,
        )
    }

    override fun buildValues(
        offset: Int,
        fetchList: List<FavoritesMovieItem>,
    ): List<PagingFavoritesMovieItem> {
        var index = offset
        return fetchList.map {
            PagingFavoritesMovieItem(index = index++, item = it)
        }.toList()
    }
}