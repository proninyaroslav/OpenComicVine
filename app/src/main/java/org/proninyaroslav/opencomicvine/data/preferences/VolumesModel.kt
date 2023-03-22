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

package org.proninyaroslav.opencomicvine.data.preferences

import com.squareup.moshi.JsonClass
import dev.zacsweers.moshix.sealed.annotations.NestedSealed
import dev.zacsweers.moshix.sealed.annotations.TypeLabel
import org.proninyaroslav.opencomicvine.data.filter.VolumesFilter
import org.proninyaroslav.opencomicvine.data.sort.ComicVineSortDirection
import org.proninyaroslav.opencomicvine.data.sort.VolumesSort
import java.util.*

@JsonClass(generateAdapter = true, generator = "sealed:type")
sealed interface PrefWikiVolumesSort {
    val direction: PrefSortDirection

    @TypeLabel("unknown")
    object Unknown : PrefWikiVolumesSort {
        override val direction: PrefSortDirection
            get() = PrefSortDirection.Unknown
    }

    @TypeLabel("alphabetical")
    @JsonClass(generateAdapter = true)
    data class Alphabetical(
        override val direction: PrefSortDirection
    ) : PrefWikiVolumesSort

    @TypeLabel("date_last_updated")
    @JsonClass(generateAdapter = true)
    data class DateLastUpdated(
        override val direction: PrefSortDirection
    ) : PrefWikiVolumesSort

    @TypeLabel("date_added")
    @JsonClass(generateAdapter = true)
    data class DateAdded(
        override val direction: PrefSortDirection
    ) : PrefWikiVolumesSort
}

@JsonClass(generateAdapter = true, generator = "sealed:type")
sealed interface PrefWikiVolumesFilter {
    @NestedSealed
    sealed interface Name : PrefWikiVolumesFilter {
        @TypeLabel("name_unknown")
        object Unknown : Name

        @TypeLabel("name_contains")
        @JsonClass(generateAdapter = true)
        data class Contains(val nameValue: String) : Name
    }

    @NestedSealed
    sealed interface DateAdded : PrefWikiVolumesFilter {
        @TypeLabel("date_added_unknown")
        object Unknown : DateAdded

        @TypeLabel("date_added_in_range")
        @JsonClass(generateAdapter = true)
        data class InRange(val start: Date, val end: Date) : DateAdded
    }

    @NestedSealed
    sealed interface DateLastUpdated : PrefWikiVolumesFilter {
        @TypeLabel("date_last_updated_unknown")
        object Unknown : DateLastUpdated

        @TypeLabel("date_last_updated_in_range")
        @JsonClass(generateAdapter = true)
        data class InRange(val start: Date, val end: Date) : DateLastUpdated
    }
}

@JsonClass(generateAdapter = true, generator = "sealed:type")
sealed interface PrefRecentVolumesFilter {
    @NestedSealed
    sealed interface DateAdded : PrefRecentVolumesFilter {
        @TypeLabel("date_added_unknown")
        object Unknown : DateAdded

        @TypeLabel("date_added_in_range")
        @JsonClass(generateAdapter = true)
        data class InRange(val start: Date, val end: Date) : DateAdded
    }
}

@JsonClass(generateAdapter = true)
data class PrefWikiVolumesFilterBundle(
    val name: PrefWikiVolumesFilter.Name,
    val dateAdded: PrefWikiVolumesFilter.DateAdded,
    val dateLastUpdated: PrefWikiVolumesFilter.DateLastUpdated,
)

@JsonClass(generateAdapter = true)
data class PrefRecentVolumesFilterBundle(
    val dateAdded: PrefRecentVolumesFilter.DateAdded,
)

fun PrefWikiVolumesSort.toComicVineSort(): VolumesSort? = when (this) {
    is PrefWikiVolumesSort.Alphabetical -> when (direction) {
        PrefSortDirection.Unknown -> null
        PrefSortDirection.Asc -> VolumesSort.Name(
            direction = ComicVineSortDirection.Asc
        )
        PrefSortDirection.Desc -> VolumesSort.Name(
            direction = ComicVineSortDirection.Desc
        )
    }
    is PrefWikiVolumesSort.DateAdded -> when (direction) {
        PrefSortDirection.Unknown -> null
        PrefSortDirection.Asc -> VolumesSort.DateAdded(
            direction = ComicVineSortDirection.Asc
        )
        PrefSortDirection.Desc -> VolumesSort.DateAdded(
            direction = ComicVineSortDirection.Desc
        )
    }
    is PrefWikiVolumesSort.DateLastUpdated -> when (direction) {
        PrefSortDirection.Unknown -> null
        PrefSortDirection.Asc -> VolumesSort.DateLastUpdated(
            direction = ComicVineSortDirection.Asc
        )
        PrefSortDirection.Desc -> VolumesSort.DateLastUpdated(
            direction = ComicVineSortDirection.Desc
        )
    }
    PrefWikiVolumesSort.Unknown -> null
}

fun PrefWikiVolumesFilter.toComicVineFilter(): VolumesFilter? = when (this) {
    is PrefWikiVolumesFilter.DateAdded.InRange -> {
        VolumesFilter.DateAdded(start = start, end = end)
    }
    PrefWikiVolumesFilter.DateAdded.Unknown -> null
    is PrefWikiVolumesFilter.DateLastUpdated.InRange -> {
        VolumesFilter.DateLastUpdated(start = start, end = end)
    }
    PrefWikiVolumesFilter.DateLastUpdated.Unknown -> null
    is PrefWikiVolumesFilter.Name.Contains -> VolumesFilter.Name(nameValue = nameValue)
    PrefWikiVolumesFilter.Name.Unknown -> null
}

fun PrefRecentVolumesFilter.toComicVineFilter(): VolumesFilter? = when (this) {
    is PrefRecentVolumesFilter.DateAdded.InRange -> {
        VolumesFilter.DateAdded(start = start, end = end)
    }
    PrefRecentVolumesFilter.DateAdded.Unknown -> null
}

fun PrefWikiVolumesFilterBundle.toComicVineFiltersList(): List<VolumesFilter> =
    listOf(
        name,
        dateAdded,
        dateLastUpdated,
    ).fold(mutableListOf()) { acc, filter ->
        filter.toComicVineFilter()?.let { acc.apply { add(it) } } ?: acc
    }

fun PrefRecentVolumesFilterBundle.toComicVineFiltersList(): List<VolumesFilter> =
    listOf(
        dateAdded,
    ).fold(mutableListOf()) { acc, filter ->
        filter.toComicVineFilter()?.let { acc.apply { add(it) } } ?: acc
    }
