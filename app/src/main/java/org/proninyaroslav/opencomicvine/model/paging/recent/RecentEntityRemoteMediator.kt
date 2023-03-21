package org.proninyaroslav.opencomicvine.model.paging.recent

import kotlinx.coroutines.CoroutineDispatcher
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.paging.ComicVinePagingItem
import org.proninyaroslav.opencomicvine.data.paging.ComicVineRemoteKeys
import org.proninyaroslav.opencomicvine.model.paging.ComicVineRemoteMediator
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import java.io.IOException

abstract class RecentEntityRemoteMediator<Value : ComicVinePagingItem, FetchValue : Any, RemoteKeys : ComicVineRemoteKeys>(
    private val pagingRepo: ComicVinePagingRepository<Value, RemoteKeys>,
    ioDispatcher: CoroutineDispatcher,
) : ComicVineRemoteMediator<
        Value,
        FetchValue,
        RemoteKeys,
        RecentEntityRemoteMediator.Error,
        >
    (ioDispatcher) {

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

    sealed class Error : ComicVineRemoteMediator.Error() {
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