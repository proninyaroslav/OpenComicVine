package org.proninyaroslav.opencomicvine.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.proninyaroslav.opencomicvine.model.AppInfoProvider
import org.proninyaroslav.opencomicvine.model.AppInfoProviderImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppInfoProviderModule {
    @Singleton
    @Binds
    abstract fun bindAppInfoProvider(provider: AppInfoProviderImpl): AppInfoProvider
}