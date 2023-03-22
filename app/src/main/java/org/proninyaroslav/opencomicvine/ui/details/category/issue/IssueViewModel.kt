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

package org.proninyaroslav.opencomicvine.ui.details.category.issue

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.IssueDetails
import org.proninyaroslav.opencomicvine.data.IssueInfo
import org.proninyaroslav.opencomicvine.data.item.*
import org.proninyaroslav.opencomicvine.di.IoDispatcher
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.paging.details.*
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.IssuesRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.ui.details.buildRelatedEntitiesPagingConfig
import org.proninyaroslav.opencomicvine.ui.details.category.DetailsViewModel
import javax.inject.Inject
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingIssueRepository as RecentIssueItemRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingIssueRepository as WikiIssueItemRepository

@HiltViewModel
class IssueViewModel @Inject constructor(
    private val issuesRepo: IssuesRepository,
    private val wikiIssueItemRepo: WikiIssueItemRepository,
    private val recentIssueItemRepo: RecentIssueItemRepository,
    private val pagingDataFactory: PagingDataFactory,
    private val favoritesRepo: FavoritesRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
    errorReportService: ErrorReportService,
) : DetailsViewModel<IssueDetailsItem, IssueViewModel.RelatedEntities>(
    ioDispatcher,
    errorReportService,
) {
    data class RelatedEntities(
        val creators: Flow<PagingData<PersonItem>>,
        val characters: Flow<PagingData<CharacterItem>>,
        val characterDiedIn: Flow<PagingData<CharacterItem>>,
        val teams: Flow<PagingData<TeamItem>>,
        val disbandedTeams: Flow<PagingData<TeamItem>>,
        val locations: Flow<PagingData<LocationItem>>,
        val concepts: Flow<PagingData<ConceptItem>>,
        val objects: Flow<PagingData<ObjectItem>>,
        val storyArcs: Flow<PagingData<StoryArcItem>>,
    ) : DetailsViewModel.RelatedEntities

    override suspend fun onLoadRemote(entityId: Int): RemoteFetchResult<IssueDetailsItem, RelatedEntities> {
        return when (val res = issuesRepo.getItemDetailsById(entityId)) {
            is ComicVineResult.Success -> {
                val details = res.response.results
                with(pagingDataFactory) {
                    RemoteFetchResult.Success(
                        details = details.toItem(),
                        relatedEntities = RelatedEntities(
                            creators = buildCreators(
                                details = details,
                                coroutineScope = viewModelScope,
                            ),
                            characters = buildCharacters(
                                details = details,
                                coroutineScope = viewModelScope,
                            ),
                            characterDiedIn = buildCharacterDiedIn(
                                details = details,
                                coroutineScope = viewModelScope,
                            ),
                            teams = buildTeams(
                                details = details,
                                coroutineScope = viewModelScope,
                            ),
                            disbandedTeams = buildDisbandedTeams(
                                details = details,
                                coroutineScope = viewModelScope,
                            ),
                            locations = buildLocations(
                                details = details,
                                coroutineScope = viewModelScope,
                            ),
                            concepts = buildConcepts(
                                details = details,
                                coroutineScope = viewModelScope,
                            ),
                            objects = buildObjects(
                                details = details,
                                coroutineScope = viewModelScope,
                            ),
                            storyArcs = buildStoryArcs(
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

    override suspend fun onLoadCache(entityId: Int): CacheFetchResult<IssueDetailsItem?> {
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

    private suspend fun loadFromWikiRepo(id: Int): CacheFetchResult<IssueDetailsItem?> {
        return when (val res = wikiIssueItemRepo.getItemById(id)) {
            is ComicVinePagingRepository.Result.Success -> CacheFetchResult.Success(
                details = res.value?.info?.toDetails()?.toItem()
            )
            is ComicVinePagingRepository.Result.Failed.IO -> CacheFetchResult.Failed.IO(
                exception = res.exception,
            )
        }
    }

    private suspend fun loadFromRecentRepo(id: Int): CacheFetchResult<IssueDetailsItem?> {
        return when (val res = recentIssueItemRepo.getItemById(id)) {
            is ComicVinePagingRepository.Result.Success -> CacheFetchResult.Success(
                details = res.value?.info?.toDetails()?.toItem()
            )
            is ComicVinePagingRepository.Result.Failed.IO -> CacheFetchResult.Failed.IO(
                exception = res.exception,
            )
        }
    }

    private fun IssueInfo.toDetails(): IssueDetails =
        IssueDetails(
            id = id,
            name = name,
            image = image,
            dateAdded = dateAdded,
            dateLastUpdated = dateLastUpdated,
            descriptionShort = null,
            description = null,
            issueNumber = issueNumber,
            volume = volume.run { IssueDetails.Volume(id = id, name = name) },
            coverDate = coverDate,
            storeDate = storeDate,
            associatedImages = emptyList(),
            characterCredits = emptyList(),
            characterDiedIn = emptyList(),
            conceptCredits = emptyList(),
            locationCredits = emptyList(),
            objectCredits = emptyList(),
            personCredits = emptyList(),
            storyArcCredits = emptyList(),
            teamCredits = emptyList(),
            teamDisbandedIn = emptyList(),
        )

    private fun IssueDetails.toItem() =
        IssueDetailsItem(
            details = this,
            isFavorite = favoritesRepo.observe(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Issue,
            )
        )

    class PagingDataFactory @Inject constructor(
        private val peopleSourceFactory: PeopleSourceFactory,
        private val charactersSourceFactory: CharactersSourceFactory,
        private val teamsSourceFactory: TeamsSourceFactory,
        private val locationsSourceFactory: LocationsSourceFactory,
        private val conceptsSourceFactory: ConceptsSourceFactory,
        private val objectsSourceFactory: ObjectsSourceFactory,
        private val storyArcsSourceFactory: StoryArcsSourceFactory,
    ) {
        fun buildCreators(
            details: IssueDetails,
            coroutineScope: CoroutineScope,
        ): Flow<PagingData<PersonItem>> = details.run {
            Pager(
                config = buildRelatedEntitiesPagingConfig(personCredits.size),
                pagingSourceFactory = {
                    peopleSourceFactory.create(personCredits.map { it.id }.toList())
                },
            ).flow.cachedIn(coroutineScope)
        }

        fun buildCharacters(
            details: IssueDetails,
            coroutineScope: CoroutineScope,
        ): Flow<PagingData<CharacterItem>> = details.run {
            Pager(
                config = buildRelatedEntitiesPagingConfig(characterCredits.size),
                pagingSourceFactory = {
                    charactersSourceFactory.create(characterCredits.map { it.id }.toList())
                },
            ).flow.cachedIn(coroutineScope)
        }

        fun buildCharacterDiedIn(
            details: IssueDetails,
            coroutineScope: CoroutineScope,
        ): Flow<PagingData<CharacterItem>> = details.run {
            Pager(
                config = buildRelatedEntitiesPagingConfig(characterDiedIn.size),
                pagingSourceFactory = {
                    charactersSourceFactory.create(characterDiedIn.map { it.id }.toList())
                },
            ).flow.cachedIn(coroutineScope)
        }

        fun buildTeams(
            details: IssueDetails,
            coroutineScope: CoroutineScope,
        ): Flow<PagingData<TeamItem>> = details.run {
            Pager(
                config = buildRelatedEntitiesPagingConfig(teamCredits.size),
                pagingSourceFactory = {
                    teamsSourceFactory.create(teamCredits.map { it.id }.toList())
                },
            ).flow.cachedIn(coroutineScope)
        }

        fun buildDisbandedTeams(
            details: IssueDetails,
            coroutineScope: CoroutineScope,
        ): Flow<PagingData<TeamItem>> = details.run {
            Pager(
                config = buildRelatedEntitiesPagingConfig(teamDisbandedIn.size),
                pagingSourceFactory = {
                    teamsSourceFactory.create(teamDisbandedIn.map { it.id }.toList())
                },
            ).flow.cachedIn(coroutineScope)
        }

        fun buildLocations(
            details: IssueDetails,
            coroutineScope: CoroutineScope,
        ): Flow<PagingData<LocationItem>> = details.run {
            Pager(
                config = buildRelatedEntitiesPagingConfig(locationCredits.size),
                pagingSourceFactory = {
                    locationsSourceFactory.create(locationCredits.map { it.id }.toList())
                },
            ).flow.cachedIn(coroutineScope)
        }

        fun buildConcepts(
            details: IssueDetails,
            coroutineScope: CoroutineScope,
        ): Flow<PagingData<ConceptItem>> = details.run {
            Pager(
                config = buildRelatedEntitiesPagingConfig(conceptCredits.size),
                pagingSourceFactory = {
                    conceptsSourceFactory.create(conceptCredits.map { it.id }.toList())
                },
            ).flow.cachedIn(coroutineScope)
        }

        fun buildObjects(
            details: IssueDetails,
            coroutineScope: CoroutineScope,
        ): Flow<PagingData<ObjectItem>> = details.run {
            Pager(
                config = buildRelatedEntitiesPagingConfig(objectCredits.size),
                pagingSourceFactory = {
                    objectsSourceFactory.create(objectCredits.map { it.id }.toList())
                },
            ).flow.cachedIn(coroutineScope)
        }

        fun buildStoryArcs(
            details: IssueDetails,
            coroutineScope: CoroutineScope,
        ): Flow<PagingData<StoryArcItem>> = details.run {
            Pager(
                config = buildRelatedEntitiesPagingConfig(storyArcCredits.size),
                pagingSourceFactory = {
                    storyArcsSourceFactory.create(storyArcCredits.map { it.id }.toList())
                },
            ).flow.cachedIn(coroutineScope)
        }
    }
}
