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
import org.proninyaroslav.opencomicvine.types.filter.CharactersFilter
import org.proninyaroslav.opencomicvine.types.sort.CharactersSort
import org.proninyaroslav.opencomicvine.types.sort.ComicVineSortDirection
import java.util.Date

@JsonClass(generateAdapter = true, generator = "sealed:type")
sealed interface PrefWikiCharactersSort {
    val direction: PrefSortDirection

    @TypeLabel("unknown")
    object Unknown : PrefWikiCharactersSort {
        override val direction: PrefSortDirection
            get() = PrefSortDirection.Unknown
    }

    @TypeLabel("alphabetical")
    @JsonClass(generateAdapter = true)
    data class Alphabetical(
        override val direction: PrefSortDirection
    ) : PrefWikiCharactersSort

    @TypeLabel("date_last_updated")
    @JsonClass(generateAdapter = true)
    data class DateLastUpdated(
        override val direction: PrefSortDirection
    ) : PrefWikiCharactersSort

    @TypeLabel("date_added")
    @JsonClass(generateAdapter = true)
    data class DateAdded(
        override val direction: PrefSortDirection
    ) : PrefWikiCharactersSort
}

@JsonClass(generateAdapter = true, generator = "sealed:type")
sealed interface PrefWikiCharactersFilter {
    @TypeLabel("gender")
    @JsonClass(generateAdapter = false)
    enum class Gender : PrefWikiCharactersFilter {
        Unknown,
        Other,
        Male,
        Female,
    }

    @NestedSealed
    sealed interface Name : PrefWikiCharactersFilter {
        @TypeLabel("name_unknown")
        object Unknown : Name

        @TypeLabel("name_contains")
        @JsonClass(generateAdapter = true)
        data class Contains(val nameValue: String) : Name
    }

    @NestedSealed
    sealed interface DateAdded : PrefWikiCharactersFilter {
        @TypeLabel("date_added_unknown")
        object Unknown : DateAdded

        @TypeLabel("date_added_in_range")
        @JsonClass(generateAdapter = true)
        data class InRange(val start: Date, val end: Date) : DateAdded
    }

    @NestedSealed
    sealed interface DateLastUpdated : PrefWikiCharactersFilter {
        @TypeLabel("date_last_updated_unknown")
        object Unknown : DateLastUpdated

        @TypeLabel("date_last_updated_in_range")
        @JsonClass(generateAdapter = true)
        data class InRange(val start: Date, val end: Date) : DateLastUpdated
    }
}

@JsonClass(generateAdapter = true, generator = "sealed:type")
sealed interface PrefRecentCharactersFilter {
    @NestedSealed
    sealed interface DateAdded : PrefRecentCharactersFilter {
        @TypeLabel("date_added_unknown")
        object Unknown : DateAdded

        @TypeLabel("date_added_in_range")
        @JsonClass(generateAdapter = true)
        data class InRange(val start: Date, val end: Date) : DateAdded
    }
}

@JsonClass(generateAdapter = true)
data class PrefWikiCharactersFilterBundle(
    val gender: PrefWikiCharactersFilter.Gender,
    val name: PrefWikiCharactersFilter.Name,
    val dateAdded: PrefWikiCharactersFilter.DateAdded,
    val dateLastUpdated: PrefWikiCharactersFilter.DateLastUpdated,
)

@JsonClass(generateAdapter = true)
data class PrefRecentCharactersFilterBundle(
    val dateAdded: PrefRecentCharactersFilter.DateAdded,
)

fun PrefWikiCharactersSort.toComicVineSort(): CharactersSort? = when (this) {
    PrefWikiCharactersSort.Unknown -> null
    is PrefWikiCharactersSort.Alphabetical -> when (direction) {
        PrefSortDirection.Unknown -> null
        PrefSortDirection.Asc -> CharactersSort.Name(ComicVineSortDirection.Asc)
        PrefSortDirection.Desc -> CharactersSort.Name(ComicVineSortDirection.Desc)
    }

    is PrefWikiCharactersSort.DateLastUpdated -> when (direction) {
        PrefSortDirection.Unknown -> null
        PrefSortDirection.Asc -> CharactersSort.DateLastUpdated(ComicVineSortDirection.Asc)
        PrefSortDirection.Desc -> CharactersSort.DateLastUpdated(ComicVineSortDirection.Desc)
    }

    is PrefWikiCharactersSort.DateAdded -> when (direction) {
        PrefSortDirection.Unknown -> null
        PrefSortDirection.Asc -> CharactersSort.DateAdded(ComicVineSortDirection.Asc)
        PrefSortDirection.Desc -> CharactersSort.DateAdded(ComicVineSortDirection.Desc)
    }
}

fun PrefWikiCharactersFilter.toComicVineFilter(): CharactersFilter? = when (this) {
    PrefWikiCharactersFilter.Gender.Unknown -> null
    PrefWikiCharactersFilter.Gender.Other -> CharactersFilter.Gender.Other
    PrefWikiCharactersFilter.Gender.Male -> CharactersFilter.Gender.Male
    PrefWikiCharactersFilter.Gender.Female -> CharactersFilter.Gender.Female
    PrefWikiCharactersFilter.Name.Unknown -> null
    is PrefWikiCharactersFilter.Name.Contains -> CharactersFilter.Name(nameValue)
    PrefWikiCharactersFilter.DateAdded.Unknown -> null
    is PrefWikiCharactersFilter.DateAdded.InRange -> {
        CharactersFilter.DateAdded(start = start, end = end)
    }

    is PrefWikiCharactersFilter.DateLastUpdated.InRange -> {
        CharactersFilter.DateLastUpdated(start = start, end = end)
    }

    PrefWikiCharactersFilter.DateLastUpdated.Unknown -> null
}

fun PrefRecentCharactersFilter.toComicVineFilter(): CharactersFilter? = when (this) {
    PrefRecentCharactersFilter.DateAdded.Unknown -> null
    is PrefRecentCharactersFilter.DateAdded.InRange -> {
        CharactersFilter.DateAdded(start = start, end = end)
    }
}

fun PrefWikiCharactersFilterBundle.toComicVineFiltersList(): List<CharactersFilter> =
    listOf(
        gender,
        name,
        dateAdded,
        dateLastUpdated,
    ).fold(mutableListOf()) { acc, filter ->
        filter.toComicVineFilter()?.let { acc.apply { add(it) } } ?: acc
    }

fun PrefRecentCharactersFilterBundle.toComicVineFiltersList(): List<CharactersFilter> =
    listOf(
        dateAdded,
    ).fold(mutableListOf()) { acc, filter ->
        filter.toComicVineFilter()?.let { acc.apply { add(it) } } ?: acc
    }
