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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.data.ErrorReportInfo
import org.proninyaroslav.opencomicvine.di.IoDispatcher
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.ImageStore
import org.proninyaroslav.opencomicvine.ui.getBitmapInputStream
import org.proninyaroslav.opencomicvine.ui.getCompressFormatByImageType
import org.proninyaroslav.opencomicvine.ui.getMimeType
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ImageSaverViewModel @Inject constructor(
    private val store: ImageStore,
    private val errorReportService: ErrorReportService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _state = MutableStateFlow<ImageSaverState>(ImageSaverState.Initial)
    val state: StateFlow<ImageSaverState> = _state

    fun save(url: Uri, bitmap: Bitmap, saveAndShare: Boolean = false) {
        save(
            bitmap = bitmap,
            url = url,
            onSuccess = { uri, mimeType ->
                _state.value = ImageSaverState.SaveSuccess(
                    uri = uri,
                    mimeType = mimeType,
                    readyToShare = saveAndShare
                )
            },
        )
    }

    fun errorReport(info: ErrorReportInfo) {
        errorReportService.report(info)
    }

    private fun save(
        onSuccess: (Uri, mimeType: String) -> Unit,
        bitmap: Bitmap,
        url: Uri,
    ) {
        if (state.value is ImageSaverState.Saving) {
            return
        }
        _state.value = ImageSaverState.Saving
        val compressFormat = url.getCompressFormatByImageType()
        if (compressFormat == null) {
            _state.value = ImageSaverState.SaveFailed.UnsupportedFormat
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
                        _state.value = ImageSaverState.SaveFailed.StoreError(res)
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

sealed interface ImageSaverState {
    object Initial : ImageSaverState

    object Saving : ImageSaverState

    data class SaveSuccess(
        val uri: Uri,
        val mimeType: String,
        val readyToShare: Boolean,
    ) : ImageSaverState

    sealed interface SaveFailed : ImageSaverState {
        object UnsupportedFormat : SaveFailed
        data class StoreError(val error: ImageStore.Result.Failed) : SaveFailed
    }
}