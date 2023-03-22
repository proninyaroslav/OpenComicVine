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

package org.proninyaroslav.opencomicvine.ui.details.category.volume

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.VolumeDetails
import org.proninyaroslav.opencomicvine.data.VolumeInfo
import org.proninyaroslav.opencomicvine.data.item.IssueItem
import org.proninyaroslav.opencomicvine.data.item.VolumeDetailsItem
import org.proninyaroslav.opencomicvine.data.item.volume.*
import org.proninyaroslav.opencomicvine.data.preferences.toComicVineSort
import org.proninyaroslav.opencomicvine.di.IoDispatcher
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.paging.details.IssuesSourceFactory
import org.proninyaroslav.opencomicvine.model.paging.details.volume.*
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.VolumesRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.ui.details.buildRelatedEntitiesPagingConfig
import org.proninyaroslav.opencomicvine.ui.details.category.DetailsViewModel
import javax.inject.Inject
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingVolumeRepository as RecentVolumeItemRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingVolumeRepository as WikiVolumeItemRepository

@HiltViewModel
class VolumeViewModel @Inject constructor(
    private val volumesRepo: VolumesRepository,
    private val wikiVolumeItemRepo: WikiVolumeItemRepository,
    private val recentVolumeItemRepo: RecentVolumeItemRepository,
    private val pagingDataFactory: PagingDataFactory,
    private val favoritesRepo: FavoritesRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
    errorReportService: ErrorReportService,
) : DetailsViewModel<VolumeDetailsItem, VolumeViewModel.RelatedEntities>(
    ioDispatcher,
    errorReportService
) {
    data class RelatedEntities(
        val issues: Flow<PagingData<IssueItem>>,
        val characters: Flow<PagingData<VolumeCharacterItem>>,
        val creators: Flow<PagingData<VolumePersonItem>>,
        val locations: Flow<PagingData<VolumeLocationItem>>,
        val concepts: Flow<PagingData<VolumeConceptItem>>,
        val objects: Flow<PagingData<VolumeObjectItem>>,
    ) : DetailsViewModel.RelatedEntities

    override suspend fun onLoadRemote(entityId: Int): RemoteFetchResult<VolumeDetailsItem, RelatedEntities> {
        return when (val res = volumesRepo.getItemDetailsById(entityId)) {
            is ComicVineResult.Success -> {
                val details = res.response.results
                with(pagingDataFactory) {
                    RemoteFetchResult.Success(
                        details = details.toItem(),
                        relatedEntities = RelatedEntities(
                            issues = buildIssues(
                                details = details,
                                coroutineScope = viewModelScope,
                            ),
                            characters = buildCharacters(
                                details = details,
                                coroutineScope = viewModelScope,
                            ),
                            creators = buildPeople(
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
                        )
                    )
                }
            }
            is ComicVineResult.Failed -> RemoteFetchResult.Failed(error = res)
        }
    }

    override suspend fun onLoadCache(entityId: Int): CacheFetchResult<VolumeDetailsItem?> {
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

    private suspend fun loadFromWikiRepo(id: Int): CacheFetchResult<VolumeDetailsItem?> {
        return when (val res = wikiVolumeItemRepo.getItemById(id)) {
            is ComicVinePagingRepository.Result.Success -> CacheFetchResult.Success(
                details = res.value?.info?.toDetails()?.toItem()
            )
            is ComicVinePagingRepository.Result.Failed.IO -> CacheFetchResult.Failed.IO(
                exception = res.exception,
            )
        }
    }

    private suspend fun loadFromRecentRepo(id: Int): CacheFetchResult<VolumeDetailsItem?> {
        return when (val res = recentVolumeItemRepo.getItemById(id)) {
            is ComicVinePagingRepository.Result.Success -> CacheFetchResult.Success(
                details = res.value?.info?.toDetails()?.toItem()
            )
            is ComicVinePagingRepository.Result.Failed.IO -> CacheFetchResult.Failed.IO(
                exception = res.exception,
            )
        }
    }

    private fun VolumeInfo.toDetails(): VolumeDetails =
        VolumeDetails(
            id = id,
            name = name,
            image = image,
            dateAdded = dateAdded,
            dateLastUpdated = dateLastUpdated,
            startYear = startYear,
            publisher = publisher?.run {
                VolumeDetails.Publisher(
                    id = id,
                    name = name,
                )
            },
            firstIssue = firstIssue?.run {
                VolumeDetails.Issue(
                    id = id,
                    name = name,
                    issueNumber = issueNumber,
                )
            },
            lastIssue = lastIssue?.run {
                VolumeDetails.Issue(
                    id = id,
                    name = name,
                    issueNumber = issueNumber,
                )
            },
            _countOfIssues = _countOfIssues,
            descriptionShort = null,
            description = null,
            characters = emptyList(),
            concepts = emptyList(),
            issues = emptyList(),
            locations = emptyList(),
            objects = emptyList(),
            people = emptyList(),
        )

    private fun VolumeDetails.toItem(): VolumeDetailsItem =
        VolumeDetailsItem(
            details = this,
            isFavorite = favoritesRepo.observe(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Volume,
            )
        )

    class PagingDataFactory @Inject constructor(
        private val issuesSourceFactory: IssuesSourceFactory,
        private val charactersSourceFactory: VolumeCharactersSourceFactory,
        private val peopleSourceFactory: VolumePeopleSourceFactory,
        private val locationsSourceFactory: VolumeLocationsSourceFactory,
        private val conceptsSourceFactory: VolumeConceptsSourceFactory,
        private val objectsSourceFactory: VolumeObjectsSourceFactory,
        private val pref: AppPreferences,
    ) {
        fun buildIssues(
            details: VolumeDetails,
            coroutineScope: CoroutineScope,
        ): Flow<PagingData<IssueItem>> = details.run {
            Pager(
                config = buildRelatedEntitiesPagingConfig(issues?.size),
                pagingSourceFactory = {
                    issuesSourceFactory.create(
                        idList = issues?.map { it.id } ?: emptyList(),
                        sort = pref.volumeIssuesSort.map { it.toComicVineSort() },
                    )
                },
            ).flow.cachedIn(coroutineScope)
        }

        fun buildCharacters(
            details: VolumeDetails,
            coroutineScope: CoroutineScope,
        ): Flow<PagingData<VolumeCharacterItem>> = details.run {
            Pager(
                config = buildRelatedEntitiesPagingConfig(characters?.size),
                pagingSourceFactory = {
                    charactersSourceFactory.create(characters ?: emptyList())
                },
            ).flow.cachedIn(coroutineScope)
        }

        fun buildPeople(
            details: VolumeDetails,
            coroutineScope: CoroutineScope,
        ): Flow<PagingData<VolumePersonItem>> = details.run {
            Pager(
                config = buildRelatedEntitiesPagingConfig(people?.size),
                pagingSourceFactory = {
                    peopleSourceFactory.create(people ?: emptyList())
                },
            ).flow.cachedIn(coroutineScope)
        }

        fun buildLocations(
            details: VolumeDetails,
            coroutineScope: CoroutineScope,
        ): Flow<PagingData<VolumeLocationItem>> = details.run {
            Pager(
                config = buildRelatedEntitiesPagingConfig(locations?.size),
                pagingSourceFactory = {
                    locationsSourceFactory.create(locations ?: emptyList())
                },
            ).flow.cachedIn(coroutineScope)
        }

        fun buildConcepts(
            details: VolumeDetails,
            coroutineScope: CoroutineScope,
        ): Flow<PagingData<VolumeConceptItem>> = details.run {
            Pager(
                config = buildRelatedEntitiesPagingConfig(concepts?.size),
                pagingSourceFactory = {
                    conceptsSourceFactory.create(concepts ?: emptyList())
                },
            ).flow.cachedIn(coroutineScope)
        }

        fun buildObjects(
            details: VolumeDetails,
            coroutineScope: CoroutineScope,
        ): Flow<PagingData<VolumeObjectItem>> = details.run {
            Pager(
                config = buildRelatedEntitiesPagingConfig(objects?.size),
                pagingSourceFactory = {
                    objectsSourceFactory.create(objects ?: emptyList())
                },
            ).flow.cachedIn(coroutineScope)
        }
    }
}
