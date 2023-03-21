package org.proninyaroslav.opencomicvine.model.paging.details

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.VolumeInfo
import org.proninyaroslav.opencomicvine.data.copyResults
import org.proninyaroslav.opencomicvine.data.filter.VolumesFilter
import org.proninyaroslav.opencomicvine.data.item.VolumeItem
import org.proninyaroslav.opencomicvine.data.sort.VolumesSort
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.VolumesRepository

@AssistedFactory
interface VolumesSourceFactory {
    fun create(
        idList: List<Int>,
        sort: Flow<VolumesSort?>? = null,
    ): VolumesSource
}

class VolumesSource @AssistedInject constructor(
    @Assisted idList: List<Int>,
    @Assisted private val sort: Flow<VolumesSort?>?,
    private val volumesRepo: VolumesRepository,
    private val favoritesRepo: FavoritesRepository,
) : DetailsEntitySource<VolumeItem>(idList) {

    override suspend fun fetch(
        offset: Int,
        limit: Int,
        idListRange: List<Int>
    ): FetchResult<VolumeItem> {
        val res = volumesRepo.getItems(
            offset = offset,
            limit = limit,
            sort = sort?.first(),
            filters = listOf(VolumesFilter.Id(idListRange)),
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

    private fun VolumeInfo.toItem() =
        VolumeItem(
            info = this,
            isFavorite = favoritesRepo.observe(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Volume,
            )
        )
}