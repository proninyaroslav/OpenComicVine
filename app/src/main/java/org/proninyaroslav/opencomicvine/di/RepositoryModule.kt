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

package org.proninyaroslav.opencomicvine.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.proninyaroslav.opencomicvine.model.repo.*
import javax.inject.Singleton
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingCharacterRepository as FavoritePagingCharacterRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingCharacterRepositoryImpl as FavoritePagingCharacterRepositoryImpl
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingConceptRepository as FavoritePagingConceptRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingConceptRepositoryImpl as FavoritePagingConceptRepositoryImpl
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingIssueRepository as FavoritePagingIssueRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingIssueRepositoryImpl as FavoritePagingIssueRepositoryImpl
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingLocationRepository as FavoritePagingLocationRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingLocationRepositoryImpl as FavoritePagingLocationRepositoryImpl
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingMovieRepository as FavoritePagingMovieRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingMovieRepositoryImpl as FavoritePagingMovieRepositoryImpl
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingObjectRepository as FavoritePagingObjectRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingObjectRepositoryImpl as FavoritePagingObjectRepositoryImpl
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingPersonRepository as FavoritePagingPersonRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingPersonRepositoryImpl as FavoritePagingPersonRepositoryImpl
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingStoryArcRepository as FavoritePagingStoryArcRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingStoryArcRepositoryImpl as FavoritePagingStoryArcRepositoryImpl
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingTeamRepository as FavoritePagingTeamRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingTeamRepositoryImpl as FavoritePagingTeamRepositoryImpl
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingVolumeRepository as FavoritePagingVolumeRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.favorites.PagingVolumeRepositoryImpl as FavoritePagingVolumeRepositoryImpl
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingCharacterRepository as RecentPagingCharacterRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingCharacterRepositoryImpl as RecentPagingCharacterRepositoryImpl
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingIssueRepository as RecentPagingIssueRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingIssueRepositoryImpl as RecentPagingIssueRepositoryImpl
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingVolumeRepository as RecentPagingVolumeRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingVolumeRepositoryImpl as RecentPagingVolumeRepositoryImpl
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingCharacterRepository as WikiPagingCharacterRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingCharacterRepositoryImpl as WikiPagingCharacterRepositoryImpl
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingIssueRepository as WikiPagingIssueRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingIssueRepositoryImpl as WikiPagingIssueRepositoryImpl
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingVolumeRepository as WikiPagingVolumeRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingVolumeRepositoryImpl as WikiPagingVolumeRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindCharactersRepository(repo: CharactersRepositoryImpl): CharactersRepository

    @Singleton
    @Binds
    abstract fun bindApiKeyRepository(repo: ApiKeyRepositoryImpl): ApiKeyRepository

    @Singleton
    @Binds
    abstract fun bindWikiPagingCharacterRepository(repo: WikiPagingCharacterRepositoryImpl): WikiPagingCharacterRepository

    @Singleton
    @Binds
    abstract fun bindRecentPagingCharacterRepository(repo: RecentPagingCharacterRepositoryImpl): RecentPagingCharacterRepository

    @Singleton
    @Binds
    abstract fun bindIssuesRepository(repo: IssuesRepositoryImpl): IssuesRepository

    @Singleton
    @Binds
    abstract fun bindWikiPagingIssueRepository(repo: WikiPagingIssueRepositoryImpl): WikiPagingIssueRepository

    @Singleton
    @Binds
    abstract fun bindRecentPagingIssueRepository(repo: RecentPagingIssueRepositoryImpl): RecentPagingIssueRepository

    @Singleton
    @Binds
    abstract fun bindVolumesRepository(repo: VolumesRepositoryImpl): VolumesRepository

    @Singleton
    @Binds
    abstract fun bindWikiPagingVolumeRepository(repo: WikiPagingVolumeRepositoryImpl): WikiPagingVolumeRepository

    @Singleton
    @Binds
    abstract fun bindRecentPagingVolumeRepository(repo: RecentPagingVolumeRepositoryImpl): RecentPagingVolumeRepository

    @Singleton
    @Binds
    abstract fun bindPeopleRepository(repo: PeopleRepositoryImpl): PeopleRepository

    @Singleton
    @Binds
    abstract fun bindLocationsRepository(repo: LocationsRepositoryImpl): LocationsRepository

    @Singleton
    @Binds
    abstract fun bindConceptsRepository(repo: ConceptsRepositoryImpl): ConceptsRepository

    @Singleton
    @Binds
    abstract fun bindObjectsRepository(repo: ObjectsRepositoryImpl): ObjectsRepository

    @Singleton
    @Binds
    abstract fun bindMoviesRepository(repo: MoviesRepositoryImpl): MoviesRepository

    @Singleton
    @Binds
    abstract fun bindStoryArcsRepository(repo: StoryArcsRepositoryImpl): StoryArcsRepository

    @Singleton
    @Binds
    abstract fun bindTeamsRepository(repo: TeamsRepositoryImpl): TeamsRepository

    @Singleton
    @Binds
    abstract fun bindFavoritesRepository(repo: FavoritesRepositoryImpl): FavoritesRepository

    @Singleton
    @Binds
    abstract fun bindFavoritePagingCharacterRepository(repo: FavoritePagingCharacterRepositoryImpl): FavoritePagingCharacterRepository

    @Singleton
    @Binds
    abstract fun bindFavoritePagingIssueRepository(repo: FavoritePagingIssueRepositoryImpl): FavoritePagingIssueRepository

    @Singleton
    @Binds
    abstract fun bindFavoritePagingVolumeRepository(repo: FavoritePagingVolumeRepositoryImpl): FavoritePagingVolumeRepository

    @Singleton
    @Binds
    abstract fun bindFavoritePagingConceptRepository(repo: FavoritePagingConceptRepositoryImpl): FavoritePagingConceptRepository

    @Singleton
    @Binds
    abstract fun bindFavoritePagingLocationRepository(repo: FavoritePagingLocationRepositoryImpl): FavoritePagingLocationRepository

    @Singleton
    @Binds
    abstract fun bindFavoritePagingMovieRepository(repo: FavoritePagingMovieRepositoryImpl): FavoritePagingMovieRepository

    @Singleton
    @Binds
    abstract fun bindFavoritePagingObjectRepository(repo: FavoritePagingObjectRepositoryImpl): FavoritePagingObjectRepository

    @Singleton
    @Binds
    abstract fun bindFavoritePagingPersonRepository(repo: FavoritePagingPersonRepositoryImpl): FavoritePagingPersonRepository

    @Singleton
    @Binds
    abstract fun bindFavoritePagingStoryArcRepository(repo: FavoritePagingStoryArcRepositoryImpl): FavoritePagingStoryArcRepository

    @Singleton
    @Binds
    abstract fun bindFavoritePagingTeamRepository(repo: FavoritePagingTeamRepositoryImpl): FavoritePagingTeamRepository

    @Singleton
    @Binds
    abstract fun bindComicVineSearchRepository(repo: SearchRepositoryImpl): SearchRepository

    @Singleton
    @Binds
    abstract fun bindSearchHistoryRepository(repo: SearchHistoryRepositoryImpl): SearchHistoryRepository
}
