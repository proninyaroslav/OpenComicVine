package org.proninyaroslav.opencomicvine.data.filter

import org.proninyaroslav.opencomicvine.model.moshi.ComicVineDateRangeConverter
import org.proninyaroslav.opencomicvine.model.moshi.ComicVineValuesListConverter
import java.util.*

sealed class IssuesFilter(key: String, value: String) :
    ComicVineFilter(field = key, value = value) {

    data class Name(val nameValue: String) : IssuesFilter(key = "name", value = nameValue)

    /**
     * @param   start  the start date of the range.
     * @param   end    the end date of the range, inclusive.
     */
    data class DateAdded(val start: Date, val end: Date) : IssuesFilter(
        key = "date_added",
        value = ComicVineDateRangeConverter.toJson(start to end)!!
    )

    /**
     * @param   start  the start date of the range.
     * @param   end    the end date of the range, inclusive.
     */
    data class DateLastUpdated(val start: Date, val end: Date) : IssuesFilter(
        key = "date_last_updated",
        value = ComicVineDateRangeConverter.toJson(start to end)!!
    )

    /**
     * @param   start  the start date of the range.
     * @param   end    the end date of the range, inclusive.
     */
    data class CoverDate(val start: Date, val end: Date) : IssuesFilter(
        key = "cover_date",
        value = ComicVineDateRangeConverter.toJson(start to end)!!
    )

    /**
     * @param   start  the start date of the range.
     * @param   end    the end date of the range, inclusive.
     */
    data class StoreDate(val start: Date, val end: Date) : IssuesFilter(
        key = "store_date",
        value = ComicVineDateRangeConverter.toJson(start to end)!!
    )

    data class IssueNumber(val issueNumberValue: String) :
        IssuesFilter(key = "issue_number", value = issueNumberValue)

    data class Id(val idList: List<Int>) :
        IssuesFilter(
            key = "id",
            value = ComicVineValuesListConverter { it.toInt() }.toJson(idList)!!
        )
}
