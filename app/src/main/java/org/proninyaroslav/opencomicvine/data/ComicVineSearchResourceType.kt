package org.proninyaroslav.opencomicvine.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
enum class ComicVineSearchResourceType(val value: String) {
    @Json(name = "character")
    Character("character"),

    @Json(name = "concept")
    Concept("concept"),

    @Json(name = "object")
    Object("object"),

    @Json(name = "location")
    Location("location"),

    @Json(name = "issue")
    Issue("issue"),

    @Json(name = "story_arc")
    StoryArc("story_arc"),

    @Json(name = "volume")
    Volume("volume"),

    @Json(name = "person")
    Person("person"),

    @Json(name = "team")
    Team("team"),

    @Json(name = "video")
    Video("video"),

    @Json(name = "series")
    Series("series"),

    @Json(name = "episode")
    Episode("episode");

    companion object {
        fun from(value: String): ComicVineSearchResourceType = when (value) {
            "character" -> Character
            "concept" -> Concept
            "object" -> Object
            "location" -> Location
            "issue" -> Issue
            "story_arc" -> StoryArc
            "volume" -> Volume
            "person" -> Person
            "team" -> Team
            "video" -> Video
            "series" -> Series
            "episode" -> Episode
            else -> throw IllegalArgumentException("Unknown type: $value")
        }
    }
}

data class ComicVineSearchResourceTypeList(val list: List<ComicVineSearchResourceType>)