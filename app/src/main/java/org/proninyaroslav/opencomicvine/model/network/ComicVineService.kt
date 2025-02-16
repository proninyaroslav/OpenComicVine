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

package org.proninyaroslav.opencomicvine.model.network

import com.skydoves.sandwich.ApiResponse
import org.proninyaroslav.opencomicvine.types.*
import org.proninyaroslav.opencomicvine.types.filter.ComicVineFilter
import org.proninyaroslav.opencomicvine.types.sort.ComicVineSort
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val FORMAT = "json"

private object Fields {
    const val CHARACTERS =
        "id,name,gender,image,date_added,date_last_updated"

    const val ISSUES =
        "id,name,volume,issue_number,image,date_added,date_last_updated,cover_date,store_date"

    const val VOLUMES =
        "id,name,publisher,count_of_issues,date_added,date_last_updated,start_year,image,first_issue,last_issue"

    const val PEOPLE = "id,name,image"

    const val LOCATIONS = "id,name,image"

    const val CONCEPTS = "id,name,image"

    const val OBJECTS = "id,name,image"

    const val MOVIES = "id,name,image"

    const val STORY_ARCS = "id,name,image"

    const val TEAMS = "id,name,image"

    const val SEARCH = "id,name,image,deck,issue_number,cover_date,volume,publisher,start_year," +
            "first_issue,last_issue,count_of_issues,first_episode,last_episode,count_of_episodes," +
            "count_of_issue_appearances,episode_number,air_date,series"

    const val SEARCH_STORY_ARCS = "id,name,deck,image,publisher"

    const val SEARCH_OBJECTS = "id,name,deck,image"
}

interface ComicVineService {

    @GET("characters?format=$FORMAT&field_list=${Fields.CHARACTERS}")
    @JvmSuppressWildcards
    suspend fun characters(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("sort", encoded = true) sort: ComicVineSort?,
        @Query("filter[]", encoded = true) filter: List<ComicVineFilter>?,
    ): ApiResponse<CharactersResponse>

    @GET("issues?format=$FORMAT&field_list=${Fields.ISSUES}")
    @JvmSuppressWildcards
    suspend fun issues(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("sort", encoded = true) sort: ComicVineSort?,
        @Query("filter[]", encoded = true) filter: List<ComicVineFilter>?,
    ): ApiResponse<IssuesResponse>

    @GET("volumes?format=$FORMAT&field_list=${Fields.VOLUMES}")
    @JvmSuppressWildcards
    suspend fun volumes(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("sort", encoded = true) sort: ComicVineSort?,
        @Query("filter[]", encoded = true) filter: List<ComicVineFilter>?,
    ): ApiResponse<VolumesResponse>

    @GET("character/4005-{id}/?format=$FORMAT")
    suspend fun character(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String,
    ): ApiResponse<CharacterResponse>

    @GET("issue/4000-{id}/?format=$FORMAT")
    suspend fun issue(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String,
    ): ApiResponse<IssueResponse>

    @GET("volume/4050-{id}/?format=$FORMAT")
    suspend fun volume(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String,
    ): ApiResponse<VolumeResponse>

    @GET("people?format=$FORMAT&field_list=${Fields.PEOPLE}")
    @JvmSuppressWildcards
    suspend fun people(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("sort", encoded = true) sort: ComicVineSort?,
        @Query("filter[]", encoded = true) filter: List<ComicVineFilter>?,
    ): ApiResponse<PeopleResponse>

    @GET("locations?format=$FORMAT&field_list=${Fields.LOCATIONS}")
    @JvmSuppressWildcards
    suspend fun locations(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("sort", encoded = true) sort: ComicVineSort?,
        @Query("filter[]", encoded = true) filter: List<ComicVineFilter>?,
    ): ApiResponse<LocationsResponse>

    @GET("concepts?format=$FORMAT&field_list=${Fields.CONCEPTS}")
    @JvmSuppressWildcards
    suspend fun concepts(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("sort", encoded = true) sort: ComicVineSort?,
        @Query("filter[]", encoded = true) filter: List<ComicVineFilter>?,
    ): ApiResponse<ConceptsResponse>

    @GET("objects?format=$FORMAT&field_list=${Fields.OBJECTS}")
    @JvmSuppressWildcards
    suspend fun objects(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("sort", encoded = true) sort: ComicVineSort?,
        @Query("filter[]", encoded = true) filter: List<ComicVineFilter>?,
    ): ApiResponse<ObjectsResponse>

    @GET("movies?format=$FORMAT&field_list=${Fields.MOVIES}")
    @JvmSuppressWildcards
    suspend fun movies(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("sort", encoded = true) sort: ComicVineSort?,
        @Query("filter[]", encoded = true) filter: List<ComicVineFilter>?,
    ): ApiResponse<MoviesResponse>

    @GET("story_arcs?format=$FORMAT&field_list=${Fields.STORY_ARCS}")
    @JvmSuppressWildcards
    suspend fun storyArcs(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("sort", encoded = true) sort: ComicVineSort?,
        @Query("filter[]", encoded = true) filter: List<ComicVineFilter>?,
    ): ApiResponse<StoryArcsResponse>

    @GET("teams?format=$FORMAT&field_list=${Fields.TEAMS}")
    @JvmSuppressWildcards
    suspend fun teams(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("sort", encoded = true) sort: ComicVineSort?,
        @Query("filter[]", encoded = true) filter: List<ComicVineFilter>?,
    ): ApiResponse<TeamsResponse>

    @GET("search?format=$FORMAT&field_list=${Fields.SEARCH}")
    @JvmSuppressWildcards
    suspend fun search(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("query") query: String,
        @Query("resources", encoded = true) resources: ComicVineSearchResourceTypeList?,
    ): ApiResponse<SearchResponse>

    @GET("story_arcs?format=$FORMAT&field_list=${Fields.SEARCH_STORY_ARCS}")
    @JvmSuppressWildcards
    suspend fun searchStoryArcs(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("filter[]", encoded = true) filter: List<ComicVineFilter>?,
    ): ApiResponse<SearchStoryArcsResponse>

    @GET("objects?format=$FORMAT&field_list=${Fields.SEARCH_OBJECTS}")
    @JvmSuppressWildcards
    suspend fun searchObjects(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("filter[]", encoded = true) filter: List<ComicVineFilter>?,
    ): ApiResponse<SearchObjectsResponse>
}
