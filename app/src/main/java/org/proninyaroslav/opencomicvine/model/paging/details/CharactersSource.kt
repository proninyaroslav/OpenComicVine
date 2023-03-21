package org.proninyaroslav.opencomicvine.model.paging.details

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.proninyaroslav.opencomicvine.data.CharacterInfo
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.copyResults
import org.proninyaroslav.opencomicvine.data.filter.CharactersFilter
import org.proninyaroslav.opencomicvine.data.item.CharacterItem
import org.proninyaroslav.opencomicvine.data.sort.CharactersSort
import org.proninyaroslav.opencomicvine.model.repo.CharactersRepository
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository

@AssistedFactory
interface CharactersSourceFactory {
    fun create(
        idList: List<Int>,
        sort: Flow<CharactersSort?>? = null,
    ): CharactersSource
}

class CharactersSource @AssistedInject constructor(
    @Assisted idList: List<Int>,
    @Assisted private val sort: Flow<CharactersSort?>?,
    private val charactersRepo: CharactersRepository,
    private val favoritesRepo: FavoritesRepository,
) : DetailsEntitySource<CharacterItem>(idList) {

    override suspend fun fetch(
        offset: Int,
        limit: Int,
        idListRange: List<Int>
    ): FetchResult<CharacterItem> {
        val res = charactersRepo.getItems(
            offset = offset,
            limit = limit,
            sort = sort?.first(),
            filters = listOf(CharactersFilter.Id(idListRange)),
        )
        return when (res) {
            is ComicVineResult.Success -> res.response.run {
                when (statusCode) {
                    StatusCode.OK -> FetchResult.Success(
                        copyResults(results.map { it.toItem() })
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

    private fun CharacterInfo.toItem() =
        CharacterItem(
            info = this,
            isFavorite = favoritesRepo.observe(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Character,
            )
        )
}