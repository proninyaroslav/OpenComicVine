package org.proninyaroslav.opencomicvine.di

import com.skydoves.sandwich.adapters.ApiResponseCallAdapterFactory
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
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(logInterceptor)
            .addInterceptor(connectivityInterceptor)
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