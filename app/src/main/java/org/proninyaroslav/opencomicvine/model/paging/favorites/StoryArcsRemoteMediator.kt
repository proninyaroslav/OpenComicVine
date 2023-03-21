package org.proninyaroslav.opencomicvine.model.paging.favorites

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.copyResults
import org.proninyaroslav.opencomicvine.data.filter.StoryArcsFilter
import org.proninyaroslav.opencomicvine.data.item.favorites.FavoritesStoryArcItem
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesStoryArcItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesStoryArcItem
import org.proninyaroslav.opencomicvine.di.IoDispatcher
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.StoryArcsRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingStoryArcRepository

@AssistedFactory
interface StoryArcsRemoteMediatorFactory {
    fun create(
        scope: CoroutineScope,
        onRefresh: () -> Unit,
    ): StoryArcsRemoteMediator
}

@OptIn(FlowPreview::class)
class StoryArcsRemoteMediator @AssistedInject constructor(
    @Assisted private val scope: CoroutineScope,
    @Assisted private val onRefresh: () -> Unit,
    private val storyArcsRepo: StoryArcsRepository,
    storyArcPagingRepo: PagingStoryArcRepository,
    private val pref: AppPreferences,
    private val favoritesRepo: FavoritesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : FavoritesEntityRemoteMediator<PagingFavoritesStoryArcItem, FavoritesStoryArcItem, FavoritesStoryArcItemRemoteKeys>(
    favoritesFlow = pref.favoriteStoryArcsSort.flatMapConcat { sort ->
        favoritesRepo.observeByType(
            entityType = FavoriteInfo.EntityType.StoryArc,
            sort = sort,
        )
    },
    pagingRepo = storyArcPagingRepo,
    scope = scope,
    onRefresh = onRefresh,
    ioDispatcher = ioDispatcher,
) {
    override suspend fun fetch(
        offset: Int,
        limit: Int,
        idListRange: List<Int>,
        favoritesItemMap: Flow<Map<Int, FavoriteInfo>>,
    ): FetchResult<FavoritesStoryArcItem> {
        val res = storyArcsRepo.getItems(
            offset = offset,
            limit = limit,
            sort = null,
            filters = listOf(StoryArcsFilter.Id(idListRange)),
        )
        return when (res) {
            is ComicVineResult.Success -> res.response.run {
                when (statusCode) {
                    StatusCode.OK -> {
                        val map = favoritesItemMap.first()
                        val items = results.mapNotNull {
                            map[it.id]?.let { info ->
                                FavoritesStoryArcItem(
                                    info = it,
                                    dateAdded = info.dateAdded,
                                )
                            }
                        }.sort(pref.favoriteStoryArcsSort.first())
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
        values: List<PagingFavoritesStoryArcItem>,
        prevOffset: Int?,
        nextOffset: Int?
    ): List<FavoritesStoryArcItemRemoteKeys> = values.map { value ->
        FavoritesStoryArcItemRemoteKeys(
            id = value.index,
            prevOffset = prevOffset,
            nextOffset = nextOffset,
        )
    }

    override fun buildValues(
        offset: Int,
        fetchList: List<FavoritesStoryArcItem>,
    ): List<PagingFavoritesStoryArcItem> {
        var index = offset
        return fetchList.map {
            PagingFavoritesStoryArcItem(index = index++, item = it)
        }.toList()
    }
}