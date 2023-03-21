package org.proninyaroslav.opencomicvine.model.paging

import androidx.paging.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.proninyaroslav.opencomicvine.data.ComicVineResponse
import org.proninyaroslav.opencomicvine.data.paging.ComicVinePagingItem
import org.proninyaroslav.opencomicvine.data.paging.ComicVineRemoteKeys

@OptIn(ExperimentalPagingApi::class)
abstract class ComicVineRemoteMediator<
        Value : ComicVinePagingItem,
        FetchValue : Any,
        RemoteKeys : ComicVineRemoteKeys,
        Error : ComicVineRemoteMediator.Error,
        >
    (
    private val ioDispatcher: CoroutineDispatcher,
) : RemoteMediator<Int, Value>() {

    protected open suspend fun getEndOfPaginationOffset(): Int? {
        return null
    }

    protected abstract suspend fun fetch(offset: Int, limit: Int): FetchResult<FetchValue>

    protected abstract suspend fun saveCache(
        values: List<Value>,
        keys: List<RemoteKeys>,
        clearCache: Boolean,
    ): LocalResult<Unit>

    protected abstract suspend fun getRemoteKeys(id: Int): LocalResult<RemoteKeys?>

    protected abstract fun buildValues(
        offset: Int,
        fetchList: List<FetchValue>
    ): List<Value>

    protected abstract fun buildRemoteKeys(
        values: List<Value>,
        prevOffset: Int?,
        nextOffset: Int?,
    ): List<RemoteKeys>

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Value>,
    ): MediatorResult {
        val limit = state.config.pageSize

        val offset = when (loadType) {
            LoadType.REFRESH -> {
                getRemoteKeyClosestToCurrentPosition(state)?.run {
                    when (this) {
                        is LocalResult.Success -> value?.nextOffset?.minus(limit)
                        is LocalResult.Failed -> return MediatorResult.Error(error)
                    }
                } ?: 0
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)?.run {
                    when (this) {
                        is LocalResult.Success -> value
                        is LocalResult.Failed -> return MediatorResult.Error(error)
                    }
                }
                val prevOffset = remoteKeys?.prevOffset
                    ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )
                prevOffset
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)?.run {
                    when (this) {
                        is LocalResult.Success -> value
                        is LocalResult.Failed -> return MediatorResult.Error(error)
                    }
                }
                val nextOffset = remoteKeys?.nextOffset
                    ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )
                nextOffset
            }
        }

        return when (val remoteRes = fetch(offset = offset, limit = limit)) {
            is FetchResult.Success -> {
                val response = remoteRes.response
                val endOfPaginationReached =
                    getEndOfPaginationOffset()?.let { offset + limit >= it } ?: false
                            || response.numberOfPageResults == 0
                            || response.results.isEmpty()

                val values = buildValues(offset, response.results)
                when (val saveRes = saveCache(
                    values = values,
                    keys = buildRemoteKeys(
                        values = values,
                        prevOffset = if (offset == 0) null else offset - limit,
                        nextOffset = if (endOfPaginationReached) null else offset + limit,
                    ),
                    clearCache = loadType == LoadType.REFRESH,
                )) {
                    is LocalResult.Success<*> -> MediatorResult.Success(
                        endOfPaginationReached = endOfPaginationReached
                    )
                    is LocalResult.Failed -> MediatorResult.Error(saveRes.error)
                }
            }
            is FetchResult.Failed -> MediatorResult.Error(remoteRes.error)
            FetchResult.Empty -> MediatorResult.Success(endOfPaginationReached = true)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Value>
    ): LocalResult<RemoteKeys?>? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.let {
                withContext(ioDispatcher) {
                    getRemoteKeys(it.index)
                }
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, Value>
    ): LocalResult<RemoteKeys?>? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { value ->
                withContext(ioDispatcher) {
                    getRemoteKeys(value.index)
                }
            }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, Value>
    ): LocalResult<RemoteKeys?>? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { value ->
                withContext(ioDispatcher) {
                    getRemoteKeys(value.index)
                }
            }
    }

    companion object {
        inline fun <reified E : Error> stateToError(state: LoadState): E? {
            return (state as LoadState.Error).error.let {
                if (it is E) it else null
            }
        }
    }

    protected sealed interface FetchResult<out T> {
        object Empty : FetchResult<Nothing>
        data class Success<T>(val response: ComicVineResponse<List<T>>) : FetchResult<T>
        data class Failed(val error: Error) : FetchResult<Nothing>
    }

    protected sealed interface LocalResult<out T> {
        data class Success<T>(val value: T) : LocalResult<T>
        data class Failed(val error: Error) : LocalResult<Nothing>
    }

    abstract class Error : Throwable()
}

