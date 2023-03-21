package org.proninyaroslav.opencomicvine.data.preferences

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.proninyaroslav.opencomicvine.data.filter.CharactersFilter
import org.proninyaroslav.opencomicvine.data.sort.CharactersSort
import org.proninyaroslav.opencomicvine.data.sort.ComicVineSortDirection
import java.util.*

class CharactersModelTest {
    @Test
    fun toComicVineFiltersList() {
        val wikiBundle = PrefWikiCharactersFilterBundle(
            gender = PrefWikiCharactersFilter.Gender.Male,
            name = PrefWikiCharactersFilter.Name.Contains("test"),
            dateAdded = PrefWikiCharactersFilter.DateAdded.InRange(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
            dateLastUpdated = PrefWikiCharactersFilter.DateLastUpdated.InRange(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
        )
        val wikiExpected = listOf(
            CharactersFilter.Gender.Male,
            CharactersFilter.Name("test"),
            CharactersFilter.DateAdded(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
            CharactersFilter.DateLastUpdated(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
        )

        val recentBundle = PrefRecentCharactersFilterBundle(
            dateAdded = PrefRecentCharactersFilter.DateAdded.InRange(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
        )
        val recentExpected = listOf(
            CharactersFilter.DateAdded(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
        )

        assertEquals(
            "PrefWikiCharactersFilterBundle",
            wikiExpected,
            wikiBundle.toComicVineFiltersList(),
        )
        assertEquals(
            "PrefRecentCharactersFilterBundle",
            recentExpected,
            recentBundle.toComicVineFiltersList(),
        )
    }

    @Test
    fun toComicVineSort() {
        listOf(
            PrefWikiCharactersSort.Unknown,
            PrefWikiCharactersSort.Alphabetical(
                direction = PrefSortDirection.Asc
            ),
            PrefWikiCharactersSort.Alphabetical(
                direction = PrefSortDirection.Desc
            ),
            PrefWikiCharactersSort.DateLastUpdated(
                direction = PrefSortDirection.Asc
            ),
            PrefWikiCharactersSort.DateLastUpdated(
                direction = PrefSortDirection.Desc
            ),
            PrefWikiCharactersSort.DateAdded(
                direction = PrefSortDirection.Asc
            ),
            PrefWikiCharactersSort.DateAdded(
                direction = PrefSortDirection.Desc
            ),
        ).onEach {
            when (it) {
                is PrefWikiCharactersSort.Alphabetical -> when (it.direction) {
                    PrefSortDirection.Unknown -> assertNull(
                        "$it",
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Asc -> assertEquals(
                        "$it",
                        CharactersSort.Name(direction = ComicVineSortDirection.Asc),
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Desc -> assertEquals(
                        "$it",
                        CharactersSort.Name(direction = ComicVineSortDirection.Desc),
                        it.toComicVineSort(),
                    )
                }
                is PrefWikiCharactersSort.DateAdded -> when (it.direction) {
                    PrefSortDirection.Unknown -> assertNull(
                        "$it",
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Asc -> assertEquals(
                        "$it",
                        CharactersSort.DateAdded(direction = ComicVineSortDirection.Asc),
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Desc -> assertEquals(
                        "$it",
                        CharactersSort.DateAdded(direction = ComicVineSortDirection.Desc),
                        it.toComicVineSort(),
                    )
                }
                is PrefWikiCharactersSort.DateLastUpdated -> when (it.direction) {
                    PrefSortDirection.Unknown -> assertNull(
                        "$it",
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Asc -> assertEquals(
                        "$it",
                        CharactersSort.DateLastUpdated(direction = ComicVineSortDirection.Asc),
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Desc -> assertEquals(
                        "$it",
                        CharactersSort.DateLastUpdated(direction = ComicVineSortDirection.Desc),
                        it.toComicVineSort(),
                    )
                }
                PrefWikiCharactersSort.Unknown -> assertNull(
                    "$it",
                    it.toComicVineSort(),
                )
            }
        }
    }

    @Test
    fun toComicVineFilter() {
        listOf(
            PrefWikiCharactersFilter.Gender.Unknown,
            PrefWikiCharactersFilter.Gender.Male,
            PrefWikiCharactersFilter.Gender.Female,
            PrefWikiCharactersFilter.Name.Unknown,
            PrefWikiCharactersFilter.Name.Contains("test"),
            PrefWikiCharactersFilter.DateAdded.Unknown,
            PrefWikiCharactersFilter.DateAdded.InRange(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
            PrefWikiCharactersFilter.DateLastUpdated.Unknown,
            PrefWikiCharactersFilter.DateLastUpdated.InRange(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
        ).onEach {
            when (it) {
                is PrefWikiCharactersFilter.DateAdded.InRange -> assertEquals(
                    "$it",
                    CharactersFilter.DateAdded(
                        start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                        end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
                    ),
                    it.toComicVineFilter(),
                )
                PrefWikiCharactersFilter.DateAdded.Unknown -> assertNull(
                    "$it",
                    it.toComicVineFilter(),
                )
                is PrefWikiCharactersFilter.DateLastUpdated.InRange -> assertEquals(
                    "$it",
                    CharactersFilter.DateLastUpdated(
                        start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                        end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
                    ),
                    it.toComicVineFilter(),
                )
                PrefWikiCharactersFilter.DateLastUpdated.Unknown -> assertNull(
                    "$it",
                    it.toComicVineFilter(),
                )
                PrefWikiCharactersFilter.Gender.Unknown -> assertNull(
                    "$it",
                    it.toComicVineFilter(),
                )
                PrefWikiCharactersFilter.Gender.Other -> assertEquals(
                    "$it",
                    CharactersFilter.Gender.Other,
                    it.toComicVineFilter(),
                )
                PrefWikiCharactersFilter.Gender.Male -> assertEquals(
                    "$it",
                    CharactersFilter.Gender.Male,
                    it.toComicVineFilter(),
                )
                PrefWikiCharactersFilter.Gender.Female -> assertEquals(
                    "$it",
                    CharactersFilter.Gender.Female,
                    it.toComicVineFilter(),
                )
                is PrefWikiCharactersFilter.Name.Contains -> assertEquals(
                    "$it",
                    CharactersFilter.Name("test"),
                    it.toComicVineFilter(),
                )
                PrefWikiCharactersFilter.Name.Unknown -> assertNull(
                    "$it",
                    it.toComicVineFilter(),
                )
            }
        }

        listOf(
            PrefRecentCharactersFilter.DateAdded.Unknown,
            PrefRecentCharactersFilter.DateAdded.InRange(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
        ).onEach {
            when (it) {
                is PrefRecentCharactersFilter.DateAdded.InRange -> assertEquals(
                    "$it",
                    CharactersFilter.DateAdded(
                        start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                        end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
                    ),
                    it.toComicVineFilter(),
                )
                PrefRecentCharactersFilter.DateAdded.Unknown -> assertNull(
                    "$it",
                    it.toComicVineFilter(),
                )
            }
        }
    }
}