package org.proninyaroslav.opencomicvine.types.preferences

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.proninyaroslav.opencomicvine.types.filter.IssuesFilter
import org.proninyaroslav.opencomicvine.types.sort.ComicVineSortDirection
import org.proninyaroslav.opencomicvine.types.sort.IssuesSort
import org.proninyaroslav.opencomicvine.model.getDaysOfCurrentWeek
import java.util.*

class IssuesModelTest {
    @Test
    fun toComicVineFiltersList() {
        val wikiBundle = PrefWikiIssuesFilterBundle(
            name = PrefWikiIssuesFilter.Name.Contains("test"),
            dateAdded = PrefWikiIssuesFilter.DateAdded.InRange(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
            dateLastUpdated = PrefWikiIssuesFilter.DateLastUpdated.InRange(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
            coverDate = PrefWikiIssuesFilter.CoverDate.InRange(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
            storeDate = PrefWikiIssuesFilter.StoreDate.InRange(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
            issueNumber = PrefWikiIssuesFilter.IssueNumber.Contains("1"),
        )
        val wikiExpected = listOf(
            IssuesFilter.Name("test"),
            IssuesFilter.DateAdded(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
            IssuesFilter.DateLastUpdated(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
            IssuesFilter.CoverDate(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
            IssuesFilter.StoreDate(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
            IssuesFilter.IssueNumber("1"),
        )

        val recentBundle = PrefRecentIssuesFilterBundle(
            dateAdded = PrefRecentIssuesFilter.DateAdded.InRange(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
            storeDate = PrefRecentIssuesFilter.StoreDate.InRange(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
        )
        val recentExpected = listOf(
            IssuesFilter.DateAdded(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
            IssuesFilter.StoreDate(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
        )

        assertEquals(
            "PrefWikiIssuesFilterBundle",
            wikiExpected,
            wikiBundle.toComicVineFiltersList(),
        )
        assertEquals(
            "PrefRecentIssuesFilterBundle",
            recentExpected,
            recentBundle.toComicVineFiltersList(),
        )
    }

    @Test
    fun toComicVineSort() {
        listOf(
            PrefWikiIssuesSort.Unknown,
            PrefWikiIssuesSort.Alphabetical(
                direction = PrefSortDirection.Asc
            ),
            PrefWikiIssuesSort.Alphabetical(
                direction = PrefSortDirection.Desc
            ),
            PrefWikiIssuesSort.DateLastUpdated(
                direction = PrefSortDirection.Asc
            ),
            PrefWikiIssuesSort.DateLastUpdated(
                direction = PrefSortDirection.Desc
            ),
            PrefWikiIssuesSort.DateAdded(
                direction = PrefSortDirection.Asc
            ),
            PrefWikiIssuesSort.DateAdded(
                direction = PrefSortDirection.Desc
            ),
            PrefWikiIssuesSort.CoverDate(
                direction = PrefSortDirection.Asc
            ),
            PrefWikiIssuesSort.CoverDate(
                direction = PrefSortDirection.Desc
            ),
            PrefWikiIssuesSort.StoreDate(
                direction = PrefSortDirection.Asc
            ),
            PrefWikiIssuesSort.StoreDate(
                direction = PrefSortDirection.Desc
            ),
        ).onEach {
            when (it) {
                is PrefWikiIssuesSort.Alphabetical -> when (it.direction) {
                    PrefSortDirection.Unknown -> assertNull(
                        "$it",
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Asc -> assertEquals(
                        "$it",
                        IssuesSort.Name(direction = ComicVineSortDirection.Asc),
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Desc -> assertEquals(
                        "$it",
                        IssuesSort.Name(direction = ComicVineSortDirection.Desc),
                        it.toComicVineSort(),
                    )
                }
                is PrefWikiIssuesSort.DateAdded -> when (it.direction) {
                    PrefSortDirection.Unknown -> assertNull(
                        "$it",
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Asc -> assertEquals(
                        "$it",
                        IssuesSort.DateAdded(direction = ComicVineSortDirection.Asc),
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Desc -> assertEquals(
                        "$it",
                        IssuesSort.DateAdded(direction = ComicVineSortDirection.Desc),
                        it.toComicVineSort(),
                    )
                }
                is PrefWikiIssuesSort.DateLastUpdated -> when (it.direction) {
                    PrefSortDirection.Unknown -> assertNull(
                        "$it",
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Asc -> assertEquals(
                        "$it",
                        IssuesSort.DateLastUpdated(direction = ComicVineSortDirection.Asc),
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Desc -> assertEquals(
                        "$it",
                        IssuesSort.DateLastUpdated(direction = ComicVineSortDirection.Desc),
                        it.toComicVineSort(),
                    )
                }
                PrefWikiIssuesSort.Unknown -> assertNull(
                    "$it",
                    it.toComicVineSort(),
                )
                is PrefWikiIssuesSort.CoverDate -> when (it.direction) {
                    PrefSortDirection.Unknown -> assertNull(
                        "$it",
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Asc -> assertEquals(
                        "$it",
                        IssuesSort.CoverDate(direction = ComicVineSortDirection.Asc),
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Desc -> assertEquals(
                        "$it",
                        IssuesSort.CoverDate(direction = ComicVineSortDirection.Desc),
                        it.toComicVineSort(),
                    )
                }
                is PrefWikiIssuesSort.StoreDate -> when (it.direction) {
                    PrefSortDirection.Unknown -> assertNull(
                        "$it",
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Asc -> assertEquals(
                        "$it",
                        IssuesSort.StoreDate(direction = ComicVineSortDirection.Asc),
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Desc -> assertEquals(
                        "$it",
                        IssuesSort.StoreDate(direction = ComicVineSortDirection.Desc),
                        it.toComicVineSort(),
                    )
                }
            }
        }

        listOf(
            PrefRecentIssuesSort.Unknown,
            PrefRecentIssuesSort.DateAdded(
                direction = PrefSortDirection.Asc
            ),
            PrefRecentIssuesSort.DateAdded(
                direction = PrefSortDirection.Desc
            ),
            PrefRecentIssuesSort.StoreDate(
                direction = PrefSortDirection.Asc
            ),
            PrefRecentIssuesSort.StoreDate(
                direction = PrefSortDirection.Desc
            ),
        ).onEach {
            when (it) {
                is PrefRecentIssuesSort.DateAdded -> when (it.direction) {
                    PrefSortDirection.Unknown -> assertNull(
                        "$it",
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Asc -> assertEquals(
                        "$it",
                        IssuesSort.DateAdded(direction = ComicVineSortDirection.Asc),
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Desc -> assertEquals(
                        "$it",
                        IssuesSort.DateAdded(direction = ComicVineSortDirection.Desc),
                        it.toComicVineSort(),
                    )
                }
                is PrefRecentIssuesSort.StoreDate -> when (it.direction) {
                    PrefSortDirection.Unknown -> assertNull(
                        "$it",
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Asc -> assertEquals(
                        "$it",
                        IssuesSort.StoreDate(direction = ComicVineSortDirection.Asc),
                        it.toComicVineSort(),
                    )
                    PrefSortDirection.Desc -> assertEquals(
                        "$it",
                        IssuesSort.StoreDate(direction = ComicVineSortDirection.Desc),
                        it.toComicVineSort(),
                    )
                }
                PrefRecentIssuesSort.Unknown -> assertNull(
                    "$it",
                    it.toComicVineSort(),
                )
            }
        }
    }

    @Test
    fun toComicVineFilter() {
        listOf(
            PrefWikiIssuesFilter.Name.Unknown,
            PrefWikiIssuesFilter.Name.Contains("test"),
            PrefWikiIssuesFilter.DateAdded.Unknown,
            PrefWikiIssuesFilter.DateAdded.InRange(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
            PrefWikiIssuesFilter.DateLastUpdated.Unknown,
            PrefWikiIssuesFilter.DateLastUpdated.InRange(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
            PrefWikiIssuesFilter.StoreDate.Unknown,
            PrefWikiIssuesFilter.StoreDate.InRange(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
            PrefWikiIssuesFilter.CoverDate.Unknown,
            PrefWikiIssuesFilter.CoverDate.InRange(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
            PrefWikiIssuesFilter.IssueNumber.Unknown,
            PrefWikiIssuesFilter.IssueNumber.Contains("1"),
        ).onEach {
            when (it) {
                is PrefWikiIssuesFilter.DateAdded.InRange -> assertEquals(
                    "$it",
                    IssuesFilter.DateAdded(
                        start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                        end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
                    ),
                    it.toComicVineFilter(),
                )
                PrefWikiIssuesFilter.DateAdded.Unknown -> assertNull(
                    "$it",
                    it.toComicVineFilter(),
                )
                is PrefWikiIssuesFilter.DateLastUpdated.InRange -> assertEquals(
                    "$it",
                    IssuesFilter.DateLastUpdated(
                        start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                        end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
                    ),
                    it.toComicVineFilter(),
                )
                PrefWikiIssuesFilter.DateLastUpdated.Unknown -> assertNull(
                    "$it",
                    it.toComicVineFilter(),
                )
                is PrefWikiIssuesFilter.Name.Contains -> assertEquals(
                    "$it",
                    IssuesFilter.Name("test"),
                    it.toComicVineFilter(),
                )
                PrefWikiIssuesFilter.Name.Unknown -> assertNull(
                    "$it",
                    it.toComicVineFilter(),
                )
                is PrefWikiIssuesFilter.CoverDate.InRange -> assertEquals(
                    "$it",
                    IssuesFilter.CoverDate(
                        start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                        end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
                    ),
                    it.toComicVineFilter(),
                )
                PrefWikiIssuesFilter.CoverDate.Unknown -> assertNull(
                    "$it",
                    it.toComicVineFilter(),
                )
                is PrefWikiIssuesFilter.IssueNumber.Contains -> assertEquals(
                    "$it",
                    IssuesFilter.IssueNumber("1"),
                    it.toComicVineFilter(),
                )
                PrefWikiIssuesFilter.IssueNumber.Unknown -> assertNull(
                    "$it",
                    it.toComicVineFilter(),
                )
                is PrefWikiIssuesFilter.StoreDate.InRange -> assertEquals(
                    "$it",
                    IssuesFilter.StoreDate(
                        start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                        end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
                    ),
                    it.toComicVineFilter(),
                )
                PrefWikiIssuesFilter.StoreDate.Unknown -> assertNull(
                    "$it",
                    it.toComicVineFilter(),
                )
            }
        }

        listOf(
            PrefRecentIssuesFilter.DateAdded.Unknown,
            PrefRecentIssuesFilter.DateAdded.ThisWeek,
            PrefRecentIssuesFilter.DateAdded.InRange(
                start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
            ),
        ).onEach {
            when (it) {
                is PrefRecentIssuesFilter.DateAdded.InRange -> assertEquals(
                    "$it",
                    IssuesFilter.DateAdded(
                        start = Date(GregorianCalendar(2022, 0, 1).timeInMillis),
                        end = Date(GregorianCalendar(2022, 0, 2).timeInMillis),
                    ),
                    it.toComicVineFilter(),
                )
                PrefRecentIssuesFilter.DateAdded.ThisWeek -> assertEquals(
                    "$it",
                    getDaysOfCurrentWeek().run {
                        IssuesFilter.DateAdded(
                            start = first,
                            end = second,
                        )
                    },
                    it.toComicVineFilter(),
                )
                PrefRecentIssuesFilter.DateAdded.Unknown -> assertNull(
                    "$it",
                    it.toComicVineFilter(),
                )
            }
        }
    }
}