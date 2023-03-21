package org.proninyaroslav.opencomicvine.model.paging.recent

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import org.proninyaroslav.opencomicvine.data.CharacterInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.paging.recent.PagingRecentCharacterItem
import org.proninyaroslav.opencomicvine.data.paging.recent.RecentCharacterItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.preferences.toComicVineFiltersList
import org.proninyaroslav.opencomicvine.data.sort.CharactersSort
import org.proninyaroslav.opencomicvine.data.sort.ComicVineSortDirection
import org.proninyaroslav.opencomicvine.di.IoDispatcher
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.repo.CharactersRepository
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingCharacterRepository

@AssistedFactory
interface CharactersRemoteMediatorFactory {
    fun create(endOfPaginationOffset: Int? = null): CharactersRemoteMediator
}

class CharactersRemoteMediator @AssistedInject constructor(
    @Assisted private val endOfPaginationOffset: Int?,
    private val charactersRepo: CharactersRepository,
    characterPagingRepo: PagingCharacterRepository,
    private val pref: AppPreferences,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : RecentEntityRemoteMediator<PagingRecentCharacterItem, CharacterInfo, RecentCharacterItemRemoteKeys>(
    pagingRepo = characterPagingRepo,
    ioDispatcher = ioDispatcher,
) {
    override suspend fun getEndOfPaginationOffset(): Int? = endOfPaginationOffset

    override suspend fun fetch(offset: Int, limit: Int): FetchResult<CharacterInfo> {
        delay(5000)
        val res = charactersRepo.getItems(
            offset = offset,
            limit = limit,
            sort = CharactersSort.DateAdded(ComicVineSortDirection.Desc),
            filters = pref.recentCharactersFilters.first().toComicVineFiltersList(),
        )
        return when (res) {
            is ComicVineResult.Success -> res.response.run {
                when (statusCode) {
                    StatusCode.OK -> FetchResult.Success(this)
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
        values: List<PagingRecentCharacterItem>,
        prevOffset: Int?,
        nextOffset: Int?
    ): List<RecentCharacterItemRemoteKeys> = values.map { value ->
        RecentCharacterItemRemoteKeys(
            id = value.index,
            prevOffset = prevOffset,
            nextOffset = nextOffset,
        )
    }

    override fun buildValues(
        offset: Int,
        fetchList: List<CharacterInfo>
    ): List<PagingRecentCharacterItem> {
        var index = offset
        return fetchList.map { PagingRecentCharacterItem(index = index++, info = it) }.toList()
    }
}