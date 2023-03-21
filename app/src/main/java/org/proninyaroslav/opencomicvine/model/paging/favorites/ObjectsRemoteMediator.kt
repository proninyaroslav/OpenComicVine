package org.proninyaroslav.opencomicvine.model.paging.favorites

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.copyResults
import org.proninyaroslav.opencomicvine.data.filter.ObjectsFilter
import org.proninyaroslav.opencomicvine.data.item.favorites.FavoritesObjectItem
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesObjectItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesObjectItem
import org.proninyaroslav.opencomicvine.di.IoDispatcher
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.ObjectsRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingObjectRepository

@AssistedFactory
interface ObjectsRemoteMediatorFactory {
    fun create(
        scope: CoroutineScope,
        onRefresh: () -> Unit,
    ): ObjectsRemoteMediator
}

@OptIn(FlowPreview::class)
class ObjectsRemoteMediator @AssistedInject constructor(
    @Assisted private val scope: CoroutineScope,
    @Assisted private val onRefresh: () -> Unit,
    private val objectsRepo: ObjectsRepository,
    objectPagingRepo: PagingObjectRepository,
    private val pref: AppPreferences,
    private val favoritesRepo: FavoritesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : FavoritesEntityRemoteMediator<PagingFavoritesObjectItem, FavoritesObjectItem, FavoritesObjectItemRemoteKeys>(
    favoritesFlow = pref.favoriteObjectsSort.flatMapConcat { sort ->
        favoritesRepo.observeByType(
            entityType = FavoriteInfo.EntityType.Object,
            sort = sort,
        )
    },
    pagingRepo = objectPagingRepo,
    scope = scope,
    onRefresh = onRefresh,
    ioDispatcher = ioDispatcher,
) {
    override suspend fun fetch(
        offset: Int,
        limit: Int,
        idListRange: List<Int>,
        favoritesItemMap: Flow<Map<Int, FavoriteInfo>>,
    ): FetchResult<FavoritesObjectItem> {
        val res = objectsRepo.getItems(
            offset = offset,
            limit = limit,
            sort = null,
            filters = listOf(ObjectsFilter.Id(idListRange)),
        )
        return when (res) {
            is ComicVineResult.Success -> res.response.run {
                when (statusCode) {
                    StatusCode.OK -> {
                        val map = favoritesItemMap.first()
                        val items = results.mapNotNull {
                            map[it.id]?.let { info ->
                                FavoritesObjectItem(
                                    info = it,
                                    dateAdded = info.dateAdded,
                                )
                            }
                        }.sort(pref.favoriteObjectsSort.first())
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
        values: List<PagingFavoritesObjectItem>,
        prevOffset: Int?,
        nextOffset: Int?
    ): List<FavoritesObjectItemRemoteKeys> = values.map { value ->
        FavoritesObjectItemRemoteKeys(
            id = value.index,
            prevOffset = prevOffset,
            nextOffset = nextOffset,
        )
    }

    override fun buildValues(
        offset: Int,
        fetchList: List<FavoritesObjectItem>,
    ): List<PagingFavoritesObjectItem> {
        var index = offset
        return fetchList.map {
            PagingFavoritesObjectItem(index = index++, item = it)
        }.toList()
    }
}