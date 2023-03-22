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

package org.proninyaroslav.opencomicvine.data

import androidx.compose.runtime.Immutable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.proninyaroslav.opencomicvine.model.issuesCount
import java.util.*

@JsonClass(generateAdapter = true)
@Immutable
data class VolumeDetails(
    @Json(name = "id")
    val id: Int,

    @Json(name = "name")
    val name: String,

    @Json(name = "first_issue")
    val firstIssue: Issue?,

    @Json(name = "last_issue")
    val lastIssue: Issue?,

    @Json(name = "count_of_issues")
    val _countOfIssues: Int,

    @Json(name = "date_added")
    val dateAdded: Date,

    @Json(name = "date_last_updated")
    val dateLastUpdated: Date,

    @Json(name = "start_year")
    val startYear: String?,

    @Json(name = "image")
    val image: ImageInfo,

    @Json(name = "publisher")
    val publisher: Publisher?,

    @Json(name = "deck")
    val descriptionShort: String?,

    @Json(name = "description")
    val description: String?,

    @Json(name = "characters")
    val characters: List<Character>?,

    @Json(name = "concepts")
    val concepts: List<Concept>?,

    @Json(name = "issues")
    val issues: List<Issue>?,

    @Json(name = "locations")
    val locations: List<Location>?,

    @Json(name = "objects")
    val objects: List<Object>?,

    @Json(name = "people")
    val people: List<Person>?,
) {
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

    @JsonClass(generateAdapter = true)
    data class Publisher(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String,
    )

    @JsonClass(generateAdapter = true)
    data class Character(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String,

        @Json(name = "count")
        val countOfAppearances: Int,
    )

    @JsonClass(generateAdapter = true)
    data class Concept(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String,

        @Json(name = "count")
        val countOfAppearances: Int,
    )

    @JsonClass(generateAdapter = true)
    data class Issue(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String?,

        @Json(name = "issue_number")
        val issueNumber: String?,
    )

    @JsonClass(generateAdapter = true)
    data class Location(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String,

        @Json(name = "count")
        val countOfAppearances: Int,
    )

    @JsonClass(generateAdapter = true)
    data class Object(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String,

        @Json(name = "count")
        val countOfAppearances: Int,
    )

    @JsonClass(generateAdapter = true)
    data class Person(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String,

        @Json(name = "count")
        val countOfAppearances: Int,
    )
}
