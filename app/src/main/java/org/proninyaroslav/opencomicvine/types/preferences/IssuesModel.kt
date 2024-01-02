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

package org.proninyaroslav.opencomicvine.types.preferences

import com.squareup.moshi.JsonClass
import dev.zacsweers.moshix.sealed.annotations.NestedSealed
import dev.zacsweers.moshix.sealed.annotations.TypeLabel
import org.proninyaroslav.opencomicvine.types.filter.IssuesFilter
import org.proninyaroslav.opencomicvine.types.sort.ComicVineSortDirection
import org.proninyaroslav.opencomicvine.types.sort.IssuesSort
import org.proninyaroslav.opencomicvine.model.getDaysOfCurrentWeek
import org.proninyaroslav.opencomicvine.model.getNextWeekFromCurrentDay
import java.util.*

@JsonClass(generateAdapter = true, generator = "sealed:type")
sealed interface PrefWikiIssuesSort {
    val direction: PrefSortDirection

    @TypeLabel("unknown")
    object Unknown : PrefWikiIssuesSort {
        override val direction: PrefSortDirection
            get() = PrefSortDirection.Unknown
    }

    @TypeLabel("alphabetical")
    @JsonClass(generateAdapter = true)
    data class Alphabetical(
        override val direction: PrefSortDirection
    ) : PrefWikiIssuesSort

    @TypeLabel("date_last_updated")
    @JsonClass(generateAdapter = true)
    data class DateLastUpdated(
        override val direction: PrefSortDirection
    ) : PrefWikiIssuesSort

    @TypeLabel("date_added")
    @JsonClass(generateAdapter = true)
    data class DateAdded(
        override val direction: PrefSortDirection
    ) : PrefWikiIssuesSort

    @TypeLabel("cover_date")
    @JsonClass(generateAdapter = true)
    data class CoverDate(
        override val direction: PrefSortDirection
    ) : PrefWikiIssuesSort

    @TypeLabel("store_date")
    @JsonClass(generateAdapter = true)
    data class StoreDate(
        override val direction: PrefSortDirection
    ) : PrefWikiIssuesSort
}

@JsonClass(generateAdapter = true, generator = "sealed:type")
sealed interface PrefWikiIssuesFilter {
    @NestedSealed
    sealed interface Name : PrefWikiIssuesFilter {
        @TypeLabel("name_unknown")
        object Unknown : Name

        @TypeLabel("name_contains")
        @JsonClass(generateAdapter = true)
        data class Contains(val nameValue: String) : Name
    }

    @NestedSealed
    sealed interface DateAdded : PrefWikiIssuesFilter {
        @TypeLabel("date_added_unknown")
        object Unknown : DateAdded

        @TypeLabel("date_added_in_range")
        @JsonClass(generateAdapter = true)
        data class InRange(val start: Date, val end: Date) : DateAdded
    }

    @NestedSealed
    sealed interface DateLastUpdated : PrefWikiIssuesFilter {
        @TypeLabel("date_last_updated_unknown")
        object Unknown : DateLastUpdated

        @TypeLabel("date_last_updated_in_range")
        @JsonClass(generateAdapter = true)
        data class InRange(val start: Date, val end: Date) : DateLastUpdated
    }

    @NestedSealed
    sealed interface CoverDate : PrefWikiIssuesFilter {
        @TypeLabel("cover_date_unknown")
        object Unknown : CoverDate

        @TypeLabel("cover_date_in_range")
        @JsonClass(generateAdapter = true)
        data class InRange(val start: Date, val end: Date) : CoverDate
    }

    @NestedSealed
    sealed interface StoreDate : PrefWikiIssuesFilter {
        @TypeLabel("store_date_unknown")
        object Unknown : StoreDate

        @TypeLabel("store_date_in_range")
        @JsonClass(generateAdapter = true)
        data class InRange(val start: Date, val end: Date) : StoreDate
    }

    @NestedSealed
    sealed interface IssueNumber : PrefWikiIssuesFilter {
        @TypeLabel("issue_number_unknown")
        object Unknown : IssueNumber

        @TypeLabel("issue_number_contains")
        @JsonClass(generateAdapter = true)
        data class Contains(val issueNumberValue: String) : IssueNumber
    }
}

@JsonClass(generateAdapter = true, generator = "sealed:type")
sealed interface PrefRecentIssuesFilter {
    @NestedSealed
    sealed interface StoreDate : PrefRecentIssuesFilter {
        @TypeLabel("store_date_unknown")
        object Unknown : StoreDate

        @TypeLabel("store_date_next_week")
        object NextWeek : StoreDate

        @TypeLabel("store_date_in_range")
        @JsonClass(generateAdapter = true)
        data class InRange(val start: Date, val end: Date) : StoreDate
    }

    @NestedSealed
    sealed interface DateAdded : PrefRecentIssuesFilter {
        @TypeLabel("date_added_unknown")
        object Unknown : DateAdded

        @TypeLabel("date_added_this_week")
        object ThisWeek : DateAdded

        @TypeLabel("date_added_in_range")
        @JsonClass(generateAdapter = true)
        data class InRange(val start: Date, val end: Date) : DateAdded
    }
}

@JsonClass(generateAdapter = true)
data class PrefWikiIssuesFilterBundle(
    val name: PrefWikiIssuesFilter.Name,
    val dateAdded: PrefWikiIssuesFilter.DateAdded,
    val dateLastUpdated: PrefWikiIssuesFilter.DateLastUpdated,
    val coverDate: PrefWikiIssuesFilter.CoverDate,
    val storeDate: PrefWikiIssuesFilter.StoreDate,
    val issueNumber: PrefWikiIssuesFilter.IssueNumber,
)

@JsonClass(generateAdapter = true)
data class PrefRecentIssuesFilterBundle(
    val dateAdded: PrefRecentIssuesFilter.DateAdded,
    val storeDate: PrefRecentIssuesFilter.StoreDate,
)

@JsonClass(generateAdapter = true, generator = "sealed:type")
sealed interface PrefRecentIssuesSort {
    val direction: PrefSortDirection

    @TypeLabel("unknown")
    object Unknown : PrefRecentIssuesSort {
        override val direction: PrefSortDirection
            get() = PrefSortDirection.Unknown
    }

    @TypeLabel("date_added")
    @JsonClass(generateAdapter = true)
    data class DateAdded(
        override val direction: PrefSortDirection
    ) : PrefRecentIssuesSort

    @TypeLabel("store_date")
    @JsonClass(generateAdapter = true)
    data class StoreDate(
        override val direction: PrefSortDirection
    ) : PrefRecentIssuesSort
}

fun PrefWikiIssuesSort.toComicVineSort(): IssuesSort? = when (this) {
    PrefWikiIssuesSort.Unknown -> null
    is PrefWikiIssuesSort.Alphabetical -> when (direction) {
        PrefSortDirection.Unknown -> null
        PrefSortDirection.Asc -> IssuesSort.Name(ComicVineSortDirection.Asc)
        PrefSortDirection.Desc -> IssuesSort.Name(ComicVineSortDirection.Desc)
    }
    is PrefWikiIssuesSort.DateLastUpdated -> when (direction) {
        PrefSortDirection.Unknown -> null
        PrefSortDirection.Asc -> IssuesSort.DateLastUpdated(ComicVineSortDirection.Asc)
        PrefSortDirection.Desc -> IssuesSort.DateLastUpdated(ComicVineSortDirection.Desc)
    }
    is PrefWikiIssuesSort.DateAdded -> when (direction) {
        PrefSortDirection.Unknown -> null
        PrefSortDirection.Asc -> IssuesSort.DateAdded(ComicVineSortDirection.Asc)
        PrefSortDirection.Desc -> IssuesSort.DateAdded(ComicVineSortDirection.Desc)
    }
    is PrefWikiIssuesSort.CoverDate -> when (direction) {
        PrefSortDirection.Unknown -> null
        PrefSortDirection.Asc -> IssuesSort.CoverDate(ComicVineSortDirection.Asc)
        PrefSortDirection.Desc -> IssuesSort.CoverDate(ComicVineSortDirection.Desc)
    }
    is PrefWikiIssuesSort.StoreDate -> when (direction) {
        PrefSortDirection.Unknown -> null
        PrefSortDirection.Asc -> IssuesSort.StoreDate(ComicVineSortDirection.Asc)
        PrefSortDirection.Desc -> IssuesSort.StoreDate(ComicVineSortDirection.Desc)
    }
}

fun PrefWikiIssuesFilter.toComicVineFilter(): IssuesFilter? = when (this) {
    is PrefWikiIssuesFilter.CoverDate.InRange -> {
        IssuesFilter.CoverDate(start = start, end = end)
    }
    PrefWikiIssuesFilter.CoverDate.Unknown -> null
    is PrefWikiIssuesFilter.DateAdded.InRange -> {
        IssuesFilter.DateAdded(start = start, end = end)
    }
    PrefWikiIssuesFilter.DateAdded.Unknown -> null
    is PrefWikiIssuesFilter.DateLastUpdated.InRange -> {
        IssuesFilter.DateLastUpdated(start = start, end = end)
    }
    PrefWikiIssuesFilter.DateLastUpdated.Unknown -> null
    is PrefWikiIssuesFilter.IssueNumber.Contains -> {
        IssuesFilter.IssueNumber(issueNumberValue = issueNumberValue)
    }
    PrefWikiIssuesFilter.IssueNumber.Unknown -> null
    is PrefWikiIssuesFilter.Name.Contains -> IssuesFilter.Name(nameValue = nameValue)
    PrefWikiIssuesFilter.Name.Unknown -> null
    is PrefWikiIssuesFilter.StoreDate.InRange -> {
        IssuesFilter.StoreDate(start = start, end = end)
    }
    PrefWikiIssuesFilter.StoreDate.Unknown -> null
}

fun PrefRecentIssuesFilter.toComicVineFilter(): IssuesFilter? = when (this) {
    is PrefRecentIssuesFilter.DateAdded.InRange -> {
        IssuesFilter.DateAdded(start = start, end = end)
    }
    PrefRecentIssuesFilter.DateAdded.ThisWeek -> getDaysOfCurrentWeek().run {
        IssuesFilter.DateAdded(start = first, end = second)
    }
    PrefRecentIssuesFilter.DateAdded.Unknown -> null
    is PrefRecentIssuesFilter.StoreDate.InRange -> {
        IssuesFilter.StoreDate(start = start, end = end)
    }
    PrefRecentIssuesFilter.StoreDate.NextWeek -> getNextWeekFromCurrentDay().run {
        IssuesFilter.StoreDate(start = first, end = second)
    }
    PrefRecentIssuesFilter.StoreDate.Unknown -> null
}

fun PrefWikiIssuesFilterBundle.toComicVineFiltersList(): List<IssuesFilter> =
    listOf(
        name,
        dateAdded,
        dateLastUpdated,
        coverDate,
        storeDate,
        issueNumber,
    ).fold(mutableListOf()) { acc, filter ->
        filter.toComicVineFilter()?.let { acc.apply { add(it) } } ?: acc
    }

fun PrefRecentIssuesFilterBundle.toComicVineFiltersList(): List<IssuesFilter> =
    listOf(
        dateAdded,
        storeDate,
    ).fold(mutableListOf()) { acc, filter ->
        filter.toComicVineFilter()?.let { acc.apply { add(it) } } ?: acc
    }

fun PrefRecentIssuesSort.toComicVineSort(): IssuesSort? = when (this) {
    PrefRecentIssuesSort.Unknown -> null
    is PrefRecentIssuesSort.DateAdded -> when (direction) {
        PrefSortDirection.Unknown -> null
        PrefSortDirection.Asc -> IssuesSort.DateAdded(ComicVineSortDirection.Asc)
        PrefSortDirection.Desc -> IssuesSort.DateAdded(ComicVineSortDirection.Desc)
    }
    is PrefRecentIssuesSort.StoreDate -> when (direction) {
        PrefSortDirection.Unknown -> null
        PrefSortDirection.Asc -> IssuesSort.StoreDate(ComicVineSortDirection.Asc)
        PrefSortDirection.Desc -> IssuesSort.StoreDate(ComicVineSortDirection.Desc)
    }
}
