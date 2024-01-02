package org.proninyaroslav.opencomicvine.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.proninyaroslav.opencomicvine.model.network.UserAgentProvider
import org.proninyaroslav.opencomicvine.model.network.UserAgentProviderImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserAgentProviderModule {
    @Singleton
    @Binds
    abstract fun bindUserAgentModule(provider: UserAgentProviderImpl): UserAgentProvider
}
