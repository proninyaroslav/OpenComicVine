package org.proninyaroslav.opencomicvine.model.paging.wiki

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import org.proninyaroslav.opencomicvine.data.CharacterInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.paging.wiki.PagingWikiCharacterItem
import org.proninyaroslav.opencomicvine.data.paging.wiki.WikiCharacterItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.preferences.toComicVineFiltersList
import org.proninyaroslav.opencomicvine.data.preferences.toComicVineSort
import org.proninyaroslav.opencomicvine.di.IoDispatcher
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.repo.CharactersRepository
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingCharacterRepository

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
) : WikiEntityRemoteMediator<PagingWikiCharacterItem, CharacterInfo, WikiCharacterItemRemoteKeys>(
    pagingRepo = characterPagingRepo,
    ioDispatcher = ioDispatcher,
) {
    override suspend fun getEndOfPaginationOffset(): Int? = endOfPaginationOffset

    override suspend fun fetch(offset: Int, limit: Int): FetchResult<CharacterInfo> {
        val res = pref.run {
            charactersRepo.getItems(
                offset = offset,
                limit = limit,
                sort = wikiCharactersSort.first().toComicVineSort(),
                filters = wikiCharactersFilters.first().toComicVineFiltersList(),
            )
        }
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
        values: List<PagingWikiCharacterItem>,
        prevOffset: Int?,
        nextOffset: Int?
    ): List<WikiCharacterItemRemoteKeys> = values.map { value ->
        WikiCharacterItemRemoteKeys(
            id = value.index,
            prevOffset = prevOffset,
            nextOffset = nextOffset,
        )
    }

    override fun buildValues(
        offset: Int,
        fetchList: List<CharacterInfo>
    ): List<PagingWikiCharacterItem> {
        var index = offset
        return fetchList.map { PagingWikiCharacterItem(index = index++, info = it) }.toList()
    }
}