package org.proninyaroslav.opencomicvine.model.repo

import org.proninyaroslav.opencomicvine.data.ComicVineResponse
import org.proninyaroslav.opencomicvine.data.filter.ComicVineFilter
import org.proninyaroslav.opencomicvine.data.sort.ComicVineSort

interface ComicVineEntityRepository<Item : Any, DetailsItem : Any, Sort : ComicVineSort, Filter : ComicVineFilter> {
    suspend fun getItems(
        offset: Int,
        limit: Int,
        sort: Sort?,
        filters: List<Filter>,
    ): ComicVineResult<ComicVineResponse<List<Item>>>

    suspend fun getItemDetailsById(id: Int): ComicVineResult<ComicVineResponse<DetailsItem>>
}