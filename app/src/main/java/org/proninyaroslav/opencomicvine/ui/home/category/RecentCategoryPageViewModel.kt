/*
 * Copyright (C) 2023 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of OpenComicVine.
 *
 * OpenComicVine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenComicVine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenComicVine.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.proninyaroslav.opencomicvine.ui.home.category

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
class RecentCategoryPageViewModel @Inject constructor(
    private val characterItemRepo: PagingCharacterRepository,
    private val favoritesRepo: FavoritesRepository,
    private val issueItemRepo: PagingIssueRepository,
    private val volumeItemRepo: PagingVolumeRepository,
    private val errorReportService: ErrorReportService,
    charactersRemoteMediatorFactory: CharactersRemoteMediatorFactory,
    issuesRemoteMediatorFactory: IssuesRemoteMediatorFactory,
    volumesRemoteMediatorFactory: VolumesRemoteMediatorFactory,
) : StoreViewModel<RecentCategoryPageEvent, Unit, RecentCategoryPageEffect>(Unit) {

    init {
        on<RecentCategoryPageEvent.ErrorReport> { event ->
            errorReportService.report(event.info)
        }
    }

    private val charactersRemoteMediator = charactersRemoteMediatorFactory.create()

    private val issuesRemoteMediator = issuesRemoteMediatorFactory.create()

    private val volumesRemoteMediator = volumesRemoteMediatorFactory.create()

    val charactersList: Flow<PagingData<CharacterItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = charactersRemoteMediator,
        pagingSourceFactory = { characterItemRepo.getAllSavedItems() },
    ).flow
        .map { it.map { info -> info.toItem() } }
        .cachedIn(viewModelScope)

    val issuesList: Flow<PagingData<IssueItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = issuesRemoteMediator,
        pagingSourceFactory = { issueItemRepo.getAllSavedItems() },
    ).flow
        .map { it.map { info -> info.toItem() } }
        .cachedIn(viewModelScope)

    fun <T> toMediatorError(state: LoadState): T? =
        ComicVineRemoteMediator.stateToError(state)

    val volumesList: Flow<PagingData<VolumeItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = volumesRemoteMediator,
        pagingSourceFactory = { volumeItemRepo.getAllSavedItems() },
    ).flow
        .map { it.map { info -> info.toItem() } }
        .cachedIn(viewModelScope)

    private fun buildPagingConfig(): PagingConfig = PagingConfig(
        pageSize = ComicVineSource.DEFAULT_PAGE_SIZE,
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

sealed interface RecentCategoryPageEvent {
    data class ErrorReport(val info: ErrorReportInfo) : RecentCategoryPageEvent
}

sealed interface RecentCategoryPageEffect {
    data class GetFavoriteFailed(val error: FavoritesRepository.Result.Failed) :
        RecentCategoryPageEffect
}
