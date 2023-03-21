package org.proninyaroslav.opencomicvine.model.repo

import kotlinx.coroutines.flow.first
import org.proninyaroslav.opencomicvine.data.ComicVineResponse
import org.proninyaroslav.opencomicvine.data.LocationInfo
import org.proninyaroslav.opencomicvine.data.LocationsResponse
import org.proninyaroslav.opencomicvine.data.filter.LocationsFilter
import org.proninyaroslav.opencomicvine.data.sort.LocationsSort
import org.proninyaroslav.opencomicvine.model.network.ComicVineService
import javax.inject.Inject

interface LocationsRepository :
    ComicVineEntityRepository<LocationInfo, Nothing, LocationsSort, LocationsFilter>

class LocationsRepositoryImpl @Inject constructor(
    private val comicVineService: ComicVineService,
    private val apiKeyRepo: ApiKeyRepository,
) : LocationsRepository {

    override suspend fun getItems(
        offset: Int,
        limit: Int,
        sort: LocationsSort?,
        filters: List<LocationsFilter>,
    ): ComicVineResult<LocationsResponse> {
        val apiKey = when (val res = apiKeyRepo.get().first()) {
            is ApiKeyRepository.GetResult.Success -> res.data
            is ApiKeyRepository.GetResult.Failed -> {
                return ComicVineResult.Failed.ApiKeyError(res)
            }
        }
        val response = comicVineService.locations(
            apiKey = apiKey,
            offset = offset,
            limit = limit,
            sort = sort,
            filter = filters.ifEmpty { null },
        )
        return response.toComicVineResult()
    }

    override suspend fun getItemDetailsById(id: Int): ComicVineResult<ComicVineResponse<Nothing>> {
        TODO("Not yet implemented")
    }
}