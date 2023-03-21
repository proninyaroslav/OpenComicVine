package org.proninyaroslav.opencomicvine.model.repo

import kotlinx.coroutines.flow.first
import org.proninyaroslav.opencomicvine.data.VolumeDetails
import org.proninyaroslav.opencomicvine.data.VolumeInfo
import org.proninyaroslav.opencomicvine.data.VolumeResponse
import org.proninyaroslav.opencomicvine.data.VolumesResponse
import org.proninyaroslav.opencomicvine.data.filter.VolumesFilter
import org.proninyaroslav.opencomicvine.data.sort.VolumesSort
import org.proninyaroslav.opencomicvine.model.network.ComicVineService
import javax.inject.Inject

interface VolumesRepository :
    ComicVineEntityRepository<VolumeInfo, VolumeDetails, VolumesSort, VolumesFilter>

class VolumesRepositoryImpl @Inject constructor(
    private val comicVineService: ComicVineService,
    private val apiKeyRepo: ApiKeyRepository,
) : VolumesRepository {

    override suspend fun getItems(
        offset: Int,
        limit: Int,
        sort: VolumesSort?,
        filters: List<VolumesFilter>,
    ): ComicVineResult<VolumesResponse> {
        val apiKey = when (val res = apiKeyRepo.get().first()) {
            is ApiKeyRepository.GetResult.Success -> res.data
            is ApiKeyRepository.GetResult.Failed -> {
                return ComicVineResult.Failed.ApiKeyError(res)
            }
        }
        val response = comicVineService.volumes(
            apiKey = apiKey,
            offset = offset,
            limit = limit,
            sort = sort,
            filter = filters.ifEmpty { null },
        )
        return response.toComicVineResult()
    }

    override suspend fun getItemDetailsById(id: Int): ComicVineResult<VolumeResponse> {
        val apiKey = when (val res = apiKeyRepo.get().first()) {
            is ApiKeyRepository.GetResult.Success -> res.data
            is ApiKeyRepository.GetResult.Failed -> {
                return ComicVineResult.Failed.ApiKeyError(res)
            }
        }
        val response = comicVineService.volume(apiKey = apiKey, id = id)
        return response.toComicVineResult()
    }
}