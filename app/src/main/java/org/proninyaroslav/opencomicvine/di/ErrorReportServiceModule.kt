package org.proninyaroslav.opencomicvine.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.ErrorReportServiceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ErrorReportServiceModule {
    @Singleton
    @Binds
    abstract fun bindErrorReportService(repo: ErrorReportServiceImpl): ErrorReportService
}