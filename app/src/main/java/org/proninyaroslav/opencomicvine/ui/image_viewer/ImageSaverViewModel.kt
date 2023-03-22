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

package org.proninyaroslav.opencomicvine.ui.image_viewer

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.data.ErrorReportInfo
import org.proninyaroslav.opencomicvine.di.IoDispatcher
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.ImageStore
import org.proninyaroslav.opencomicvine.model.state.StoreViewModel
import org.proninyaroslav.opencomicvine.ui.getBitmapInputStream
import org.proninyaroslav.opencomicvine.ui.getCompressFormatByImageType
import org.proninyaroslav.opencomicvine.ui.getMimeType
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ImageSaverViewModel @Inject constructor(
    private val store: ImageStore,
    private val errorReportService: ErrorReportService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : StoreViewModel<
        ImageSaverEvent,
        ImageSaverState,
        ImageSaverEffect,
        >(ImageSaverState.Initial) {
    init {
        on<ImageSaverEvent.Save> { event ->
            event.run {
                save(
                    bitmap = bitmap,
                    url = url,
                    onSuccess = { uri, mimeType ->
                        emitState(
                            ImageSaverState.SaveSuccess(
                                uri = uri,
                                mimeType = mimeType,
                            )
                        )
                        emitEffect(
                            ImageSaverEffect.SaveSuccess
                        )
                    },
                )
            }
        }

        on<ImageSaverEvent.SaveForSharing> { event ->
            event.run {
                save(
                    bitmap = bitmap,
                    url = url,
                    onSuccess = { uri, mimeType ->
                        emitState(
                            ImageSaverState.SaveSuccess(
                                uri = uri,
                                mimeType = mimeType,
                            )
                        )
                        emitEffect(
                            ImageSaverEffect.ReadyToShare(
                                uri = uri,
                                mimeType = mimeType,
                            )
                        )
                    },
                )
            }
        }

        on<ImageSaverEvent.ErrorReport> { event ->
            errorReportService.report(event.info)
        }
    }

    private fun save(
        onSuccess: (Uri, mimeType: String) -> Unit,
        bitmap: Bitmap,
        url: Uri,
    ) {
        if (state.value is ImageSaverState.Saving) {
            return
        }
        emitState(ImageSaverState.Saving)
        val compressFormat = url.getCompressFormatByImageType()
        if (compressFormat == null) {
            emitState(ImageSaverState.SaveFailed.UnsupportedFormat)
            emitEffect(ImageSaverEffect.SaveFailed.UnsupportedFormat)
            return
        }
        viewModelScope.launch(ioDispatcher) {
            bitmap.getBitmapInputStream(compressFormat).use { isStream ->
                val name = url.extractName() ?: UUID.randomUUID().toString()
                when (val res = store.save(imageStream = isStream, name = name)) {
                    is ImageStore.Result.Success -> {
                        onSuccess(res.uri, compressFormat.getMimeType())
                    }
                    is ImageStore.Result.Failed -> {
                        emitState(ImageSaverState.SaveFailed.StoreError(res))
                        emitEffect(ImageSaverEffect.SaveFailed.StoreError(res))
                    }
                }
            }
        }
    }

    private fun Uri.extractName(): String? {
        val url = toString()
        val index = url.lastIndexOf('/')
        return if (index > 0 && index < url.length) {
            url.substring(index + 1)
        } else {
            null
        }
    }
}

sealed interface ImageSaverEvent {
    data class Save(
        val bitmap: Bitmap,
        val url: Uri,
    ) : ImageSaverEvent

    data class SaveForSharing(
        val bitmap: Bitmap,
        val url: Uri,
    ) : ImageSaverEvent

    data class ErrorReport(val info: ErrorReportInfo) : ImageSaverEvent
}

sealed interface ImageSaverState {
    object Initial : ImageSaverState

    object Saving : ImageSaverState

    data class SaveSuccess(
        val uri: Uri,
        val mimeType: String,
    ) : ImageSaverState

    sealed interface SaveFailed : ImageSaverState {
        object UnsupportedFormat : SaveFailed
        data class StoreError(val error: ImageStore.Result.Failed) : SaveFailed
    }
}

sealed interface ImageSaverEffect {
    object SaveSuccess : ImageSaverEffect

    data class ReadyToShare(
        val uri: Uri,
        val mimeType: String,
    ) : ImageSaverEffect

    sealed interface SaveFailed : ImageSaverEffect {
        object UnsupportedFormat : SaveFailed
        data class StoreError(val error: ImageStore.Result.Failed) : SaveFailed
    }
}
