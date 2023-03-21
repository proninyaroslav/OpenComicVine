package org.proninyaroslav.opencomicvine.model.network

import com.skydoves.sandwich.ApiResponse
import org.proninyaroslav.opencomicvine.data.*
import org.proninyaroslav.opencomicvine.data.filter.ComicVineFilter
import org.proninyaroslav.opencomicvine.data.sort.ComicVineSort
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val FORMAT = "json"

private object Fields {
    const val Characters =
        "id,name,gender,image,date_added,date_last_updated"

    const val Issues =
        "id,name,volume,issue_number,image,date_added,date_last_updated,cover_date,store_date"

    const val Volumes =
        "id,name,publisher,count_of_issues,date_added,date_last_updated,start_year,image,first_issue,last_issue"

    const val People = "id,name,image"

    const val Locations = "id,name,image"

    const val Concepts = "id,name,image"

    const val Objects = "id,name,image"

    const val Movies = "id,name,image"

    const val StoryArcs = "id,name,image"

    const val Teams = "id,name,image"

    const val Search = "id,name,image,deck,issue_number,cover_date,volume,publisher,start_year," +
            "first_issue,last_issue,count_of_issues,first_episode,last_episode,count_of_episodes," +
            "count_of_issue_appearances,episode_number,air_date,series"

    const val SearchStoryArcs = "id,name,deck,image,publisher"

    const val SearchObjects = "id,name,deck,image"
}

interface ComicVineService {

    @GET("characters?format=$FORMAT&field_list=${Fields.Characters}")
    @JvmSuppressWildcards
    suspend fun characters(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("sort", encoded = true) sort: ComicVineSort?,
        @Query("filter[]", encoded = true) filter: List<ComicVineFilter>?,
    ): ApiResponse<CharactersResponse>

    @GET("issues?format=$FORMAT&field_list=${Fields.Issues}")
    @JvmSuppressWildcards
    suspend fun issues(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("sort", encoded = true) sort: ComicVineSort?,
        @Query("filter[]", encoded = true) filter: List<ComicVineFilter>?,
    ): ApiResponse<IssuesResponse>

    @GET("volumes?format=$FORMAT&field_list=${Fields.Volumes}")
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

    @GET("people?format=$FORMAT&field_list=${Fields.People}")
    @JvmSuppressWildcards
    suspend fun people(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("sort", encoded = true) sort: ComicVineSort?,
        @Query("filter[]", encoded = true) filter: List<ComicVineFilter>?,
    ): ApiResponse<PeopleResponse>

    @GET("locations?format=$FORMAT&field_list=${Fields.Locations}")
    @JvmSuppressWildcards
    suspend fun locations(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("sort", encoded = true) sort: ComicVineSort?,
        @Query("filter[]", encoded = true) filter: List<ComicVineFilter>?,
    ): ApiResponse<LocationsResponse>

    @GET("concepts?format=$FORMAT&field_list=${Fields.Concepts}")
    @JvmSuppressWildcards
    suspend fun concepts(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("sort", encoded = true) sort: ComicVineSort?,
        @Query("filter[]", encoded = true) filter: List<ComicVineFilter>?,
    ): ApiResponse<ConceptsResponse>

    @GET("objects?format=$FORMAT&field_list=${Fields.Objects}")
    @JvmSuppressWildcards
    suspend fun objects(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("sort", encoded = true) sort: ComicVineSort?,
        @Query("filter[]", encoded = true) filter: List<ComicVineFilter>?,
    ): ApiResponse<ObjectsResponse>

    @GET("movies?format=$FORMAT&field_list=${Fields.Movies}")
    @JvmSuppressWildcards
    suspend fun movies(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("sort", encoded = true) sort: ComicVineSort?,
        @Query("filter[]", encoded = true) filter: List<ComicVineFilter>?,
    ): ApiResponse<MoviesResponse>

    @GET("story_arcs?format=$FORMAT&field_list=${Fields.StoryArcs}")
    @JvmSuppressWildcards
    suspend fun storyArcs(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("sort", encoded = true) sort: ComicVineSort?,
        @Query("filter[]", encoded = true) filter: List<ComicVineFilter>?,
    ): ApiResponse<StoryArcsResponse>

    @GET("teams?format=$FORMAT&field_list=${Fields.Teams}")
    @JvmSuppressWildcards
    suspend fun teams(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("sort", encoded = true) sort: ComicVineSort?,
        @Query("filter[]", encoded = true) filter: List<ComicVineFilter>?,
    ): ApiResponse<TeamsResponse>

    @GET("search?format=$FORMAT&field_list=${Fields.Search}")
    @JvmSuppressWildcards
    suspend fun search(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("query") query: String,
        @Query("resources", encoded = true) resources: ComicVineSearchResourceTypeList?,
    ): ApiResponse<SearchResponse>

    @GET("story_arcs?format=$FORMAT&field_list=${Fields.SearchStoryArcs}")
    @JvmSuppressWildcards
    suspend fun searchStoryArcs(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("filter[]", encoded = true) filter: List<ComicVineFilter>?,
    ): ApiResponse<SearchStoryArcsResponse>

    @GET("objects?format=$FORMAT&field_list=${Fields.SearchObjects}")
    @JvmSuppressWildcards
    suspend fun searchObjects(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("filter[]", encoded = true) filter: List<ComicVineFilter>?,
    ): ApiResponse<SearchObjectsResponse>
}