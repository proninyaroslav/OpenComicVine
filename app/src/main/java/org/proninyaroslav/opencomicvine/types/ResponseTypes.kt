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

package org.proninyaroslav.opencomicvine.types

typealias CharactersResponse = ComicVineResponse<List<CharacterInfo>>

typealias IssuesResponse = ComicVineResponse<List<IssueInfo>>

typealias VolumesResponse = ComicVineResponse<List<VolumeInfo>>

typealias CharacterResponse = ComicVineResponse<CharacterDetails>

typealias IssueResponse = ComicVineResponse<IssueDetails>

typealias VolumeResponse = ComicVineResponse<VolumeDetails>

typealias PeopleResponse = ComicVineResponse<List<PersonInfo>>

typealias LocationsResponse = ComicVineResponse<List<LocationInfo>>

typealias ConceptsResponse = ComicVineResponse<List<ConceptInfo>>

typealias ObjectsResponse = ComicVineResponse<List<ObjectInfo>>

typealias MoviesResponse = ComicVineResponse<List<MovieInfo>>

typealias StoryArcsResponse = ComicVineResponse<List<StoryArcInfo>>

typealias TeamsResponse = ComicVineResponse<List<TeamInfo>>

typealias SearchResponse = ComicVineResponse<List<SearchInfo>>

typealias SearchStoryArcsResponse = ComicVineResponse<List<SearchInfo.StoryArc>>

typealias SearchObjectsResponse = ComicVineResponse<List<SearchInfo.Object>>
