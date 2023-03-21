package org.proninyaroslav.opencomicvine.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.db.recent.RecentCharactersDao
import org.proninyaroslav.opencomicvine.model.db.recent.RecentIssuesDao
import org.proninyaroslav.opencomicvine.model.db.recent.RecentVolumesDao
import org.proninyaroslav.opencomicvine.model.db.wiki.WikiCharactersDao
import org.proninyaroslav.opencomicvine.model.db.wiki.WikiIssuesDao
import org.proninyaroslav.opencomicvine.model.db.wiki.WikiVolumesDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext applicationContext: Context): AppDatabase =
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "open_comic_vine",
        ).build()

    @Singleton
    @Provides
    fun provideWikiCharactersDao(appDatabase: AppDatabase): WikiCharactersDao =
        appDatabase.wikiCharactersDao()

    @Singleton
    @Provides
    fun provideRecentCharactersDao(appDatabase: AppDatabase): RecentCharactersDao =
        appDatabase.recentCharactersDao()

    @Singleton
    @Provides
    fun provideWikiIssuesDao(appDatabase: AppDatabase): WikiIssuesDao = appDatabase.wikiIssuesDao()

    @Singleton
    @Provides
    fun provideRecentIssuesDao(appDatabase: AppDatabase): RecentIssuesDao =
        appDatabase.recentIssuesDao()

    @Singleton
    @Provides
    fun provideWikiVolumesDao(appDatabase: AppDatabase): WikiVolumesDao =
        appDatabase.wikiVolumesDao()

    @Singleton
    @Provides
    fun provideRecentVolumesDao(appDatabase: AppDatabase): RecentVolumesDao =
        appDatabase.recentVolumesDao()
}