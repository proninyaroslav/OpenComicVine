package org.proninyaroslav.opencomicvine.model.paging.favorites

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.paging.ComicVinePagingItem
import org.proninyaroslav.opencomicvine.data.paging.ComicVineRemoteKeys
import org.proninyaroslav.opencomicvine.model.paging.ComicVineRemoteMediator
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesListFetchResult
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.FavoritesPagingRepository
import org.proninyaroslav.opencomicvine.model.subListFrom
import org.proninyaroslav.opencomicvine.ui.favorites.FavoritesDiffUtil
import java.io.IOException

abstract class FavoritesEntityRemoteMediator<Value : ComicVinePagingItem, FetchValue : Any, RemoteKeys : ComicVineRemoteKeys>(
    favoritesFlow: Flow<FavoritesListFetchResult>,
    private val pagingRepo: FavoritesPagingRepository<Value, RemoteKeys>,
    private val scope: CoroutineScope,
    private val onRefresh: () -> Unit,
    private val ioDispatcher: CoroutineDispatcher,
) : ComicVineRemoteMediator<
        Value,
        FetchValue,
        RemoteKeys,
        FavoritesEntityRemoteMediator.Error,
        >
    (ioDispatcher) {

    private val diffUtil = FavoritesDiffUtil()

    private val favorites = favoritesFlow
        .onEach { res ->
            if (res is FavoritesListFetchResult.Success) {
                onFetchFavorites(res.entityList)
            }
        }.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = FavoritesListFetchResult.Success(emptyList()),
        )

    private val favoritesItemMap = favorites.map { res ->
        when (res) {
            is FavoritesListFetchResult.Success -> res.entityList.associateBy { it.entityId }
            else -> emptyMap()
        }
    }
    private val idList = favoritesItemMap.map { it.keys.toList() }

    private fun onFetchFavorites(entityList: List<FavoriteInfo>) {
        scope.launch {
            diffUtil.compare(entityList).run {
                withContext(ioDispatcher) {
                    pagingRepo.deleteByIdList(removedItems.map { it.entityId })
                }
                if (addedItems.isNotEmpty()) {
                    onRefresh()
                }
            }
        }
    }

    override suspend fun getEndOfPaginationOffset() = idList.first().size

    final override suspend fun fetch(offset: Int, limit: Int): FetchResult<FetchValue> {
        return when (val res = favorites.first()) {
            is FavoritesListFetchResult.Failed.IO -> {
                FetchResult.Failed(Error.IO(res.exception))
            }
            is FavoritesListFetchResult.Success -> {
                if (res.entityList.isEmpty()) {
                    FetchResult.Empty
                } else {
                    fetch(
                        offset = 0,
                        limit = limit,
                        idListRange = idList.first()
                            .subListFrom(offset = offset, maxLength = limit),
                        favoritesItemMap = favoritesItemMap,
                    )
                }
            }
        }
    }

    override suspend fun saveCache(
        values: List<Value>,
        keys: List<RemoteKeys>,
        clearCache: Boolean
    ): LocalResult<Unit> {
        return when (val res = pagingRepo.saveItems(
            items = values,
            remoteKeys = keys,
            clearBeforeSave = clearCache,
        )) {
            is ComicVinePagingRepository.Result.Success -> LocalResult.Success(Unit)
            is ComicVinePagingRepository.Result.Failed.IO -> {
                LocalResult.Failed(Error.Save.IO(res.exception))
            }
        }
    }

    override suspend fun getRemoteKeys(id: Int): LocalResult<RemoteKeys?> {
        return when (val res = pagingRepo.getRemoteKeysById(id)) {
            is ComicVinePagingRepository.Result.Success -> LocalResult.Success(res.value)
            is ComicVinePagingRepository.Result.Failed.IO -> {
                LocalResult.Failed(Error.Save.IO(res.exception))
            }
        }
    }

    protected abstract suspend fun fetch(
        offset: Int,
        limit: Int,
        idListRange: List<Int>,
        favoritesItemMap: Flow<Map<Int, FavoriteInfo>>,
    ): FetchResult<FetchValue>

    sealed class Error : ComicVineRemoteMediator.Error() {
        data class IO(val exception: IOException) : Error()

        data class Service(
            val statusCode: StatusCode,
            val errorMessage: String,
        ) : Error()

        data class Fetching(
            val error: ComicVineResult.Failed
        ) : Error()

        sealed class Save : Error() {
            data class IO(val exception: IOException) : Save()
        }
    }
}