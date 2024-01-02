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

package org.proninyaroslav.opencomicvine.ui.wiki

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.proninyaroslav.opencomicvine.types.ErrorReportInfo
import org.proninyaroslav.opencomicvine.types.FavoriteInfo
import org.proninyaroslav.opencomicvine.types.item.CharacterItem
import org.proninyaroslav.opencomicvine.types.item.IssueItem
import org.proninyaroslav.opencomicvine.types.item.VolumeItem
import org.proninyaroslav.opencomicvine.types.paging.wiki.PagingWikiCharacterItem
import org.proninyaroslav.opencomicvine.types.paging.wiki.PagingWikiIssueItem
import org.proninyaroslav.opencomicvine.types.paging.wiki.PagingWikiVolumeItem
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
) : ViewModel() {

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

    fun errorReport(info: ErrorReportInfo) {
        errorReportService.report(info)
    }

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