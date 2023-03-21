package org.proninyaroslav.opencomicvine.model.paging.favorites

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.copyResults
import org.proninyaroslav.opencomicvine.data.filter.TeamsFilter
import org.proninyaroslav.opencomicvine.data.item.favorites.FavoritesTeamItem
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesTeamItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesTeamItem
import org.proninyaroslav.opencomicvine.di.IoDispatcher
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.TeamsRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingTeamRepository

@AssistedFactory
interface TeamsRemoteMediatorFactory {
    fun create(
        scope: CoroutineScope,
        onRefresh: () -> Unit,
    ): TeamsRemoteMediator
}

@OptIn(FlowPreview::class)
class TeamsRemoteMediator @AssistedInject constructor(
    @Assisted private val scope: CoroutineScope,
    @Assisted private val onRefresh: () -> Unit,
    private val teamsRepo: TeamsRepository,
    teamPagingRepo: PagingTeamRepository,
    private val pref: AppPreferences,
    private val favoritesRepo: FavoritesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : FavoritesEntityRemoteMediator<PagingFavoritesTeamItem, FavoritesTeamItem, FavoritesTeamItemRemoteKeys>(
    favoritesFlow = pref.favoriteTeamsSort.flatMapConcat { sort ->
        favoritesRepo.observeByType(
            entityType = FavoriteInfo.EntityType.Team,
            sort = sort,
        )
    },
    pagingRepo = teamPagingRepo,
    scope = scope,
    onRefresh = onRefresh,
    ioDispatcher = ioDispatcher,
) {
    override suspend fun fetch(
        offset: Int,
        limit: Int,
        idListRange: List<Int>,
        favoritesItemMap: Flow<Map<Int, FavoriteInfo>>,
    ): FetchResult<FavoritesTeamItem> {
        val res = teamsRepo.getItems(
            offset = offset,
            limit = limit,
            sort = null,
            filters = listOf(TeamsFilter.Id(idListRange)),
        )
        return when (res) {
            is ComicVineResult.Success -> res.response.run {
                when (statusCode) {
                    StatusCode.OK -> {
                        val map = favoritesItemMap.first()
                        val items = results.mapNotNull {
                            map[it.id]?.let { info ->
                                FavoritesTeamItem(
                                    info = it,
                                    dateAdded = info.dateAdded,
                                )
                            }
                        }.sort(pref.favoriteTeamsSort.first())
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
        values: List<PagingFavoritesTeamItem>,
        prevOffset: Int?,
        nextOffset: Int?
    ): List<FavoritesTeamItemRemoteKeys> = values.map { value ->
        FavoritesTeamItemRemoteKeys(
            id = value.index,
            prevOffset = prevOffset,
            nextOffset = nextOffset,
        )
    }

    override fun buildValues(
        offset: Int,
        fetchList: List<FavoritesTeamItem>,
    ): List<PagingFavoritesTeamItem> {
        var index = offset
        return fetchList.map {
            PagingFavoritesTeamItem(index = index++, item = it)
        }.toList()
    }
}