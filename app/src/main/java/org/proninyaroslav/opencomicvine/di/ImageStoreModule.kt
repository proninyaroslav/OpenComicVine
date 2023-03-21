package org.proninyaroslav.opencomicvine.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.proninyaroslav.opencomicvine.model.ImageStore
import org.proninyaroslav.opencomicvine.model.ImageStoreImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class ImageStoreModule {
    @Binds
    abstract fun bindImageStore(store: ImageStoreImpl): ImageStore
}