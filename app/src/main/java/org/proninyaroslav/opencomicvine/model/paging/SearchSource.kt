package org.proninyaroslav.opencomicvine.model.paging

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import org.proninyaroslav.opencomicvine.data.SearchInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.copyResults
import org.proninyaroslav.opencomicvine.data.item.SearchItem
import org.proninyaroslav.opencomicvine.data.preferences.toComicVineResourceType
import org.proninyaroslav.opencomicvine.data.toFavoritesType
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoriteFetchResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.SearchRepository

@AssistedFactory
interface SearchSourceFactory {
    fun create(queryState: Flow<SearchSource.Query>): SearchSource
}

class SearchSource @AssistedInject constructor(
    @Assisted private val queryState: Flow<Query>,
    private val searchRepo: SearchRepository,
    private val favoritesRepo: FavoritesRepository,
    private val pref: AppPreferences,
) : ComicVineSource<SearchItem, SearchSource.Error>() {

    override suspend fun fetch(offset: Int, limit: Int): FetchResult<SearchItem> {
        return when (val queryState = queryState.first()) {
            Query.Empty -> FetchResult.Empty
            is Query.Value -> {
                val searchFilter = pref.searchFilter.first()
                val res = searchRepo.search(
                    offset = offset,
                    limit = limit,
                    query = queryState.query,
                    resources = searchFilter.resources.toComicVineResourceType(),
                )

                return when (res) {
                    is ComicVineResult.Success -> res.response.run {
                        when (statusCode) {
                            StatusCode.OK -> FetchResult.Success(
                                copyResults(
                                    results.mapIndexed { index, item ->
                                        item.toItem(position = offset + index)
                                    }
                                )
                            )
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
        }
    }

    private fun SearchInfo.toItem(position: Int): SearchItem =
        SearchItem(
            id = position,
            info = this,
            isFavorite = toFavoritesType()?.let { entityType ->
                favoritesRepo.observe(
                    entityId = id,
                    entityType = entityType
                )
            } ?: flowOf(FavoriteFetchResult.Success(false))
        )

    sealed class Error : ComicVineSource.Error() {
        data class Service(
            val statusCode: StatusCode,
            val errorMessage: String,
        ) : Error()

        data class Fetching(
            val error: ComicVineResult.Failed
        ) : Error()
    }

    sealed interface Query {
        object Empty : Query

        data class Value(val query: String) : Query {
            init {
                check(query.isNotBlank()) { "Query value cannot be blank" }
            }
        }
    }
}