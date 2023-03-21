package org.proninyaroslav.opencomicvine.model.paging.wiki

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.VolumeInfo
import org.proninyaroslav.opencomicvine.data.paging.wiki.PagingWikiVolumeItem
import org.proninyaroslav.opencomicvine.data.paging.wiki.WikiVolumeItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.preferences.toComicVineFiltersList
import org.proninyaroslav.opencomicvine.data.preferences.toComicVineSort
import org.proninyaroslav.opencomicvine.di.IoDispatcher
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.VolumesRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingVolumeRepository

@AssistedFactory
interface VolumesRemoteMediatorFactory {
    fun create(endOfPaginationOffset: Int? = null): VolumesRemoteMediator
}

class VolumesRemoteMediator @AssistedInject constructor(
    @Assisted private val endOfPaginationOffset: Int?,
    private val volumesRepo: VolumesRepository,
    volumePagingRepo: PagingVolumeRepository,
    private val pref: AppPreferences,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : WikiEntityRemoteMediator<PagingWikiVolumeItem, VolumeInfo, WikiVolumeItemRemoteKeys>(
    pagingRepo = volumePagingRepo,
    ioDispatcher = ioDispatcher,
) {
    override suspend fun getEndOfPaginationOffset(): Int? = endOfPaginationOffset

    override suspend fun fetch(offset: Int, limit: Int): FetchResult<VolumeInfo> {
        val res = pref.run {
            volumesRepo.getItems(
                offset = offset,
                limit = limit,
                sort = wikiVolumesSort.first().toComicVineSort(),
                filters = wikiVolumesFilters.first().toComicVineFiltersList(),
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
        values: List<PagingWikiVolumeItem>,
        prevOffset: Int?,
        nextOffset: Int?
    ): List<WikiVolumeItemRemoteKeys> = values.map { value ->
        WikiVolumeItemRemoteKeys(
            id = value.index,
            prevOffset = prevOffset,
            nextOffset = nextOffset,
        )
    }

    override fun buildValues(
        offset: Int,
        fetchList: List<VolumeInfo>
    ): List<PagingWikiVolumeItem> {
        var index = offset
        return fetchList.map { PagingWikiVolumeItem(index = index++, info = it) }.toList()
    }
}