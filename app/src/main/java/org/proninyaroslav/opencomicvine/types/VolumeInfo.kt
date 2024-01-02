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

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Ignore
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.proninyaroslav.opencomicvine.model.issuesCount
import java.util.*

@JsonClass(generateAdapter = true)
@Immutable
data class VolumeInfo(
    @Json(name = "id")
    val id: Int,

    @Json(name = "name")
    val name: String,

    @Embedded(prefix = "first_issue_")
    @Json(name = "first_issue")
    val firstIssue: Issue?,

    @Embedded(prefix = "last_issue_")
    @Json(name = "last_issue")
    val lastIssue: Issue?,

    @ColumnInfo(name = "countOfIssues")
    @Json(name = "count_of_issues")
    val _countOfIssues: Int,

    @Json(name = "date_added")
    val dateAdded: Date,

    @Json(name = "date_last_updated")
    val dateLastUpdated: Date,

    @Json(name = "start_year")
    val startYear: String?,

    @Embedded(prefix = "image_")
    @Json(name = "image")
    val image: ImageInfo,

    @Embedded(prefix = "publisher_")
    @Json(name = "publisher")
    val publisher: Publisher?,
) {
    @Ignore
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
        @Ignore get

    @JsonClass(generateAdapter = true)
    data class Publisher(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String,
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
}
