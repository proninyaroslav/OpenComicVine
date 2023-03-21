package org.proninyaroslav.opencomicvine.data.preferences

import com.squareup.moshi.JsonClass
import dev.zacsweers.moshix.sealed.annotations.NestedSealed
import dev.zacsweers.moshix.sealed.annotations.TypeLabel
import org.proninyaroslav.opencomicvine.data.filter.CharactersFilter
import org.proninyaroslav.opencomicvine.data.sort.CharactersSort
import org.proninyaroslav.opencomicvine.data.sort.ComicVineSortDirection
import java.util.*

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