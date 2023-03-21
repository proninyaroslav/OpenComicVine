package org.proninyaroslav.opencomicvine.model.paging.details.volume

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.VolumeDetails
import org.proninyaroslav.opencomicvine.data.copyResults
import org.proninyaroslav.opencomicvine.data.filter.CharactersFilter
import org.proninyaroslav.opencomicvine.data.item.volume.VolumeCharacterItem
import org.proninyaroslav.opencomicvine.model.paging.details.DetailsEntitySource
import org.proninyaroslav.opencomicvine.model.repo.CharactersRepository
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository

@AssistedFactory
interface VolumeCharactersSourceFactory {
    fun create(characters: List<VolumeDetails.Character>): VolumeCharactersSource
}

class VolumeCharactersSource @AssistedInject constructor(
    @Assisted private val characters: List<VolumeDetails.Character>,
    private val charactersRepo: CharactersRepository,
    private val favoritesRepo: FavoritesRepository,
) : VolumeRelatedEntitySource<VolumeDetails.Character, VolumeCharacterItem>(characters) {

    override suspend fun fetch(
        offset: Int,
        limit: Int,
        idListRange: List<Int>,
        entitiesMap: Map<Int, VolumeDetails.Character>,
    ): FetchResult<VolumeCharacterItem> {
        val res = charactersRepo.getItems(
            offset = offset,
            limit = limit,
            sort = null,
            filters = listOf(CharactersFilter.Id(idListRange)),
        )
        return when (res) {
            is ComicVineResult.Success -> res.response.run {
                when (statusCode) {
                    StatusCode.OK -> {
                        val items = results.map {
                            VolumeCharacterItem(
                                info = it,
                                countOfAppearances = entitiesMap[it.id]!!.countOfAppearances,
                                isFavorite = favoritesRepo.observe(
                                    entityId = it.id,
                                    entityType = FavoriteInfo.EntityType.Character,
                                )
                            )
                        }
                        FetchResult.Success(copyResults(items))
                    }
                    else -> FetchResult.Failed(
                        DetailsEntitySource.Error.Service(
                            statusCode = statusCode,
                            errorMessage = error,
                        )
                    )
                }
            }
            else -> FetchResult.Failed(
                DetailsEntitySource.Error.Fetching(
                    error = res as ComicVineResult.Failed
                )
            )
        }
    }

    override fun VolumeDetails.Character.getId(): Int = id
}