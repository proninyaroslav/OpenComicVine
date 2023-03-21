package org.proninyaroslav.opencomicvine.data

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