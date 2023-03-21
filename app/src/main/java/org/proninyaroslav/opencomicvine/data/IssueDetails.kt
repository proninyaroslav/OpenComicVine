package org.proninyaroslav.opencomicvine.data

import androidx.compose.runtime.Immutable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
@Immutable
data class IssueDetails(
    @Json(name = "id")
    val id: Int,

    @Json(name = "name")
    val name: String?,

    @Json(name = "issue_number")
    val issueNumber: String,

    @Json(name = "volume")
    val volume: Volume,

    @Json(name = "image")
    val image: ImageInfo,

    @Json(name = "cover_date")
    val coverDate: Date?,

    @Json(name = "store_date")
    val storeDate: Date?,

    @Json(name = "date_added")
    val dateAdded: Date,

    @Json(name = "date_last_updated")
    val dateLastUpdated: Date,

    @Json(name = "associated_images")
    val associatedImages: List<AssociatedImage>,

    @Json(name = "character_credits")
    val characterCredits: List<Character>,

    @Json(name = "character_died_in")
    val characterDiedIn: List<Character>,

    @Json(name = "concept_credits")
    val conceptCredits: List<Concept>,

    @Json(name = "deck")
    val descriptionShort: String?,

    @Json(name = "description")
    val description: String?,

    @Json(name = "location_credits")
    val locationCredits: List<Location>,

    @Json(name = "object_credits")
    val objectCredits: List<Object>,

    @Json(name = "person_credits")
    val personCredits: List<Person>,

    @Json(name = "story_arc_credits")
    val storyArcCredits: List<StoryArc>,

    @Json(name = "team_credits")
    val teamCredits: List<Team>,

    @Json(name = "team_disbanded_in")
    val teamDisbandedIn: List<Team>,
) {
    @JsonClass(generateAdapter = true)
    data class Volume(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String,
    )

    @JsonClass(generateAdapter = true)
    data class AssociatedImage(
        @Json(name = "id")
        val id: Int,

        @Json(name = "original_url")
        val originalUrl: String,

        @Json(name = "caption")
        val caption: String?,

        @Json(name = "image_tags")
        val imageTags: String?,
    )

    @JsonClass(generateAdapter = true)
    data class Character(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String,
    )

    @JsonClass(generateAdapter = true)
    data class Concept(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String,
    )

    @JsonClass(generateAdapter = true)
    data class Location(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String,
    )

    @JsonClass(generateAdapter = true)
    data class Object(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String,
    )

    @JsonClass(generateAdapter = true)
    data class Person(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String,

        @Json(name = "role")
        val role: String,
    )

    @JsonClass(generateAdapter = true)
    data class StoryArc(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String,
    )

    @JsonClass(generateAdapter = true)
    data class Team(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String,
    )
}
