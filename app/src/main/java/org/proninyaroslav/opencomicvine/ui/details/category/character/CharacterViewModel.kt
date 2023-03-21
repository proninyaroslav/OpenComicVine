package org.proninyaroslav.opencomicvine.ui.details.category.character

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.proninyaroslav.opencomicvine.data.CharacterDetails
import org.proninyaroslav.opencomicvine.data.CharacterInfo
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.item.*
import org.proninyaroslav.opencomicvine.di.IoDispatcher
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.paging.details.*
import org.proninyaroslav.opencomicvine.model.repo.CharactersRepository
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.ui.details.buildRelatedEntitiesPagingConfig
import org.proninyaroslav.opencomicvine.ui.details.category.DetailsViewModel
import javax.inject.Inject
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingCharacterRepository as RecentCharacterItemRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingCharacterRepository as WikiCharacterItemRepository

@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val charactersRepo: CharactersRepository,
    private val wikiCharacterItemRepo: WikiCharacterItemRepository,
    private val recentCharacterItemRepo: RecentCharacterItemRepository,
    private val pagingDataFactory: PagingDataFactory,
    private val favoritesRepo: FavoritesRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
    errorReportService: ErrorReportService,
) : DetailsViewModel<CharacterDetailsItem, CharacterViewModel.RelatedEntities>(
    ioDispatcher,
    errorReportService,
) {
    data class RelatedEntities(
        val movies: Flow<PagingData<MovieItem>>,
        val issues: Flow<PagingData<IssueItem>>,
        val volumes: Flow<PagingData<VolumeItem>>,
        val storyArcs: Flow<PagingData<StoryArcItem>>,
        val friends: Flow<PagingData<CharacterItem>>,
        val enemies: Flow<PagingData<CharacterItem>>,
        val teams: Flow<PagingData<TeamItem>>,
        val teamEnemies: Flow<PagingData<TeamItem>>,
        val teamFriends: Flow<PagingData<TeamItem>>,
    ) : DetailsViewModel.RelatedEntities

    override suspend fun onLoadRemote(entityId: Int): RemoteFetchResult<CharacterDetailsItem, RelatedEntities> {
        return when (val res = charactersRepo.getItemDetailsById(entityId)) {
            is ComicVineResult.Success -> {
                val details = res.response.results
                with(pagingDataFactory) {
                    RemoteFetchResult.Success(
                        details = details.toItem(),
                        relatedEntities = RelatedEntities(
                            movies = buildMovies(
                                details = details,
                                coroutineScope = viewModelScope,
                            ),
                            issues = buildIssues(
                                details = details,
                                coroutineScope = viewModelScope,
                            ),
                            volumes = buildVolumes(
                                details = details,
                                coroutineScope = viewModelScope,
                            ),
                            storyArcs = buildStoryArcs(
                                details = details,
                                coroutineScope = viewModelScope,
                            ),
                            friends = buildFriends(
                                details = details,
                                coroutineScope = viewModelScope,
                            ),
                            enemies = buildEnemies(
                                details = details,
                                coroutineScope = viewModelScope,
                            ),
                            teams = buildTeams(
                                details = details,
                                coroutineScope = viewModelScope,
                            ),
                            teamEnemies = buildTeamEnemies(
                                details = details,
                                coroutineScope = viewModelScope,
                            ),
                            teamFriends = buildTeamFriends(
                                details = details,
                                coroutineScope = viewModelScope,
                            ),
                        )
                    )
                }
            }
            is ComicVineResult.Failed -> RemoteFetchResult.Failed(error = res)
        }
    }

    override suspend fun onLoadCache(entityId: Int): CacheFetchResult<CharacterDetailsItem?> {
        return loadFromWikiRepo(entityId).let {
            when (it) {
                is CacheFetchResult.Success -> if (it.details == null) {
                    loadFromRecentRepo(entityId)
                } else {
                    it
                }
                is CacheFetchResult.Failed -> loadFromRecentRepo(entityId)
            }
        }
    }

    private suspend fun loadFromWikiRepo(id: Int): CacheFetchResult<CharacterDetailsItem?> {
        return when (val res = wikiCharacterItemRepo.getItemById(id)) {
            is ComicVinePagingRepository.Result.Success -> CacheFetchResult.Success(
                details = res.value?.info?.toDetails()?.toItem()
            )
            is ComicVinePagingRepository.Result.Failed.IO -> CacheFetchResult.Failed.IO(
                exception = res.exception,
            )
        }
    }

    private suspend fun loadFromRecentRepo(id: Int): CacheFetchResult<CharacterDetailsItem?> {
        return when (val res = recentCharacterItemRepo.getItemById(id)) {
            is ComicVinePagingRepository.Result.Success -> CacheFetchResult.Success(
                details = res.value?.info?.toDetails()?.toItem()
            )
            is ComicVinePagingRepository.Result.Failed.IO -> CacheFetchResult.Failed.IO(
                exception = res.exception,
            )
        }
    }

    private fun CharacterInfo.toDetails(): CharacterDetails =
        CharacterDetails(
            id = id,
            name = name,
            gender = gender,
            image = image,
            dateAdded = dateAdded,
            dateLastUpdated = dateLastUpdated,
            aliases = null,
            birth = null,
            enemies = emptyList(),
            friends = emptyList(),
            countOfIssueAppearances = 0,
            creators = emptyList(),
            descriptionShort = null,
            description = null,
            firstAppearedInIssue = null,
            issueCredits = emptyList(),
            issuesDiedIn = emptyList(),
            movies = emptyList(),
            origin = null,
            powers = emptyList(),
            publisher = null,
            realName = null,
            storyArcCredits = emptyList(),
            teamFriends = emptyList(),
            teamEnemies = emptyList(),
            teams = emptyList(),
            volumeCredits = emptyList(),
        )

    private fun CharacterDetails.toItem() =
        CharacterDetailsItem(
            details = this,
            isFavorite = favoritesRepo.observe(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Character,
            )
        )

    class PagingDataFactory @Inject constructor(
        private val issuesSourceFactory: IssuesSourceFactory,
        private val moviesSourceFactory: MoviesSourceFactory,
        private val volumesSourceFactory: VolumesSourceFactory,
        private val storyArcsSourceFactory: StoryArcsSourceFactory,
        private val charactersSourceFactory: CharactersSourceFactory,
        private val teamsSourceFactory: TeamsSourceFactory,
    ) {
        fun buildMovies(
            details: CharacterDetails,
            coroutineScope: CoroutineScope,
        ): Flow<PagingData<MovieItem>> = details.run {
            Pager(
                config = buildRelatedEntitiesPagingConfig(movies.size),
                pagingSourceFactory = {
                    moviesSourceFactory.create(movies.map { it.id }.toList())
                },
            ).flow.cachedIn(coroutineScope)
        }

        fun buildIssues(
            details: CharacterDetails,
            coroutineScope: CoroutineScope,
        ): Flow<PagingData<IssueItem>> = details.run {
            Pager(
                config = buildRelatedEntitiesPagingConfig(issueCredits.size),
                pagingSourceFactory = {
                    issuesSourceFactory.create(issueCredits.map { it.id }.toList())
                },
            ).flow.cachedIn(coroutineScope)
        }

        fun buildVolumes(
            details: CharacterDetails,
            coroutineScope: CoroutineScope,
        ): Flow<PagingData<VolumeItem>> = details.run {
            Pager(
                config = buildRelatedEntitiesPagingConfig(volumeCredits.size),
                pagingSourceFactory = {
                    volumesSourceFactory.create(volumeCredits.map { it.id }.toList())
                },
            ).flow.cachedIn(coroutineScope)
        }

        fun buildStoryArcs(
            details: CharacterDetails,
            coroutineScope: CoroutineScope,
        ): Flow<PagingData<StoryArcItem>> = details.run {
            Pager(
                config = buildRelatedEntitiesPagingConfig(storyArcCredits.size),
                pagingSourceFactory = {
                    storyArcsSourceFactory.create(storyArcCredits.map { it.id }.toList())
                },
            ).flow.cachedIn(coroutineScope)
        }

        fun buildFriends(
            details: CharacterDetails,
            coroutineScope: CoroutineScope,
        ): Flow<PagingData<CharacterItem>> = details.run {
            Pager(
                config = buildRelatedEntitiesPagingConfig(friends.size),
                pagingSourceFactory = {
                    charactersSourceFactory.create(friends.map { it.id }.toList())
                },
            ).flow.cachedIn(coroutineScope)
        }

        fun buildEnemies(
            details: CharacterDetails,
            coroutineScope: CoroutineScope,
        ): Flow<PagingData<CharacterItem>> = details.run {
            Pager(
                config = buildRelatedEntitiesPagingConfig(enemies.size),
                pagingSourceFactory = {
                    charactersSourceFactory.create(enemies.map { it.id }.toList())
                },
            ).flow.cachedIn(coroutineScope)
        }

        fun buildTeams(
            details: CharacterDetails,
            coroutineScope: CoroutineScope,
        ): Flow<PagingData<TeamItem>> = details.run {
            Pager(
                config = buildRelatedEntitiesPagingConfig(teams.size),
                pagingSourceFactory = {
                    teamsSourceFactory.create(teams.map { it.id }.toList())
                },
            ).flow.cachedIn(coroutineScope)
        }

        fun buildTeamFriends(
            details: CharacterDetails,
            coroutineScope: CoroutineScope,
        ): Flow<PagingData<TeamItem>> = details.run {
            Pager(
                config = buildRelatedEntitiesPagingConfig(teamFriends.size),
                pagingSourceFactory = {
                    teamsSourceFactory.create(teamFriends.map { it.id }.toList())
                },
            ).flow.cachedIn(coroutineScope)
        }

        fun buildTeamEnemies(
            details: CharacterDetails,
            coroutineScope: CoroutineScope,
        ): Flow<PagingData<TeamItem>> = details.run {
            Pager(
                config = buildRelatedEntitiesPagingConfig(teamEnemies.size),
                pagingSourceFactory = {
                    teamsSourceFactory.create(teamEnemies.map { it.id }.toList())
                },
            ).flow.cachedIn(coroutineScope)
        }
    }
}