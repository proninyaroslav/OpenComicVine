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
import org.proninyaroslav.opencomicvine.model.issuesCount
import java.util.*

sealed interface SearchInfo {
    val id: Int

    data class Character(
        override val id: Int,
        val name: String,
        val descriptionShort: String?,
        val image: ImageInfo,
        val countOfIssueAppearances: Int,
        val publisher: Publisher?,
    ) : SearchInfo {
        data class Publisher(
            val id: Int,
            val name: String,
        )
    }

    data class Concept(
        override val id: Int,
        val name: String,
        val descriptionShort: String?,
        val image: ImageInfo,
    ) : SearchInfo

    @JsonClass(generateAdapter = true)
    data class Object(
        @Json(name = "id")
        override val id: Int,

        @Json(name = "name")
        val name: String,

        @Json(name = "deck")
        val descriptionShort: String?,

        @Json(name = "image")
        val image: ImageInfo,
    ) : SearchInfo

    data class Location(
        override val id: Int,
        val name: String,
        val descriptionShort: String?,
        val image: ImageInfo,
    ) : SearchInfo

    data class Issue(
        override val id: Int,
        val name: String?,
        val issueNumber: String,
        val descriptionShort: String?,
        val image: ImageInfo,
        val coverDate: Date?,
        val volume: Volume,
    ) : SearchInfo {
        data class Volume(
            val id: Int,
            val name: String,
        )
    }

    @JsonClass(generateAdapter = true)
    data class StoryArc(
        @Json(name = "id")
        override val id: Int,

        @Json(name = "name")
        val name: String,

        @Json(name = "deck")
        val descriptionShort: String?,

        @Json(name = "image")
        val image: ImageInfo,

        @Json(name = "publisher")
        val publisher: Publisher?,
    ) : SearchInfo {
        @JsonClass(generateAdapter = true)
        data class Publisher(
            @Json(name = "id")
            val id: Int,

            @Json(name = "name")
            val name: String,
        )
    }

    data class Volume(
        override val id: Int,
        val name: String,
        val descriptionShort: String?,
        val image: ImageInfo,
        val startYear: String?,
        val firstIssue: Issue?,
        val lastIssue: Issue?,
        val _countOfIssues: Int,
        val publisher: Publisher?,
    ) : SearchInfo {
        val countOfIssues: Int =
            if (_countOfIssues == 0) {
                val count = issuesCount(
                    firstIssueNumber = firstIssue?.issueNumber,
                    lastIssueNumber = lastIssue?.issueNumber,
                )
                count ?: _countOfIssues
            } else {
                _countOfIssues
            }

        data class Issue(
            val id: Int,
            val name: String?,
            val issueNumber: String?,
        )

        data class Publisher(
            val id: Int,
            val name: String,
        )
    }

    data class Person(
        override val id: Int,
        val name: String,
        val descriptionShort: String?,
        val image: ImageInfo,
    ) : SearchInfo

    data class Team(
        override val id: Int,
        val name: String,
        val descriptionShort: String?,
        val image: ImageInfo,
    ) : SearchInfo

    data class Video(
        override val id: Int,
        val name: String,
        val descriptionShort: String?,
        val image: ImageInfo,
    ) : SearchInfo

    data class Series(
        override val id: Int,
        val name: String,
        val descriptionShort: String?,
        val image: ImageInfo,
        val startYear: String?,
        val firstEpisode: Episode?,
        val lastEpisode: Episode?,
        val _countOfEpisodes: Int,
        val publisher: Publisher?,
    ) : SearchInfo {
        val countOfEpisodes: Int =
            if (_countOfEpisodes == 0) {
                val count = issuesCount(
                    firstIssueNumber = firstEpisode?.episodeNumber,
                    lastIssueNumber = lastEpisode?.episodeNumber,
                )
                count ?: _countOfEpisodes
            } else {
                _countOfEpisodes
            }

        data class Episode(
            val id: Int,
            val name: String?,
            val episodeNumber: String?,
        )

        data class Publisher(
            val id: Int,
            val name: String,
        )
    }

    data class Episode(
        override val id: Int,
        val name: String?,
        val episodeNumber: String,
        val descriptionShort: String?,
        val image: ImageInfo,
        val airDate: Date?,
        val series: Series,
    ) : SearchInfo {
        data class Series(
            val id: Int,
            val name: String,
        )
    }
}

@JsonClass(generateAdapter = true)
data class ComicVineSearchInfoJson(
    val resource_type: ComicVineSearchResourceType,
    val id: Int,
    val name: String?,
    val deck: String?,
    val image: ImageInfo,
    val count_of_issue_appearances: Int = 0,
    val publisher: Publisher? = null,
    val issue_number: String = "",
    val cover_date: Date? = null,
    val volume: Volume = Volume(id = 0, name = ""),
    val start_year: String? = null,
    val first_issue: Issue? = null,
    val last_issue: Issue? = null,
    val count_of_issues: Int = 0,
    val first_episode: Episode? = null,
    val last_episode: Episode? = null,
    val count_of_episodes: Int = 0,
    val episode_number: String = "",
    val air_date: Date? = null,
    val series: Series = Series(id = 0, name = ""),
) {
    @JsonClass(generateAdapter = true)
    data class Publisher(
        val id: Int,
        val name: String,
    )

    @JsonClass(generateAdapter = true)
    data class Volume(
        val id: Int,
        val name: String,
    )

    @JsonClass(generateAdapter = true)
    data class Issue(
        val id: Int,
        val name: String?,
        val issue_number: String?,
    )

    @JsonClass(generateAdapter = true)
    data class Episode(
        val id: Int,
        val name: String?,
        val episode_number: String?,
    )

    @JsonClass(generateAdapter = true)
    data class Series(
        val id: Int,
        val name: String,
    )
}

fun SearchInfo.toFavoritesType(): FavoriteInfo.EntityType? = when (this) {
    is SearchInfo.Character -> FavoriteInfo.EntityType.Character
    is SearchInfo.Concept -> FavoriteInfo.EntityType.Concept
    is SearchInfo.Issue -> FavoriteInfo.EntityType.Issue
    is SearchInfo.Location -> FavoriteInfo.EntityType.Location
    is SearchInfo.Object -> FavoriteInfo.EntityType.Object
    is SearchInfo.Person -> FavoriteInfo.EntityType.Person
    is SearchInfo.StoryArc -> FavoriteInfo.EntityType.StoryArc
    is SearchInfo.Team -> FavoriteInfo.EntityType.Team
    is SearchInfo.Volume -> FavoriteInfo.EntityType.Volume
    is SearchInfo.Episode -> null // TODO: add favorites support
    is SearchInfo.Series -> null // TODO: add favorites support
    is SearchInfo.Video -> null // TODO: add favorites support
}
