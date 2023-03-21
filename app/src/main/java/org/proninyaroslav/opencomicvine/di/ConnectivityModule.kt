package org.proninyaroslav.opencomicvine.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.proninyaroslav.opencomicvine.model.network.AppConnectivityManager
import org.proninyaroslav.opencomicvine.model.network.AppConnectivityManagerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ConnectivityModule {
    @Singleton
    @Binds
    abstract fun bindAppConnectivityManager(
        manager: AppConnectivityManagerImpl
    ): AppConnectivityManager
}