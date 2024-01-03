package org.proninyaroslav.opencomicvine.model

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.proninyaroslav.opencomicvine.types.preferences.*
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class AppPreferencesTest {
    private lateinit var pref: AppPreferences

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val dataStore = PreferenceDataStoreFactory.create(
        scope = coroutineScope,
        produceFile = { context.preferencesDataStoreFile("test_preferences") }
    )

    @Before
    fun setUp() {
        pref = AppPreferencesImpl(dataStore)
    }

    @After
    fun tearDown() = runTest {
        dataStore.edit { it.clear() }
        coroutineScope.cancel()
    }

    @Test
    fun wikiCharactersSort_defaultValue() = runTest {
        assertEquals(
            PrefWikiCharactersSort.DateLastUpdated(
                direction = PrefSortDirection.Desc,
            ),
            pref.wikiCharactersSort.first(),
        )
    }

    @Test
    fun setWikiCharactersSort() = runTest {
        val sort = PrefWikiCharactersSort.Alphabetical(
            direction = PrefSortDirection.Asc,
        )
        pref.setWikiCharactersSort(sort)
        assertEquals(sort, pref.wikiCharactersSort.first())
    }

    @Test
    fun wikiCharactersFilters_defaultValue() = runTest {
        assertEquals(
            PrefWikiCharactersFilterBundle(
                gender = PrefWikiCharactersFilter.Gender.Unknown,
                name = PrefWikiCharactersFilter.Name.Unknown,
                dateAdded = PrefWikiCharactersFilter.DateAdded.Unknown,
                dateLastUpdated = PrefWikiCharactersFilter.DateLastUpdated.Unknown,
            ),
            pref.wikiCharactersFilters.first(),
        )
    }

    @Test
    fun setWikiCharactersFilters() = runTest {
        val filter = PrefWikiCharactersFilterBundle(
            gender = PrefWikiCharactersFilter.Gender.Male,
            name = PrefWikiCharactersFilter.Name.Contains("test"),
            dateAdded = PrefWikiCharactersFilter.DateAdded.InRange(
                start = Date(
                    GregorianCalendar(2022, 0, 1).timeInMillis
                ),
                end = Date(
                    GregorianCalendar(2022, 0, 2).timeInMillis
                ),
            ),
            dateLastUpdated = PrefWikiCharactersFilter.DateLastUpdated.InRange(
                start = Date(
                    GregorianCalendar(2022, 0, 3).timeInMillis
                ),
                end = Date(
                    GregorianCalendar(2022, 0, 4).timeInMillis
                ),
            ),
        )
        pref.setWikiCharactersFilters(filter)
        assertEquals(filter, pref.wikiCharactersFilters.first())
    }

    @Test
    fun recentCharactersFilters_defaultValue() = runTest {
        assertEquals(
            PrefRecentCharactersFilterBundle(
                dateAdded = PrefRecentCharactersFilter.DateAdded.Unknown,
            ),
            pref.recentCharactersFilters.first(),
        )
    }

    @Test
    fun setRecentCharactersFilters() = runTest {
        val filter = PrefRecentCharactersFilterBundle(
            dateAdded = PrefRecentCharactersFilter.DateAdded.InRange(
                start = Date(
                    GregorianCalendar(2022, 0, 1).timeInMillis
                ),
                end = Date(
                    GregorianCalendar(2022, 0, 2).timeInMillis
                ),
            ),
        )
        pref.setRecentCharactersFilters(filter)
        assertEquals(filter, pref.recentCharactersFilters.first())
    }

    @Test
    fun wikiIssuesSort_defaultValue() = runTest {
        assertEquals(
            PrefWikiIssuesSort.DateLastUpdated(
                direction = PrefSortDirection.Desc,
            ),
            pref.wikiIssuesSort.first(),
        )
    }

    @Test
    fun setWikiIssuesSort() = runTest {
        val sort = PrefWikiIssuesSort.Alphabetical(
            direction = PrefSortDirection.Asc,
        )
        pref.setWikiIssuesSort(sort)
        assertEquals(sort, pref.wikiIssuesSort.first())
    }

    @Test
    fun wikiIssuesFilters_defaultValue() = runTest {
        assertEquals(
            PrefWikiIssuesFilterBundle(
                name = PrefWikiIssuesFilter.Name.Unknown,
                dateAdded = PrefWikiIssuesFilter.DateAdded.Unknown,
                dateLastUpdated = PrefWikiIssuesFilter.DateLastUpdated.Unknown,
                coverDate = PrefWikiIssuesFilter.CoverDate.Unknown,
                storeDate = PrefWikiIssuesFilter.StoreDate.Unknown,
                issueNumber = PrefWikiIssuesFilter.IssueNumber.Unknown,
            ),
            pref.wikiIssuesFilters.first(),
        )
    }

    @Test
    fun setWikiIssuesFilters() = runTest {
        val filter = PrefWikiIssuesFilterBundle(
            name = PrefWikiIssuesFilter.Name.Contains("test"),
            dateAdded = PrefWikiIssuesFilter.DateAdded.InRange(
                start = Date(
                    GregorianCalendar(2022, 0, 1).timeInMillis
                ),
                end = Date(
                    GregorianCalendar(2022, 0, 2).timeInMillis
                ),
            ),
            dateLastUpdated = PrefWikiIssuesFilter.DateLastUpdated.InRange(
                start = Date(
                    GregorianCalendar(2022, 0, 3).timeInMillis
                ),
                end = Date(
                    GregorianCalendar(2022, 0, 4).timeInMillis
                ),
            ),
            coverDate = PrefWikiIssuesFilter.CoverDate.InRange(
                start = Date(
                    GregorianCalendar(2022, 0, 5).timeInMillis
                ),
                end = Date(
                    GregorianCalendar(2022, 0, 6).timeInMillis
                ),
            ),
            storeDate = PrefWikiIssuesFilter.StoreDate.InRange(
                start = Date(
                    GregorianCalendar(2022, 0, 7).timeInMillis
                ),
                end = Date(
                    GregorianCalendar(2022, 0, 8).timeInMillis
                ),
            ),
            issueNumber = PrefWikiIssuesFilter.IssueNumber.Contains("1"),
        )
        pref.setWikiIssuesFilters(filter)
        assertEquals(filter, pref.wikiIssuesFilters.first())
    }

    @Test
    fun recentIssuesFilters_defaultValue() = runTest {
        assertEquals(
            PrefRecentIssuesFilterBundle(
                dateAdded = PrefRecentIssuesFilter.DateAdded.Unknown,
                storeDate = PrefRecentIssuesFilter.StoreDate.NextWeek,
            ),
            pref.recentIssuesFilters.first(),
        )
    }

    @Test
    fun setRecentIssuesFilters() = runTest {
        val filter = PrefRecentIssuesFilterBundle(
            dateAdded = PrefRecentIssuesFilter.DateAdded.InRange(
                start = Date(
                    GregorianCalendar(2022, 0, 1).timeInMillis
                ),
                end = Date(
                    GregorianCalendar(2022, 0, 2).timeInMillis
                ),
            ),
            storeDate = PrefRecentIssuesFilter.StoreDate.InRange(
                start = Date(
                    GregorianCalendar(2022, 0, 1).timeInMillis
                ),
                end = Date(
                    GregorianCalendar(2022, 0, 2).timeInMillis
                ),
            ),
        )
        pref.setRecentIssuesFilters(filter)
        assertEquals(filter, pref.recentIssuesFilters.first())
    }

    @Test
    fun recentIssuesSort_defaultValue() = runTest {
        assertEquals(
            PrefRecentIssuesSort.StoreDate(
                direction = PrefSortDirection.Desc,
            ),
            pref.recentIssuesSort.first(),
        )
    }

    @Test
    fun setRecentIssuesSort() = runTest {
        val sort = PrefRecentIssuesSort.DateAdded(
            direction = PrefSortDirection.Asc,
        )
        pref.setRecentIssuesSort(sort)
        assertEquals(sort, pref.recentIssuesSort.first())
    }

    @Test
    fun wikiVolumesSort_defaultValue() = runTest {
        assertEquals(
            PrefWikiVolumesSort.DateLastUpdated(
                direction = PrefSortDirection.Desc,
            ),
            pref.wikiVolumesSort.first(),
        )
    }

    @Test
    fun setVolumesIssuesSort() = runTest {
        val sort = PrefWikiVolumesSort.Alphabetical(
            direction = PrefSortDirection.Asc,
        )
        pref.setWikiVolumesSort(sort)
        assertEquals(sort, pref.wikiVolumesSort.first())
    }

    @Test
    fun wikiVolumesFilters_defaultValue() = runTest {
        assertEquals(
            PrefWikiVolumesFilterBundle(
                name = PrefWikiVolumesFilter.Name.Unknown,
                dateAdded = PrefWikiVolumesFilter.DateAdded.Unknown,
                dateLastUpdated = PrefWikiVolumesFilter.DateLastUpdated.Unknown,
            ),
            pref.wikiVolumesFilters.first(),
        )
    }

    @Test
    fun setWikiVolumesFilters() = runTest {
        val filter = PrefWikiVolumesFilterBundle(
            name = PrefWikiVolumesFilter.Name.Contains("test"),
            dateAdded = PrefWikiVolumesFilter.DateAdded.InRange(
                start = Date(
                    GregorianCalendar(2022, 0, 1).timeInMillis
                ),
                end = Date(
                    GregorianCalendar(2022, 0, 2).timeInMillis
                ),
            ),
            dateLastUpdated = PrefWikiVolumesFilter.DateLastUpdated.InRange(
                start = Date(
                    GregorianCalendar(2022, 0, 3).timeInMillis
                ),
                end = Date(
                    GregorianCalendar(2022, 0, 4).timeInMillis
                ),
            ),
        )
        pref.setWikiVolumesFilters(filter)
        assertEquals(filter, pref.wikiVolumesFilters.first())
    }

    @Test
    fun recentVolumesFilters_defaultValue() = runTest {
        assertEquals(
            PrefRecentVolumesFilterBundle(
                dateAdded = PrefRecentVolumesFilter.DateAdded.Unknown,
            ),
            pref.recentVolumesFilters.first(),
        )
    }

    @Test
    fun setRecentVolumesFilters() = runTest {
        val filter = PrefRecentVolumesFilterBundle(
            dateAdded = PrefRecentVolumesFilter.DateAdded.InRange(
                start = Date(
                    GregorianCalendar(2022, 0, 1).timeInMillis
                ),
                end = Date(
                    GregorianCalendar(2022, 0, 2).timeInMillis
                ),
            ),
        )
        pref.setRecentVolumesFilters(filter)
        assertEquals(filter, pref.recentVolumesFilters.first())
    }

    @Test
    fun volumeIssuesSort_defaultValue() = runTest {
        assertEquals(
            PrefVolumeIssuesSort.StoreDate(direction = PrefSortDirection.Asc),
            pref.volumeIssuesSort.first(),
        )
    }

    @Test
    fun setVolumeIssuesSort() = runTest {
        val sort = PrefVolumeIssuesSort.StoreDate(direction = PrefSortDirection.Desc)
        pref.setVolumeIssuesSort(sort)
        assertEquals(sort, pref.volumeIssuesSort.first())
    }

    @Test
    fun favoriteCharactersSort_defaultValue() = runTest {
        assertEquals(
            PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Desc),
            pref.favoriteCharactersSort.first(),
        )
    }

    @Test
    fun setFavoriteCharactersSort() = runTest {
        val sort = PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Asc)
        pref.setFavoriteCharactersSort(sort)
        assertEquals(sort, pref.favoriteCharactersSort.first())
    }

    @Test
    fun favoriteIssuesSort_defaultValue() = runTest {
        assertEquals(
            PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Desc),
            pref.favoriteIssuesSort.first(),
        )
    }

    @Test
    fun setFavoriteIssuesSort() = runTest {
        val sort = PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Asc)
        pref.setFavoriteIssuesSort(sort)
        assertEquals(sort, pref.favoriteIssuesSort.first())
    }

    @Test
    fun favoriteVolumesSort_defaultValue() = runTest {
        assertEquals(
            PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Desc),
            pref.favoriteVolumesSort.first(),
        )
    }

    @Test
    fun setFavoriteVolumesSort() = runTest {
        val sort = PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Asc)
        pref.setFavoriteVolumesSort(sort)
        assertEquals(sort, pref.favoriteVolumesSort.first())
    }

    @Test
    fun favoriteConceptsSort_defaultValue() = runTest {
        assertEquals(
            PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Desc),
            pref.favoriteConceptsSort.first(),
        )
    }

    @Test
    fun setFavoriteConceptsSort() = runTest {
        val sort = PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Asc)
        pref.setFavoriteConceptsSort(sort)
        assertEquals(sort, pref.favoriteConceptsSort.first())
    }

    @Test
    fun favoriteLocationsSort_defaultValue() = runTest {
        assertEquals(
            PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Desc),
            pref.favoriteLocationsSort.first(),
        )
    }

    @Test
    fun setFavoriteLocationsSort() = runTest {
        val sort = PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Asc)
        pref.setFavoriteLocationsSort(sort)
        assertEquals(sort, pref.favoriteLocationsSort.first())
    }

    @Test
    fun favoriteMoviesSort_defaultValue() = runTest {
        assertEquals(
            PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Desc),
            pref.favoriteMoviesSort.first(),
        )
    }

    @Test
    fun setFavoriteMoviesSort() = runTest {
        val sort = PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Asc)
        pref.setFavoriteMoviesSort(sort)
        assertEquals(sort, pref.favoriteMoviesSort.first())
    }

    @Test
    fun favoriteObjectsSort_defaultValue() = runTest {
        assertEquals(
            PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Desc),
            pref.favoriteObjectsSort.first(),
        )
    }

    @Test
    fun setFavoriteObjectsSort() = runTest {
        val sort = PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Asc)
        pref.setFavoriteObjectsSort(sort)
        assertEquals(sort, pref.favoriteObjectsSort.first())
    }

    @Test
    fun favoriteStoryArcsSort_defaultValue() = runTest {
        assertEquals(
            PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Desc),
            pref.favoriteStoryArcsSort.first(),
        )
    }

    @Test
    fun setFavoriteStoryArcsSort() = runTest {
        val sort = PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Asc)
        pref.setFavoriteStoryArcsSort(sort)
        assertEquals(sort, pref.favoriteStoryArcsSort.first())
    }

    @Test
    fun favoriteTeamsSort_defaultValue() = runTest {
        assertEquals(
            PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Desc),
            pref.favoriteTeamsSort.first(),
        )
    }

    @Test
    fun setFavoriteTeamsSort() = runTest {
        val sort = PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Asc)
        pref.setFavoriteTeamsSort(sort)
        assertEquals(sort, pref.favoriteTeamsSort.first())
    }

    @Test
    fun searchHistorySize_defaultValue() = runTest {
        assertEquals(
            10,
            pref.searchHistorySize.first(),
        )
    }

    @Test
    fun setSearchHistorySize() = runTest {
        val historySize = 20
        pref.setSearchHistorySize(historySize)
        assertEquals(historySize, pref.searchHistorySize.first())
    }
}