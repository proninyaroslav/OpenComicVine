package org.proninyaroslav.opencomicvine.model.repo

import kotlinx.coroutines.flow.first
import org.proninyaroslav.opencomicvine.data.CharacterDetails
import org.proninyaroslav.opencomicvine.data.CharacterInfo
import org.proninyaroslav.opencomicvine.data.CharacterResponse
import org.proninyaroslav.opencomicvine.data.CharactersResponse
import org.proninyaroslav.opencomicvine.data.filter.CharactersFilter
import org.proninyaroslav.opencomicvine.data.sort.CharactersSort
import org.proninyaroslav.opencomicvine.model.network.ComicVineService
import javax.inject.Inject

interface CharactersRepository :
    ComicVineEntityRepository<CharacterInfo, CharacterDetails, CharactersSort, CharactersFilter>

class CharactersRepositoryImpl @Inject constructor(
    private val comicVineService: ComicVineService,
    private val apiKeyRepo: ApiKeyRepository,
) : CharactersRepository {

    override suspend fun getItems(
        offset: Int,
        limit: Int,
        sort: CharactersSort?,
        filters: List<CharactersFilter>,
    ): ComicVineResult<CharactersResponse> {
        val apiKey = when (val res = apiKeyRepo.get().first()) {
            is ApiKeyRepository.GetResult.Success -> res.data
            is ApiKeyRepository.GetResult.Failed -> {
                return ComicVineResult.Failed.ApiKeyError(res)
            }
        }
        val response = comicVineService.characters(
            apiKey = apiKey,
            offset = offset,
            limit = limit,
            sort = sort,
            filter = filters.ifEmpty { null },
        )
        return response.toComicVineResult()
    }

    override suspend fun getItemDetailsById(id: Int): ComicVineResult<CharacterResponse> {
        val apiKey = when (val res = apiKeyRepo.get().first()) {
            is ApiKeyRepository.GetResult.Success -> res.data
            is ApiKeyRepository.GetResult.Failed -> {
                return ComicVineResult.Failed.ApiKeyError(res)
            }
        }
        val response = comicVineService.character(apiKey = apiKey, id = id)
        return response.toComicVineResult()
    }
}