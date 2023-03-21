package org.proninyaroslav.opencomicvine.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.proninyaroslav.opencomicvine.model.DateProvider
import org.proninyaroslav.opencomicvine.model.DateProviderImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DateProviderModule {
    @Singleton
    @Binds
    abstract fun bindDateProvider(provider: DateProviderImpl): DateProvider
}