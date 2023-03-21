package org.proninyaroslav.opencomicvine.ui.wiki

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
import org.proninyaroslav.opencomicvine.data.paging.wiki.PagingWikiCharacterItem
import org.proninyaroslav.opencomicvine.data.paging.wiki.PagingWikiIssueItem
import org.proninyaroslav.opencomicvine.data.paging.wiki.PagingWikiVolumeItem
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.paging.ComicVineRemoteMediator
import org.proninyaroslav.opencomicvine.model.paging.ComicVineSource
import org.proninyaroslav.opencomicvine.model.paging.wiki.CharactersRemoteMediatorFactory
import org.proninyaroslav.opencomicvine.model.paging.wiki.IssuesRemoteMediatorFactory
import org.proninyaroslav.opencomicvine.model.paging.wiki.VolumesRemoteMediatorFactory
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingCharacterRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingIssueRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingVolumeRepository
import org.proninyaroslav.opencomicvine.model.state.StoreViewModel
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
@HiltViewModel
class WikiViewModel @Inject constructor(
    private val characterItemRepo: PagingCharacterRepository,
    private val issueItemRepo: PagingIssueRepository,
    private val volumeItemRepo: PagingVolumeRepository,
    private val favoritesRepo: FavoritesRepository,
    private val errorReportService: ErrorReportService,
    charactersRemoteMediatorFactory: CharactersRemoteMediatorFactory,
    issuesRemoteMediatorFactory: IssuesRemoteMediatorFactory,
    volumesRemoteMediatorFactory: VolumesRemoteMediatorFactory,
) : StoreViewModel<WikiEvent, Unit, Unit>(Unit) {

    init {
        on<WikiEvent.ErrorReport> { event ->
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

    private fun PagingWikiCharacterItem.toItem(): CharacterItem =
        CharacterItem(
            info = info,
            isFavorite = favoritesRepo.observe(
                entityId = info.id,
                entityType = FavoriteInfo.EntityType.Character,
            )
        )

    private fun PagingWikiIssueItem.toItem(): IssueItem =
        IssueItem(
            info = info,
            isFavorite = favoritesRepo.observe(
                entityId = info.id,
                entityType = FavoriteInfo.EntityType.Issue,
            )
        )

    private fun PagingWikiVolumeItem.toItem(): VolumeItem =
        VolumeItem(
            info = info,
            isFavorite = favoritesRepo.observe(
                entityId = info.id,
                entityType = FavoriteInfo.EntityType.Volume,
            )
        )
}

sealed interface WikiEvent {
    data class ErrorReport(val info: ErrorReportInfo) : WikiEvent
}