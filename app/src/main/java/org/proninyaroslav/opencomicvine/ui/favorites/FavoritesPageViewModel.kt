package org.proninyaroslav.opencomicvine.ui.favorites

import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import org.proninyaroslav.opencomicvine.data.ErrorReportInfo
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.item.favorites.*
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.paging.ComicVineRemoteMediator
import org.proninyaroslav.opencomicvine.model.paging.ComicVineSource
import org.proninyaroslav.opencomicvine.model.paging.favorites.*
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.*
import org.proninyaroslav.opencomicvine.model.state.StoreViewModel
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalPagingApi::class)
class FavoritesPageViewModel @Inject constructor(
    private val characterItemRepo: PagingCharacterRepository,
    private val issueItemRepo: PagingIssueRepository,
    private val volumeItemRepo: PagingVolumeRepository,
    private val conceptItemRepo: PagingConceptRepository,
    private val locationItemRepo: PagingLocationRepository,
    private val moviesItemRepo: PagingMovieRepository,
    private val objectsItemRepo: PagingObjectRepository,
    private val peopleItemRepo: PagingPersonRepository,
    private val storyArcsItemRepo: PagingStoryArcRepository,
    private val teamsItemRepo: PagingTeamRepository,
    private val errorReportService: ErrorReportService,
    charactersRemoteMediatorFactory: CharactersRemoteMediatorFactory,
    issuesRemoteMediatorFactory: IssuesRemoteMediatorFactory,
    volumesRemoteMediatorFactory: VolumesRemoteMediatorFactory,
    conceptsRemoteMediatorFactory: ConceptsRemoteMediatorFactory,
    locationsRemoteMediatorFactory: LocationsRemoteMediatorFactory,
    moviesRemoteMediatorFactory: MoviesRemoteMediatorFactory,
    objectsRemoteMediatorFactory: ObjectsRemoteMediatorFactory,
    peopleRemoteMediatorFactory: PeopleRemoteMediatorFactory,
    storyArcsRemoteMediatorFactory: StoryArcsRemoteMediatorFactory,
    teamsRemoteMediatorFactory: TeamsRemoteMediatorFactory,
) : StoreViewModel<
        FavoritesPageEvent,
        Unit,
        FavoritesPageEffect>
    (Unit) {

    init {
        on<FavoritesPageEvent.ErrorReport> { event ->
            errorReportService.report(event.info)
        }
    }

    private val charactersRemoteMediator = charactersRemoteMediatorFactory.create(
        scope = viewModelScope,
        onRefresh = {
            emitEffect(
                FavoritesPageEffect.Refresh(FavoriteInfo.EntityType.Character)
            )
        },
    )
    private val issuesRemoteMediator = issuesRemoteMediatorFactory.create(
        scope = viewModelScope,
        onRefresh = {
            emitEffect(
                FavoritesPageEffect.Refresh(FavoriteInfo.EntityType.Issue)
            )
        },
    )
    private val volumesRemoteMediator = volumesRemoteMediatorFactory.create(
        scope = viewModelScope,
        onRefresh = {
            emitEffect(
                FavoritesPageEffect.Refresh(FavoriteInfo.EntityType.Volume)
            )
        },
    )
    private val conceptsRemoteMediator = conceptsRemoteMediatorFactory.create(
        scope = viewModelScope,
        onRefresh = {
            emitEffect(
                FavoritesPageEffect.Refresh(FavoriteInfo.EntityType.Concept)
            )
        },
    )
    private val locationsRemoteMediator = locationsRemoteMediatorFactory.create(
        scope = viewModelScope,
        onRefresh = {
            emitEffect(
                FavoritesPageEffect.Refresh(FavoriteInfo.EntityType.Location)
            )
        },
    )
    private val moviesRemoteMediator = moviesRemoteMediatorFactory.create(
        scope = viewModelScope,
        onRefresh = {
            emitEffect(
                FavoritesPageEffect.Refresh(FavoriteInfo.EntityType.Movie)
            )
        },
    )
    private val objectsRemoteMediator = objectsRemoteMediatorFactory.create(
        scope = viewModelScope,
        onRefresh = {
            emitEffect(
                FavoritesPageEffect.Refresh(FavoriteInfo.EntityType.Object)
            )
        },
    )
    private val peopleRemoteMediator = peopleRemoteMediatorFactory.create(
        scope = viewModelScope,
        onRefresh = {
            emitEffect(
                FavoritesPageEffect.Refresh(FavoriteInfo.EntityType.Person)
            )
        },
    )
    private val storyArcsRemoteMediator = storyArcsRemoteMediatorFactory.create(
        scope = viewModelScope,
        onRefresh = {
            emitEffect(
                FavoritesPageEffect.Refresh(FavoriteInfo.EntityType.StoryArc)
            )
        },
    )
    private val teamsRemoteMediator = teamsRemoteMediatorFactory.create(
        scope = viewModelScope,
        onRefresh = {
            emitEffect(
                FavoritesPageEffect.Refresh(FavoriteInfo.EntityType.Team)
            )
        },
    )

    val miniCharactersList: Flow<PagingData<FavoritesCharacterItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = charactersRemoteMediator,
        pagingSourceFactory = {
            characterItemRepo.getSavedItems(ComicVineSource.DEFAULT_MINI_PAGE_SIZE)
        },
    ).flow
        .map { it.map { item -> item.item } }
        .cachedIn(viewModelScope)

    val miniIssuesList: Flow<PagingData<FavoritesIssueItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = issuesRemoteMediator,
        pagingSourceFactory = {
            issueItemRepo.getSavedItems(ComicVineSource.DEFAULT_MINI_PAGE_SIZE)
        },
    ).flow
        .map { it.map { item -> item.item } }
        .cachedIn(viewModelScope)

    val miniVolumesList: Flow<PagingData<FavoritesVolumeItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = volumesRemoteMediator,
        pagingSourceFactory = {
            volumeItemRepo.getSavedItems(ComicVineSource.DEFAULT_MINI_PAGE_SIZE)
        },
    ).flow
        .map { it.map { item -> item.item } }
        .cachedIn(viewModelScope)

    val miniConceptsList: Flow<PagingData<FavoritesConceptItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = conceptsRemoteMediator,
        pagingSourceFactory = {
            conceptItemRepo.getSavedItems(ComicVineSource.DEFAULT_MINI_PAGE_SIZE)
        },
    ).flow
        .map { it.map { item -> item.item } }
        .cachedIn(viewModelScope)

    val miniLocationsList: Flow<PagingData<FavoritesLocationItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = locationsRemoteMediator,
        pagingSourceFactory = {
            locationItemRepo.getSavedItems(ComicVineSource.DEFAULT_MINI_PAGE_SIZE)
        },
    ).flow
        .map { it.map { item -> item.item } }
        .cachedIn(viewModelScope)

    val miniMoviesList: Flow<PagingData<FavoritesMovieItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = moviesRemoteMediator,
        pagingSourceFactory = {
            moviesItemRepo.getSavedItems(ComicVineSource.DEFAULT_MINI_PAGE_SIZE)
        },
    ).flow
        .map { it.map { item -> item.item } }
        .cachedIn(viewModelScope)

    val miniObjectsList: Flow<PagingData<FavoritesObjectItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = objectsRemoteMediator,
        pagingSourceFactory = {
            objectsItemRepo.getSavedItems(ComicVineSource.DEFAULT_MINI_PAGE_SIZE)
        },
    ).flow
        .map { it.map { item -> item.item } }
        .cachedIn(viewModelScope)

    val miniPeopleList: Flow<PagingData<FavoritesPersonItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = peopleRemoteMediator,
        pagingSourceFactory = {
            peopleItemRepo.getSavedItems(ComicVineSource.DEFAULT_MINI_PAGE_SIZE)
        },
    ).flow
        .map { it.map { item -> item.item } }
        .cachedIn(viewModelScope)

    val miniStoryArcsList: Flow<PagingData<FavoritesStoryArcItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = storyArcsRemoteMediator,
        pagingSourceFactory = {
            storyArcsItemRepo.getSavedItems(ComicVineSource.DEFAULT_MINI_PAGE_SIZE)
        },
    ).flow
        .map { it.map { item -> item.item } }
        .cachedIn(viewModelScope)

    val miniTeamsList: Flow<PagingData<FavoritesTeamItem>> = Pager(
        config = buildPagingConfig(),
        remoteMediator = teamsRemoteMediator,
        pagingSourceFactory = {
            teamsItemRepo.getSavedItems(ComicVineSource.DEFAULT_MINI_PAGE_SIZE)
        },
    ).flow
        .map { it.map { item -> item.item } }
        .cachedIn(viewModelScope)

    fun <T> toMediatorError(state: LoadState): T? =
        ComicVineRemoteMediator.stateToError(state)

    private fun buildPagingConfig(): PagingConfig = PagingConfig(
        pageSize = ComicVineSource.DEFAULT_MINI_PAGE_SIZE,
        initialLoadSize = ComicVineSource.DEFAULT_MINI_PAGE_SIZE,
    )
}

sealed interface FavoritesPageEvent {
    data class ErrorReport(val info: ErrorReportInfo) : FavoritesPageEvent
}


sealed interface FavoritesPageEffect {
    data class Refresh(val entityType: FavoriteInfo.EntityType) : FavoritesPageEffect
}