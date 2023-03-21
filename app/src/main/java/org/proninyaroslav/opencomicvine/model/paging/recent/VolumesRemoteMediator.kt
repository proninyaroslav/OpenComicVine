package org.proninyaroslav.opencomicvine.model.paging.recent

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.VolumeInfo
import org.proninyaroslav.opencomicvine.data.paging.recent.PagingRecentVolumeItem
import org.proninyaroslav.opencomicvine.data.paging.recent.RecentVolumeItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.preferences.toComicVineFiltersList
import org.proninyaroslav.opencomicvine.data.sort.ComicVineSortDirection
import org.proninyaroslav.opencomicvine.data.sort.VolumesSort
import org.proninyaroslav.opencomicvine.di.IoDispatcher
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.VolumesRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingVolumeRepository

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
) : RecentEntityRemoteMediator<PagingRecentVolumeItem, VolumeInfo, RecentVolumeItemRemoteKeys>(
    pagingRepo = volumePagingRepo,
    ioDispatcher = ioDispatcher,
) {
    override suspend fun getEndOfPaginationOffset(): Int? = endOfPaginationOffset

    override suspend fun fetch(offset: Int, limit: Int): FetchResult<VolumeInfo> {
        val res = volumesRepo.getItems(
            offset = offset,
            limit = limit,
            sort = VolumesSort.DateAdded(ComicVineSortDirection.Desc),
            filters = pref.recentVolumesFilters.first().toComicVineFiltersList(),
        )
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
        values: List<PagingRecentVolumeItem>,
        prevOffset: Int?,
        nextOffset: Int?
    ): List<RecentVolumeItemRemoteKeys> = values.map { value ->
        RecentVolumeItemRemoteKeys(
            id = value.index,
            prevOffset = prevOffset,
            nextOffset = nextOffset,
        )
    }

    override fun buildValues(
        offset: Int,
        fetchList: List<VolumeInfo>
    ): List<PagingRecentVolumeItem> {
        var index = offset
        return fetchList.map { PagingRecentVolumeItem(index = index++, info = it) }.toList()
    }
}