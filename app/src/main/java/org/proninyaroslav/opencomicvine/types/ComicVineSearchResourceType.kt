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
