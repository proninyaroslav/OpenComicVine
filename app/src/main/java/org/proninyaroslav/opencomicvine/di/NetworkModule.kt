/*
 * Copyright (C) 2023 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of OpenComicVine.
 *
 * OpenComicVine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenComicVine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenComicVine.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.proninyaroslav.opencomicvine.di

import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.proninyaroslav.opencomicvine.BuildConfig
import org.proninyaroslav.opencomicvine.model.COMIC_VINE_BASE_API_URL
import org.proninyaroslav.opencomicvine.model.moshi.*
import org.proninyaroslav.opencomicvine.model.network.ComicVineService
import org.proninyaroslav.opencomicvine.model.network.ConnectivityInterceptor
import org.proninyaroslav.opencomicvine.model.network.UserAgentInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS)
        }
        return interceptor
    }

    @Provides
    fun provideOkHttpClient(
        logInterceptor: HttpLoggingInterceptor,
        connectivityInterceptor: ConnectivityInterceptor,
        userAgentInterceptor: UserAgentInterceptor,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(logInterceptor)
            .addInterceptor(connectivityInterceptor)
            .addInterceptor(userAgentInterceptor)
            .build()

    @Singleton
    @Provides
    fun provideComicVineService(okHttpClient: OkHttpClient): ComicVineService {
        val moshi = Moshi.Builder()
            .add(ComicVineDateConverter)
            .add(EnumJsonAdapterFactory)
            .add(ComicVineFilterConverter)
            .add(ComicVineSortConverter)
            .add(AliasesConverter)
            .add(ComicVineSearchInfoConverter)
            .add(ComicVineSearchResourceTypeListConverter)
            .build()
        return Retrofit.Builder()
            .baseUrl(COMIC_VINE_BASE_API_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addConverterFactory(MoshiStringConverterFactory(moshi))
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .build()
            .create(ComicVineService::class.java)
    }
}
