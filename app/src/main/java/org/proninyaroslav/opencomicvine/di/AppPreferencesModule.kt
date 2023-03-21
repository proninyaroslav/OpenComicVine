package org.proninyaroslav.opencomicvine.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.AppPreferencesImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppPreferencesModule {
    @Singleton
    @Binds
    abstract fun bindAppPreferences(appPreferences: AppPreferencesImpl): AppPreferences
}