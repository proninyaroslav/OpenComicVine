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

package org.proninyaroslav.opencomicvine.ui.favorites.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import org.proninyaroslav.opencomicvine.types.ErrorReportInfo
import org.proninyaroslav.opencomicvine.types.item.favorites.*
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.paging.ComicVineRemoteMediator
import org.proninyaroslav.opencomicvine.model.paging.ComicVineSource
import org.proninyaroslav.opencomicvine.model.paging.favorites.*
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.*
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalPagingApi::class)
class FavoriteCategoryPageViewModel @Inject constructor(
    private val characterItemRepo: PagingCharacterRepository,
    private val issueItemRepo: PagingIssueRepository,
    private val conceptItemRepo: PagingConceptRepository,
    private val locationItemRepo: PagingLocationRepository,
    private val movieItemRepo: PagingMovieRepository,
    private val objectItemRepo: PagingObjectRepository,
    private val personItemRepo: PagingPersonRepository,
    private val volumeItemRepo: PagingVolumeRepository,
    private val storyArcItemRepo: PagingStoryArcRepository,
    private val teamItemRepo: PagingTeamRepository,
    private val errorReportService: ErrorReportService,
    charactersRemoteMediatorFactory: CharactersRemoteMediatorFactory,
    issuesRemoteMediatorFactory: IssuesRemoteMediatorFactory,
    conceptsRemoteMediatorFactory: ConceptsRemoteMediatorFactory,
    locationsRemoteMediatorFactory: LocationsRemoteMediatorFactory,
    moviesRemoteMediatorFactory: MoviesRemoteMediatorFactory,
    objectRemoteMediatorFactory: ObjectsRemoteMediatorFactory,
    peopleRemoteMediatorFactory: PeopleRemoteMediatorFactory,
    volumesRemoteMediatorFactory: VolumesRemoteMediatorFactory,
    storyArcsRemoteMediatorFactory: StoryArcsRemoteMediatorFactory,
    teamsRemoteMediatorFactory: TeamsRemoteMediatorFactory,
) : ViewModel() {

    private val _effect = MutableSharedFlow<FavoriteCategoryPageEffect>()
    val effect: SharedFlow<FavoriteCategoryPageEffect> = _effect

    fun errorReport(info: ErrorReportInfo) {
        errorReportService.report(info)
    }

    private val charactersRemoteMediator = charactersRemoteMediatorFactory.create(
        scope = viewModelScope,
        onRefresh = { _effect.tryEmit(FavoriteCategoryPageEffect.Refresh) },
    )

    private val issuesRemoteMediator = issuesRemoteMediatorFactory.create(
        scope = viewModelScope,
        onRefresh = { _effect.tryEmit(FavoriteCategoryPageEffect.Refresh) },
    )

    private val conceptsRemoteMediator = conceptsRemoteMediatorFactory.create(
        scope = viewModelScope,
        onRefresh = { _effect.tryEmit(FavoriteCategoryPageEffect.Refresh) },
    )

    private val locationsRemoteMediator = locationsRemoteMediatorFactory.create(
        scope = viewModelScope,
        onRefresh = { _effect.tryEmit(FavoriteCategoryPageEffect.Refresh) },
    )

    private val moviesRemoteMediator = moviesRemoteMediatorFactory.create(
        scope = viewModelScope,
        onRefresh = { _effect.tryEmit(FavoriteCategoryPageEffect.Refresh) },
    )

    private val objectsRemoteMediator = objectRemoteMediatorFactory.create(
        scope = viewModelScope,
        onRefresh = { _effect.tryEmit(FavoriteCategoryPageEffect.Refresh) },
    )

    private val peopleRemoteMediator = peopleRemoteMediatorFactory.create(
        scope = viewModelScope,
        onRefresh = { _effect.tryEmit(FavoriteCategoryPageEffect.Refresh) },
    )

    private val volumesRemoteMediator = volumesRemoteMediatorFactory.create(
        scope = viewModelScope,
        onRefresh = { _effect.tryEmit(FavoriteCategoryPageEffect.Refresh) },
    )

    private val storyArcsRemoteMediator = storyArcsRemoteMediatorFactory.create(
        scope = viewModelScope,
        onRefresh = { _effect.tryEmit(FavoriteCategoryPageEffect.Refresh) },
    )

    private val teamsRemoteMediator = teamsRemoteMediatorFactory.create(
        scope = viewModelScope,
        onRefresh = { _effect.tryEmit(FavoriteCategoryPageEffect.Refresh) },
    )

    val charactersList: Flow<PagingData<FavoritesCharacterItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = charactersRemoteMediator,
        pagingSourceFactory = { characterItemRepo.getAllSavedItems() },
    ).flow
        .map { it.map { item -> item.item } }
        .cachedIn(viewModelScope)

    val issuesList: Flow<PagingData<FavoritesIssueItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = issuesRemoteMediator,
        pagingSourceFactory = { issueItemRepo.getAllSavedItems() },
    ).flow
        .map { it.map { item -> item.item } }
        .cachedIn(viewModelScope)

    val conceptsList: Flow<PagingData<FavoritesConceptItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = conceptsRemoteMediator,
        pagingSourceFactory = { conceptItemRepo.getAllSavedItems() },
    ).flow
        .map { it.map { item -> item.item } }
        .cachedIn(viewModelScope)

    val locationsList: Flow<PagingData<FavoritesLocationItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = locationsRemoteMediator,
        pagingSourceFactory = { locationItemRepo.getAllSavedItems() },
    ).flow
        .map { it.map { item -> item.item } }
        .cachedIn(viewModelScope)

    val moviesList: Flow<PagingData<FavoritesMovieItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = moviesRemoteMediator,
        pagingSourceFactory = { movieItemRepo.getAllSavedItems() },
    ).flow
        .map { it.map { item -> item.item } }
        .cachedIn(viewModelScope)

    val objectsList: Flow<PagingData<FavoritesObjectItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = objectsRemoteMediator,
        pagingSourceFactory = { objectItemRepo.getAllSavedItems() },
    ).flow
        .map { it.map { item -> item.item } }
        .cachedIn(viewModelScope)

    val peopleList: Flow<PagingData<FavoritesPersonItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = peopleRemoteMediator,
        pagingSourceFactory = { personItemRepo.getAllSavedItems() },
    ).flow
        .map { it.map { item -> item.item } }
        .cachedIn(viewModelScope)

    val volumesList: Flow<PagingData<FavoritesVolumeItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = volumesRemoteMediator,
        pagingSourceFactory = { volumeItemRepo.getAllSavedItems() },
    ).flow
        .map { it.map { item -> item.item } }
        .cachedIn(viewModelScope)

    val storyArcsList: Flow<PagingData<FavoritesStoryArcItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = storyArcsRemoteMediator,
        pagingSourceFactory = { storyArcItemRepo.getAllSavedItems() },
    ).flow
        .map { it.map { item -> item.item } }
        .cachedIn(viewModelScope)

    val teamsList: Flow<PagingData<FavoritesTeamItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = teamsRemoteMediator,
        pagingSourceFactory = { teamItemRepo.getAllSavedItems() },
    ).flow
        .map { it.map { item -> item.item } }
        .cachedIn(viewModelScope)

    fun <T> toMediatorError(state: LoadState): T? =
        ComicVineRemoteMediator.stateToError(state)

    private fun buildPagingConfig(): PagingConfig = PagingConfig(
        pageSize = ComicVineSource.DEFAULT_PAGE_SIZE,
    )
}

sealed interface FavoriteCategoryPageEffect {
    data object Refresh : FavoriteCategoryPageEffect
}
