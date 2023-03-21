package org.proninyaroslav.opencomicvine.ui.home

import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.proninyaroslav.opencomicvine.data.ErrorReportInfo
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.item.CharacterItem
import org.proninyaroslav.opencomicvine.data.item.IssueItem
import org.proninyaroslav.opencomicvine.data.item.VolumeItem
import org.proninyaroslav.opencomicvine.data.paging.recent.PagingRecentCharacterItem
import org.proninyaroslav.opencomicvine.data.paging.recent.PagingRecentIssueItem
import org.proninyaroslav.opencomicvine.data.paging.recent.PagingRecentVolumeItem
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.paging.ComicVineRemoteMediator
import org.proninyaroslav.opencomicvine.model.paging.ComicVineSource
import org.proninyaroslav.opencomicvine.model.paging.recent.CharactersRemoteMediatorFactory
import org.proninyaroslav.opencomicvine.model.paging.recent.IssuesRemoteMediatorFactory
import org.proninyaroslav.opencomicvine.model.paging.recent.VolumesRemoteMediatorFactory
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingCharacterRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingIssueRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingVolumeRepository
import org.proninyaroslav.opencomicvine.model.state.StoreViewModel
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val characterItemRepo: PagingCharacterRepository,
    private val issueItemRepo: PagingIssueRepository,
    private val volumeItemRepo: PagingVolumeRepository,
    private val favoritesRepo: FavoritesRepository,
    private val errorReportService: ErrorReportService,
    charactersRemoteMediatorFactory: CharactersRemoteMediatorFactory,
    issuesRemoteMediatorFactory: IssuesRemoteMediatorFactory,
    volumesRemoteMediatorFactory: VolumesRemoteMediatorFactory,
) : StoreViewModel<HomeEvent, Unit, Unit>(Unit) {

    init {
        on<HomeEvent.ErrorReport> { event ->
            errorReportService.report(event.info)
        }
    }

    private val miniCharactersRemoteMediator = charactersRemoteMediatorFactory.create(
        endOfPaginationOffset = ComicVineSource.DEFAULT_MINI_PAGE_SIZE - 1,
    )
    private val miniIssuesRemoteMediator = issuesRemoteMediatorFactory.create(
        endOfPaginationOffset = ComicVineSource.DEFAULT_MINI_PAGE_SIZE - 1,
    )
    private val miniVolumesRemoteMediator = volumesRemoteMediatorFactory.create(
        endOfPaginationOffset = ComicVineSource.DEFAULT_MINI_PAGE_SIZE - 1,
    )

    val miniCharactersList: Flow<PagingData<CharacterItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = miniCharactersRemoteMediator,
        pagingSourceFactory = {
            characterItemRepo.getSavedItems(ComicVineSource.DEFAULT_MINI_PAGE_SIZE)
        },
    ).flow
        .map { it.map { info -> info.toItem() } }
        .cachedIn(viewModelScope)

    val miniIssuesList: Flow<PagingData<IssueItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = miniIssuesRemoteMediator,
        pagingSourceFactory = {
            issueItemRepo.getSavedItems(ComicVineSource.DEFAULT_MINI_PAGE_SIZE)
        },
    ).flow
        .map { it.map { info -> info.toItem() } }
        .cachedIn(viewModelScope)

    val miniVolumesList: Flow<PagingData<VolumeItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = miniVolumesRemoteMediator,
        pagingSourceFactory = {
            volumeItemRepo.getSavedItems(ComicVineSource.DEFAULT_MINI_PAGE_SIZE)
        },
    ).flow
        .map { it.map { info -> info.toItem() } }
        .cachedIn(viewModelScope)

    fun <T> toMediatorError(state: LoadState): T? =
        ComicVineRemoteMediator.stateToError(state)

    private fun buildPagingConfig(): PagingConfig = PagingConfig(
        pageSize = ComicVineSource.DEFAULT_MINI_PAGE_SIZE,
        initialLoadSize = ComicVineSource.DEFAULT_MINI_PAGE_SIZE,
    )

    private fun PagingRecentCharacterItem.toItem(): CharacterItem =
        CharacterItem(
            info = info,
            isFavorite = favoritesRepo.observe(
                entityId = info.id,
                entityType = FavoriteInfo.EntityType.Character,
            )
        )

    private fun PagingRecentIssueItem.toItem(): IssueItem =
        IssueItem(
            info = info,
            isFavorite = favoritesRepo.observe(
                entityId = info.id,
                entityType = FavoriteInfo.EntityType.Issue,
            )
        )

    private fun PagingRecentVolumeItem.toItem(): VolumeItem =
        VolumeItem(
            info = info,
            isFavorite = favoritesRepo.observe(
                entityId = info.id,
                entityType = FavoriteInfo.EntityType.Volume,
            )
        )
}

sealed interface HomeEvent {
    data class ErrorReport(val info: ErrorReportInfo) : HomeEvent
}