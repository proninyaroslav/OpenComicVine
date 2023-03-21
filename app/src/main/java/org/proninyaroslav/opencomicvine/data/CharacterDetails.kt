package org.proninyaroslav.opencomicvine.data

import androidx.compose.runtime.Immutable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
@Immutable
data class CharacterDetails(
    @Json(name = "id")
    val id: Int,

    @Json(name = "name")
    val name: String,

    @Json(name = "gender")
    val gender: Gender,

    @Json(name = "image")
    val image: ImageInfo,

    @Json(name = "date_added")
    val dateAdded: Date,

    @Json(name = "date_last_updated")
    val dateLastUpdated: Date,

    @Json(name = "aliases")
    val aliases: Aliases?,

    @Json(name = "birth")
    val birth: Date?,

    @Json(name = "character_enemies")
    val enemies: List<Enemy>,

    @Json(name = "character_friends")
    val friends: List<Friend>,

    @Json(name = "count_of_issue_appearances")
    val countOfIssueAppearances: Int,

    @Json(name = "creators")
    val creators: List<Creator>,

    @Json(name = "deck")
    val descriptionShort: String?,

    @Json(name = "description")
    val description: String?,

    @Json(name = "first_appeared_in_issue")
    val firstAppearedInIssue: FirstIssueAppearance?,

    @Json(name = "issue_credits")
    val issueCredits: List<Issue>,

    @Json(name = "issues_died_in")
    val issuesDiedIn: List<Issue>,

    @Json(name = "movies")
    val movies: List<Movie>,

    @Json(name = "origin")
    val origin: Origin?,

    @Json(name = "powers")
    val powers: List<Power>,

    @Json(name = "publisher")
    val publisher: Publisher?,

    @Json(name = "real_name")
    val realName: String?,

    @Json(name = "story_arc_credits")
    val storyArcCredits: List<StoryArc>,

    @Json(name = "team_enemies")
    val teamEnemies: List<Team>,

    @Json(name = "team_friends")
    val teamFriends: List<Team>,

    @Json(name = "teams")
    val teams: List<Team>,

    @Json(name = "volume_credits")
    val volumeCredits: List<Volume>,
) {
    @JsonClass(generateAdapter = true)
    data class Enemy(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String,
    )

    @JsonClass(generateAdapter = true)
    data class Friend(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String,
    )

    @JsonClass(generateAdapter = true)
    data class Creator(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String,
    )

    @JsonClass(generateAdapter = true)
    data class FirstIssueAppearance(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String?,

        @Json(name = "issue_number")
        val issueNumber: String?,
    )

    @JsonClass(generateAdapter = true)
    data class Issue(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String?,
    )

    @JsonClass(generateAdapter = true)
    data class Movie(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String,
    )

    @JsonClass(generateAdapter = true)
    data class Power(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String,
    )

    @JsonClass(generateAdapter = true)
    data class Publisher(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String,
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

    @JsonClass(generateAdapter = true)
    data class Volume(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String,
    )
}