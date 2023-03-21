package org.proninyaroslav.opencomicvine.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.SearchHistoryInfo
import org.proninyaroslav.opencomicvine.data.paging.favorites.*
import org.proninyaroslav.opencomicvine.data.paging.recent.*
import org.proninyaroslav.opencomicvine.data.paging.wiki.*
import org.proninyaroslav.opencomicvine.model.db.favorites.*
import org.proninyaroslav.opencomicvine.model.db.recent.*
import org.proninyaroslav.opencomicvine.model.db.wiki.*

@Database(
    version = 1,
    entities = [
        PagingWikiCharacterItem::class,
        WikiCharacterItemRemoteKeys::class,
        PagingRecentCharacterItem::class,
        RecentCharacterItemRemoteKeys::class,
        PagingWikiIssueItem::class,
        WikiIssueItemRemoteKeys::class,
        PagingRecentIssueItem::class,
        RecentIssueItemRemoteKeys::class,
        PagingWikiVolumeItem::class,
        WikiVolumeItemRemoteKeys::class,
        PagingRecentVolumeItem::class,
        RecentVolumeItemRemoteKeys::class,
        FavoriteInfo::class,
        PagingFavoritesCharacterItem::class,
        FavoritesCharacterItemRemoteKeys::class,
        PagingFavoritesIssueItem::class,
        FavoritesIssueItemRemoteKeys::class,
        PagingFavoritesVolumeItem::class,
        FavoritesVolumeItemRemoteKeys::class,
        PagingFavoritesConceptItem::class,
        FavoritesConceptItemRemoteKeys::class,
        PagingFavoritesLocationItem::class,
        FavoritesLocationItemRemoteKeys::class,
        PagingFavoritesMovieItem::class,
        FavoritesMovieItemRemoteKeys::class,
        PagingFavoritesObjectItem::class,
        FavoritesObjectItemRemoteKeys::class,
        PagingFavoritesPersonItem::class,
        FavoritesPersonItemRemoteKeys::class,
        PagingFavoritesStoryArcItem::class,
        FavoritesStoryArcItemRemoteKeys::class,
        PagingFavoritesTeamItem::class,
        FavoritesTeamItemRemoteKeys::class,
        SearchHistoryInfo::class,
    ],
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wikiCharactersDao(): WikiCharactersDao
    abstract fun wikiCharactersRemoteKeysDao(): WikiCharactersRemoteKeysDao
    abstract fun recentCharactersDao(): RecentCharactersDao
    abstract fun recentCharactersRemoteKeysDao(): RecentCharactersRemoteKeysDao
    abstract fun wikiIssuesDao(): WikiIssuesDao
    abstract fun wikiIssuesRemoteKeysDao(): WikiIssuesRemoteKeysDao
    abstract fun recentIssuesDao(): RecentIssuesDao
    abstract fun recentIssuesRemoteKeysDao(): RecentIssuesRemoteKeysDao
    abstract fun wikiVolumesDao(): WikiVolumesDao
    abstract fun wikiVolumesRemoteKeysDao(): WikiVolumesRemoteKeysDao
    abstract fun recentVolumesDao(): RecentVolumesDao
    abstract fun recentVolumesRemoteKeysDao(): RecentVolumesRemoteKeysDao
    abstract fun favoritesDao(): FavoritesDao
    abstract fun favoritesCharactersRemoteKeysDao(): FavoritesCharactersRemoteKeysDao
    abstract fun favoritesCharactersDao(): FavoritesCharactersDao
    abstract fun favoritesIssuesRemoteKeysDao(): FavoritesIssuesRemoteKeysDao
    abstract fun favoritesIssuesDao(): FavoritesIssuesDao
    abstract fun favoritesVolumesRemoteKeysDao(): FavoritesVolumesRemoteKeysDao
    abstract fun favoritesVolumesDao(): FavoritesVolumesDao
    abstract fun favoritesConceptsRemoteKeysDao(): FavoritesConceptsRemoteKeysDao
    abstract fun favoritesConceptsDao(): FavoritesConceptsDao
    abstract fun favoritesLocationsRemoteKeysDao(): FavoritesLocationsRemoteKeysDao
    abstract fun favoritesLocationsDao(): FavoritesLocationsDao
    abstract fun favoritesMoviesRemoteKeysDao(): FavoritesMoviesRemoteKeysDao
    abstract fun favoritesMoviesDao(): FavoritesMoviesDao
    abstract fun favoritesObjectsRemoteKeysDao(): FavoritesObjectsRemoteKeysDao
    abstract fun favoritesObjectsDao(): FavoritesObjectsDao
    abstract fun favoritesPeopleRemoteKeysDao(): FavoritesPeopleRemoteKeysDao
    abstract fun favoritesPeopleDao(): FavoritesPeopleDao
    abstract fun favoritesStoryArcsRemoteKeysDao(): FavoritesStoryArcsRemoteKeysDao
    abstract fun favoritesStoryArcsDao(): FavoritesStoryArcsDao
    abstract fun favoritesTeamsRemoteKeysDao(): FavoritesTeamsRemoteKeysDao
    abstract fun favoritesTeamsDao(): FavoritesTeamsDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}