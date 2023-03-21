package org.proninyaroslav.opencomicvine.model.repo

import kotlinx.coroutines.flow.first
import org.proninyaroslav.opencomicvine.data.ComicVineResponse
import org.proninyaroslav.opencomicvine.data.MovieInfo
import org.proninyaroslav.opencomicvine.data.MoviesResponse
import org.proninyaroslav.opencomicvine.data.filter.MoviesFilter
import org.proninyaroslav.opencomicvine.data.sort.MoviesSort
import org.proninyaroslav.opencomicvine.model.network.ComicVineService
import javax.inject.Inject

interface MoviesRepository : ComicVineEntityRepository<MovieInfo, Nothing, MoviesSort, MoviesFilter>

class MoviesRepositoryImpl @Inject constructor(
    private val comicVineService: ComicVineService,
    private val apiKeyRepo: ApiKeyRepository,
) : MoviesRepository {

    override suspend fun getItems(
        offset: Int,
        limit: Int,
        sort: MoviesSort?,
        filters: List<MoviesFilter>,
    ): ComicVineResult<MoviesResponse> {
        val apiKey = when (val res = apiKeyRepo.get().first()) {
            is ApiKeyRepository.GetResult.Success -> res.data
            is ApiKeyRepository.GetResult.Failed -> {
                return ComicVineResult.Failed.ApiKeyError(res)
            }
        }
        val response = comicVineService.movies(
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