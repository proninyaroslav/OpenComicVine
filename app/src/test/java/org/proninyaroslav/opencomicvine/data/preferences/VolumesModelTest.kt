package org.proninyaroslav.opencomicvine.data.preferences

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.proninyaroslav.opencomicvine.data.filter.VolumesFilter
import org.proninyaroslav.opencomicvine.data.sort.ComicVineSortDirection
import org.proninyaroslav.opencomicvine.data.sort.VolumesSort
import java.util.*

class VolumesModelTest {
    @Test
    fun toComicVineFiltersList() {
        val wikiBundle = PrefWikiVolumesFilterBundle(
            name = PrefWikiVolumesFilter.Name.Contains("test"),
            dateAdded = PrefWikiVolumesFilter.DateAdded.InRange(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
            dateLastUpdated = PrefWikiVolumesFilter.DateLastUpdated.InRange(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
        )
        val wikiExpected = listOf(
            VolumesFilter.Name("test"),
            VolumesFilter.DateAdded(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
            VolumesFilter.DateLastUpdated(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
        )

        val recentBundle = PrefRecentVolumesFilterBundle(
            dateAdded = PrefRecentVolumesFilter.DateAdded.InRange(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
        )
        val recentExpected = listOf(
            VolumesFilter.DateAdded(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
        )

        assertEquals(
            "PrefWikiVolumesFilterBundle",
            wikiExpected,
            wikiBundle.toComicVineFiltersList(),
        )
        assertEquals(
            "PrefRecentVolumesFilterBundle",
            recentExpected,
            recentBundle.toComicVineFiltersList(),
        )
    }

    @Test
    fun toComicVineSort() {
        listOf(
            PrefWikiVolumesSort.Unknown,
            PrefWikiVolumesSort.Alphabetical(
                direction = PrefSortDirection.Asc
            ),
            PrefWikiVolumesSort.Alphabetical(
                direction = PrefSortDirection.Desc
            ),
            PrefWikiVolumesSort.DateLastUpdated(
                direction = PrefSortDirection.Asc
            ),
            PrefWikiVolumesSort.DateLastUpdated(
                direction = PrefSortDirection.Desc
            ),
            PrefWikiVolumesSort.DateAdded(
                direction = PrefSortDirection.Asc
            ),
            PrefWikiVolumesSort.DateAdded(
                direction = PrefSortDirection.Desc
            ),
        ).onEach {
            when (it) {
                is PrefWikiVolumesSort.Alphabetical -> when (it.direction) {
                    PrefSortDirection.Unknown -> assertNull(
                        "$it",
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Asc -> assertEquals(
                        "$it",
                        VolumesSort.Name(direction = ComicVineSortDirection.Asc),
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Desc -> assertEquals(
                        "$it",
                        VolumesSort.Name(direction = ComicVineSortDirection.Desc),
                        it.toComicVineSort(),
                    )
                }
                is PrefWikiVolumesSort.DateAdded -> when (it.direction) {
                    PrefSortDirection.Unknown -> assertNull(
                        "$it",
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Asc -> assertEquals(
                        "$it",
                        VolumesSort.DateAdded(direction = ComicVineSortDirection.Asc),
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Desc -> assertEquals(
                        "$it",
                        VolumesSort.DateAdded(direction = ComicVineSortDirection.Desc),
                        it.toComicVineSort(),
                    )
                }
                is PrefWikiVolumesSort.DateLastUpdated -> when (it.direction) {
                    PrefSortDirection.Unknown -> assertNull(
                        "$it",
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Asc -> assertEquals(
                        "$it",
                        VolumesSort.DateLastUpdated(direction = ComicVineSortDirection.Asc),
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Desc -> assertEquals(
                        "$it",
                        VolumesSort.DateLastUpdated(direction = ComicVineSortDirection.Desc),
                        it.toComicVineSort(),
                    )
                }
                PrefWikiVolumesSort.Unknown -> assertNull(
                    "$it",
                    it.toComicVineSort(),
                )
            }
        }
    }

    @Test
    fun toComicVineFilter() {
        listOf(
            PrefWikiVolumesFilter.Name.Unknown,
            PrefWikiVolumesFilter.Name.Contains("test"),
            PrefWikiVolumesFilter.DateAdded.Unknown,
            PrefWikiVolumesFilter.DateAdded.InRange(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
            PrefWikiVolumesFilter.DateLastUpdated.Unknown,
            PrefWikiVolumesFilter.DateLastUpdated.InRange(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
        ).onEach {
            when (it) {
                is PrefWikiVolumesFilter.DateAdded.InRange -> assertEquals(
                    "$it",
                    VolumesFilter.DateAdded(
                        start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                        end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
                    ),
                    it.toComicVineFilter(),
                )
                PrefWikiVolumesFilter.DateAdded.Unknown -> assertNull(
                    "$it",
                    it.toComicVineFilter(),
                )
                is PrefWikiVolumesFilter.DateLastUpdated.InRange -> assertEquals(
                    "$it",
                    VolumesFilter.DateLastUpdated(
                        start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                        end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
                    ),
                    it.toComicVineFilter(),
                )
                PrefWikiVolumesFilter.DateLastUpdated.Unknown -> assertNull(
                    "$it",
                    it.toComicVineFilter(),
                )
                is PrefWikiVolumesFilter.Name.Contains -> assertEquals(
                    "$it",
                    VolumesFilter.Name("test"),
                    it.toComicVineFilter(),
                )
                PrefWikiVolumesFilter.Name.Unknown -> assertNull(
                    "$it",
                    it.toComicVineFilter(),
                )
            }
        }

        listOf(
            PrefRecentVolumesFilter.DateAdded.Unknown,
            PrefRecentVolumesFilter.DateAdded.InRange(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
        ).onEach {
            when (it) {
                is PrefRecentVolumesFilter.DateAdded.InRange -> assertEquals(
                    "$it",
                    VolumesFilter.DateAdded(
                        start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                        end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
                    ),
                    it.toComicVineFilter(),
                )
                PrefRecentVolumesFilter.DateAdded.Unknown -> assertNull(
                    "$it",
                    it.toComicVineFilter(),
                )
            }
        }
    }
}