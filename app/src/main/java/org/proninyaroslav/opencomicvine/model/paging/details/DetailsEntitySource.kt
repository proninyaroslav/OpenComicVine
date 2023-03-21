package org.proninyaroslav.opencomicvine.model.paging.details

import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.model.paging.ComicVineSource
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.subListFrom

abstract class DetailsEntitySource<Value : Any>(
    private val idList: List<Int>
) : ComicVineSource<Value, DetailsEntitySource.Error>(
    endOfPaginationOffset = idList.size,
) {
    final override suspend fun fetch(offset: Int, limit: Int): FetchResult<Value> {
        return if (idList.isEmpty()) {
            FetchResult.Empty
        } else {
            fetch(
                offset = 0,
                limit = limit,
                idListRange = idList.subListFrom(offset = offset, maxLength = limit)
            )
        }
    }

    protected abstract suspend fun fetch(
        offset: Int,
        limit: Int,
        idListRange: List<Int>,
    ): FetchResult<Value>

    sealed class Error : ComicVineSource.Error() {
        data class Service(
            val statusCode: StatusCode,
            val errorMessage: String,
        ) : Error()

        data class Fetching(
            val error: ComicVineResult.Failed
        ) : Error()
    }
}