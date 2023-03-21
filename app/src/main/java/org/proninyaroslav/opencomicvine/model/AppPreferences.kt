package org.proninyaroslav.opencomicvine.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.squareup.moshi.Moshi
import dev.zacsweers.moshix.sealed.annotations.NestedSealed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.proninyaroslav.opencomicvine.data.preferences.*
import org.proninyaroslav.opencomicvine.model.moshi.ComicVineDateConverter
import javax.inject.Inject

interface AppPreferences {
    val wikiCharactersSort: Flow<PrefWikiCharactersSort>

    suspend fun setWikiCharactersSort(value: PrefWikiCharactersSort)

    val wikiCharactersFilters: Flow<PrefWikiCharactersFilterBundle>

    suspend fun setWikiCharactersFilters(value: PrefWikiCharactersFilterBundle)

    val recentCharactersFilters: Flow<PrefRecentCharactersFilterBundle>

    suspend fun setRecentCharactersFilters(value: PrefRecentCharactersFilterBundle)

    val wikiIssuesSort: Flow<PrefWikiIssuesSort>

    suspend fun setWikiIssuesSort(value: PrefWikiIssuesSort)

    val wikiIssuesFilters: Flow<PrefWikiIssuesFilterBundle>

    suspend fun setWikiIssuesFilters(value: PrefWikiIssuesFilterBundle)

    val recentIssuesFilters: Flow<PrefRecentIssuesFilterBundle>

    suspend fun setRecentIssuesFilters(value: PrefRecentIssuesFilterBundle)

    val recentIssuesSort: Flow<PrefRecentIssuesSort>

    suspend fun setRecentIssuesSort(value: PrefRecentIssuesSort)

    val wikiVolumesSort: Flow<PrefWikiVolumesSort>

    suspend fun setWikiVolumesSort(value: PrefWikiVolumesSort)

    val wikiVolumesFilters: Flow<PrefWikiVolumesFilterBundle>

    suspend fun setWikiVolumesFilters(value: PrefWikiVolumesFilterBundle)

    val recentVolumesFilters: Flow<PrefRecentVolumesFilterBundle>

    suspend fun setRecentVolumesFilters(value: PrefRecentVolumesFilterBundle)

    val volumeIssuesSort: Flow<PrefVolumeIssuesSort>

    suspend fun setVolumeIssuesSort(value: PrefVolumeIssuesSort)

    val favoriteCharactersSort: Flow<PrefFavoritesSort>

    suspend fun setFavoriteCharactersSort(value: PrefFavoritesSort)

    val favoriteIssuesSort: Flow<PrefFavoritesSort>

    suspend fun setFavoriteIssuesSort(value: PrefFavoritesSort)

    val favoriteVolumesSort: Flow<PrefFavoritesSort>

    suspend fun setFavoriteVolumesSort(value: PrefFavoritesSort)

    val favoriteConceptsSort: Flow<PrefFavoritesSort>

    suspend fun setFavoriteConceptsSort(value: PrefFavoritesSort)

    val favoriteLocationsSort: Flow<PrefFavoritesSort>

    suspend fun setFavoriteLocationsSort(value: PrefFavoritesSort)

    val favoriteMoviesSort: Flow<PrefFavoritesSort>

    suspend fun setFavoriteMoviesSort(value: PrefFavoritesSort)

    val favoriteObjectsSort: Flow<PrefFavoritesSort>

    suspend fun setFavoriteObjectsSort(value: PrefFavoritesSort)

    val favoritePeopleSort: Flow<PrefFavoritesSort>

    suspend fun setFavoritePeopleSort(value: PrefFavoritesSort)

    val favoriteStoryArcsSort: Flow<PrefFavoritesSort>

    suspend fun setFavoriteStoryArcsSort(value: PrefFavoritesSort)

    val favoriteTeamsSort: Flow<PrefFavoritesSort>

    suspend fun setFavoriteTeamsSort(value: PrefFavoritesSort)

    val searchFilter: Flow<PrefSearchFilterBundle>

    suspend fun setSearchFilter(value: PrefSearchFilterBundle)

    val searchHistorySize: Flow<Int>

    suspend fun setSearchHistorySize(value: Int)

    val theme: Flow<PrefTheme>

    suspend fun setTheme(value: PrefTheme)
}

class AppPreferencesImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AppPreferences {
    private val moshi = Moshi.Builder()
        .add(NestedSealed.Factory())
        .add(ComicVineDateConverter)
        .build()

    private inline fun <reified T> get(pref: Preference<T, String>): Flow<T> {
        val adapter = moshi.adapter(T::class.java)
        return dataStore.data.map {
            val json = it[pref.key]
            json?.let { adapter.fromJson(json) } ?: pref.defaultValue
        }.distinctUntilChanged()
    }

    private suspend inline fun <reified T> set(pref: Preference<T, String>, value: T) {
        val adapter = moshi.adapter(T::class.java)
        dataStore.edit { settings ->
            settings[pref.key] = adapter.toJson(value)
        }
    }

    @JvmName("get_int")
    private fun get(pref: Preference<Int, Int>): Flow<Int> {
        return dataStore.data.map {
            val value = it[pref.key]
            value ?: pref.defaultValue
        }.distinctUntilChanged()
    }

    @JvmName("set_int")
    private suspend fun set(pref: Preference<Int, Int>, value: Int) {
        dataStore.edit { settings ->
            settings[pref.key] = value
        }
    }

    override val wikiCharactersSort: Flow<PrefWikiCharactersSort> =
        get(Preference.WikiCharactersSort)

    override suspend fun setWikiCharactersSort(value: PrefWikiCharactersSort) = set(
        pref = Preference.WikiCharactersSort,
        value = value,
    )

    override val wikiCharactersFilters: Flow<PrefWikiCharactersFilterBundle> =
        get(Preference.WikiCharactersFilters)

    override suspend fun setWikiCharactersFilters(value: PrefWikiCharactersFilterBundle) = set(
        pref = Preference.WikiCharactersFilters,
        value = value,
    )

    override val recentCharactersFilters: Flow<PrefRecentCharactersFilterBundle> =
        get(Preference.RecentCharactersFilters)

    override suspend fun setRecentCharactersFilters(value: PrefRecentCharactersFilterBundle) = set(
        pref = Preference.RecentCharactersFilters,
        value = value,
    )

    override val wikiIssuesSort: Flow<PrefWikiIssuesSort> = get(Preference.WikiIssuesSort)

    override suspend fun setWikiIssuesSort(value: PrefWikiIssuesSort) = set(
        pref = Preference.WikiIssuesSort,
        value = value,
    )

    override val wikiIssuesFilters: Flow<PrefWikiIssuesFilterBundle> =
        get(Preference.WikiIssuesFilters)

    override suspend fun setWikiIssuesFilters(value: PrefWikiIssuesFilterBundle) = set(
        pref = Preference.WikiIssuesFilters,
        value = value,
    )

    override val recentIssuesFilters: Flow<PrefRecentIssuesFilterBundle> =
        get(Preference.RecentIssuesFilters)

    override suspend fun setRecentIssuesFilters(value: PrefRecentIssuesFilterBundle) = set(
        pref = Preference.RecentIssuesFilters,
        value = value,
    )

    override val recentIssuesSort: Flow<PrefRecentIssuesSort> = get(Preference.RecentIssuesSort)

    override suspend fun setRecentIssuesSort(value: PrefRecentIssuesSort) = set(
        pref = Preference.RecentIssuesSort,
        value = value,
    )

    override val wikiVolumesSort: Flow<PrefWikiVolumesSort> = get(Preference.WikiVolumesSort)

    override suspend fun setWikiVolumesSort(value: PrefWikiVolumesSort) = set(
        pref = Preference.WikiVolumesSort,
        value = value,
    )

    override val wikiVolumesFilters: Flow<PrefWikiVolumesFilterBundle> =
        get(Preference.WikiVolumesFilters)

    override suspend fun setWikiVolumesFilters(value: PrefWikiVolumesFilterBundle) = set(
        pref = Preference.WikiVolumesFilters,
        value = value,
    )

    override val recentVolumesFilters: Flow<PrefRecentVolumesFilterBundle> =
        get(Preference.RecentVolumesFilters)

    override suspend fun setRecentVolumesFilters(value: PrefRecentVolumesFilterBundle) = set(
        pref = Preference.RecentVolumesFilters,
        value = value,
    )

    override val volumeIssuesSort: Flow<PrefVolumeIssuesSort> = get(Preference.VolumeIssuesSort)

    override suspend fun setVolumeIssuesSort(value: PrefVolumeIssuesSort) = set(
        pref = Preference.VolumeIssuesSort,
        value = value,
    )

    override val favoriteCharactersSort: Flow<PrefFavoritesSort> =
        get(Preference.FavoriteCharactersSort)

    override suspend fun setFavoriteCharactersSort(value: PrefFavoritesSort) = set(
        pref = Preference.FavoriteCharactersSort,
        value = value,
    )

    override val favoriteIssuesSort: Flow<PrefFavoritesSort> =
        get(Preference.FavoriteIssuesSort)

    override suspend fun setFavoriteIssuesSort(value: PrefFavoritesSort) = set(
        pref = Preference.FavoriteIssuesSort,
        value = value,
    )

    override val favoriteVolumesSort: Flow<PrefFavoritesSort> =
        get(Preference.FavoriteVolumesSort)

    override suspend fun setFavoriteVolumesSort(value: PrefFavoritesSort) = set(
        pref = Preference.FavoriteVolumesSort,
        value = value,
    )

    override val favoriteConceptsSort: Flow<PrefFavoritesSort> =
        get(Preference.FavoriteConceptsSort)

    override suspend fun setFavoriteConceptsSort(value: PrefFavoritesSort) = set(
        pref = Preference.FavoriteConceptsSort,
        value = value,
    )

    override val favoriteLocationsSort: Flow<PrefFavoritesSort> =
        get(Preference.FavoriteLocationsSort)

    override suspend fun setFavoriteLocationsSort(value: PrefFavoritesSort) = set(
        pref = Preference.FavoriteLocationsSort,
        value = value,
    )

    override val favoriteMoviesSort: Flow<PrefFavoritesSort> =
        get(Preference.FavoriteMoviesSort)

    override suspend fun setFavoriteMoviesSort(value: PrefFavoritesSort) = set(
        pref = Preference.FavoriteMoviesSort,
        value = value,
    )

    override val favoriteObjectsSort: Flow<PrefFavoritesSort> =
        get(Preference.FavoriteObjectsSort)

    override suspend fun setFavoriteObjectsSort(value: PrefFavoritesSort) = set(
        pref = Preference.FavoriteObjectsSort,
        value = value,
    )

    override val favoritePeopleSort: Flow<PrefFavoritesSort> =
        get(Preference.FavoritePeopleSort)

    override suspend fun setFavoritePeopleSort(value: PrefFavoritesSort) = set(
        pref = Preference.FavoritePeopleSort,
        value = value,
    )

    override val favoriteStoryArcsSort: Flow<PrefFavoritesSort> =
        get(Preference.FavoriteStoryArcsSort)

    override suspend fun setFavoriteStoryArcsSort(value: PrefFavoritesSort) = set(
        pref = Preference.FavoriteStoryArcsSort,
        value = value,
    )

    override val favoriteTeamsSort: Flow<PrefFavoritesSort> =
        get(Preference.FavoriteTeamsSort)

    override suspend fun setFavoriteTeamsSort(value: PrefFavoritesSort) = set(
        pref = Preference.FavoriteTeamsSort,
        value = value,
    )

    override val searchFilter: Flow<PrefSearchFilterBundle> =
        get(Preference.SearchFilter)

    override suspend fun setSearchFilter(value: PrefSearchFilterBundle) = set(
        pref = Preference.SearchFilter,
        value = value,
    )

    override val searchHistorySize: Flow<Int> = get(Preference.SearchHistorySize)

    override suspend fun setSearchHistorySize(value: Int) = set(
        pref = Preference.SearchHistorySize,
        value = value,
    )

    override val theme: Flow<PrefTheme> = get(Preference.Theme)

    override suspend fun setTheme(value: PrefTheme) = set(
        pref = Preference.Theme,
        value = value,
    )

    private sealed class Preference<T, K>(
        val key: Preferences.Key<K>,
        val defaultValue: T,
    ) {
        object WikiCharactersSort : Preference<PrefWikiCharactersSort, String>(
            key = stringPreferencesKey("pref_key_wiki_characters_sort"),
            defaultValue = PrefWikiCharactersSort.DateLastUpdated(
                direction = PrefSortDirection.Desc,
            ),
        )

        object WikiCharactersFilters : Preference<PrefWikiCharactersFilterBundle, String>(
            key = stringPreferencesKey("pref_key_wiki_characters_filters"),
            defaultValue = PrefWikiCharactersFilterBundle(
                gender = PrefWikiCharactersFilter.Gender.Unknown,
                name = PrefWikiCharactersFilter.Name.Unknown,
                dateAdded = PrefWikiCharactersFilter.DateAdded.Unknown,
                dateLastUpdated = PrefWikiCharactersFilter.DateLastUpdated.Unknown,
            ),
        )

        object RecentCharactersFilters : Preference<PrefRecentCharactersFilterBundle, String>(
            key = stringPreferencesKey("pref_key_recent_characters_filters"),
            defaultValue = PrefRecentCharactersFilterBundle(
                dateAdded = PrefRecentCharactersFilter.DateAdded.Unknown,
            ),
        )

        object WikiIssuesSort : Preference<PrefWikiIssuesSort, String>(
            key = stringPreferencesKey("pref_key_wiki_issues_sort"),
            defaultValue = PrefWikiIssuesSort.DateLastUpdated(
                direction = PrefSortDirection.Desc,
            ),
        )

        object WikiIssuesFilters : Preference<PrefWikiIssuesFilterBundle, String>(
            key = stringPreferencesKey("pref_key_wiki_issues_filters"),
            defaultValue = PrefWikiIssuesFilterBundle(
                name = PrefWikiIssuesFilter.Name.Unknown,
                dateAdded = PrefWikiIssuesFilter.DateAdded.Unknown,
                dateLastUpdated = PrefWikiIssuesFilter.DateLastUpdated.Unknown,
                coverDate = PrefWikiIssuesFilter.CoverDate.Unknown,
                storeDate = PrefWikiIssuesFilter.StoreDate.Unknown,
                issueNumber = PrefWikiIssuesFilter.IssueNumber.Unknown,
            )
        )

        object RecentIssuesFilters : Preference<PrefRecentIssuesFilterBundle, String>(
            key = stringPreferencesKey("pref_key_recent_issues_filters"),
            defaultValue = PrefRecentIssuesFilterBundle(
                dateAdded = PrefRecentIssuesFilter.DateAdded.Unknown,
                storeDate = PrefRecentIssuesFilter.StoreDate.NextWeek
            ),
        )

        object RecentIssuesSort : Preference<PrefRecentIssuesSort, String>(
            key = stringPreferencesKey("pref_key_recent_issues_sort"),
            defaultValue = PrefRecentIssuesSort.StoreDate(
                direction = PrefSortDirection.Desc,
            ),
        )

        object WikiVolumesSort : Preference<PrefWikiVolumesSort, String>(
            key = stringPreferencesKey("pref_key_wiki_volumes_sort"),
            defaultValue = PrefWikiVolumesSort.DateLastUpdated(
                direction = PrefSortDirection.Desc,
            )
        )

        object WikiVolumesFilters : Preference<PrefWikiVolumesFilterBundle, String>(
            key = stringPreferencesKey("pref_key_wiki_volumes_filters"),
            defaultValue = PrefWikiVolumesFilterBundle(
                name = PrefWikiVolumesFilter.Name.Unknown,
                dateAdded = PrefWikiVolumesFilter.DateAdded.Unknown,
                dateLastUpdated = PrefWikiVolumesFilter.DateLastUpdated.Unknown,
            ),
        )

        object RecentVolumesFilters : Preference<PrefRecentVolumesFilterBundle, String>(
            key = stringPreferencesKey("pref_key_recent_volumes_filters"),
            defaultValue = PrefRecentVolumesFilterBundle(
                dateAdded = PrefRecentVolumesFilter.DateAdded.Unknown,
            ),
        )

        object VolumeIssuesSort : Preference<PrefVolumeIssuesSort, String>(
            key = stringPreferencesKey("pref_key_volume_issues_sort"),
            defaultValue = PrefVolumeIssuesSort.StoreDate(
                direction = PrefSortDirection.Asc,
            ),
        )

        object FavoriteCharactersSort : Preference<PrefFavoritesSort, String>(
            key = stringPreferencesKey("pref_key_favorite_characters_sort"),
            defaultValue = PrefFavoritesSort.DateAdded(
                direction = PrefSortDirection.Desc,
            ),
        )

        object FavoriteIssuesSort : Preference<PrefFavoritesSort, String>(
            key = stringPreferencesKey("pref_key_favorite_issues_sort"),
            defaultValue = PrefFavoritesSort.DateAdded(
                direction = PrefSortDirection.Desc,
            ),
        )

        object FavoriteVolumesSort : Preference<PrefFavoritesSort, String>(
            key = stringPreferencesKey("pref_key_favorite_volumes_sort"),
            defaultValue = PrefFavoritesSort.DateAdded(
                direction = PrefSortDirection.Desc,
            ),
        )

        object FavoriteConceptsSort : Preference<PrefFavoritesSort, String>(
            key = stringPreferencesKey("pref_key_favorite_concepts_sort"),
            defaultValue = PrefFavoritesSort.DateAdded(
                direction = PrefSortDirection.Desc,
            ),
        )

        object FavoriteLocationsSort : Preference<PrefFavoritesSort, String>(
            key = stringPreferencesKey("pref_key_favorite_locations_sort"),
            defaultValue = PrefFavoritesSort.DateAdded(
                direction = PrefSortDirection.Desc,
            ),
        )

        object FavoriteMoviesSort : Preference<PrefFavoritesSort, String>(
            key = stringPreferencesKey("pref_key_favorite_movies_sort"),
            defaultValue = PrefFavoritesSort.DateAdded(
                direction = PrefSortDirection.Desc,
            ),
        )

        object FavoriteObjectsSort : Preference<PrefFavoritesSort, String>(
            key = stringPreferencesKey("pref_key_favorite_objects_sort"),
            defaultValue = PrefFavoritesSort.DateAdded(
                direction = PrefSortDirection.Desc,
            ),
        )

        object FavoritePeopleSort : Preference<PrefFavoritesSort, String>(
            key = stringPreferencesKey("pref_key_favorite_people_sort"),
            defaultValue = PrefFavoritesSort.DateAdded(
                direction = PrefSortDirection.Desc,
            ),
        )

        object FavoriteStoryArcsSort : Preference<PrefFavoritesSort, String>(
            key = stringPreferencesKey("pref_key_favorite_story_arcs_sort"),
            defaultValue = PrefFavoritesSort.DateAdded(
                direction = PrefSortDirection.Desc,
            ),
        )

        object FavoriteTeamsSort : Preference<PrefFavoritesSort, String>(
            key = stringPreferencesKey("pref_key_favorite_teams_sort"),
            defaultValue = PrefFavoritesSort.DateAdded(
                direction = PrefSortDirection.Desc,
            ),
        )

        object SearchFilter : Preference<PrefSearchFilterBundle, String>(
            key = stringPreferencesKey("pref_key_search_filter"),
            defaultValue = PrefSearchFilterBundle(
                resources = PrefSearchFilter.Resources.All,
            ),
        )

        object SearchHistorySize : Preference<Int, Int>(
            key = intPreferencesKey("pref_key_search_history_size"),
            defaultValue = 10,
        )

        object Theme : Preference<PrefTheme, String>(
            key = stringPreferencesKey("pref_key_theme"),
            defaultValue = PrefTheme.System,
        )
    }
}