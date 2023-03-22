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

package org.proninyaroslav.opencomicvine.model

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import org.proninyaroslav.opencomicvine.R
import java.io.File
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

interface ImageStore {
    fun save(imageStream: InputStream, name: String): Result

    sealed interface Result {
        data class Success(val uri: Uri) : Result
        data class Failed(val exception: IOException?) : Result
    }
}

class ImageStoreImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ImageStore {
    private val contentResolver = context.contentResolver

    private val mediaStoreDir
        get() = (Environment.DIRECTORY_PICTURES
                + File.separator
                + context.getString(R.string.app_name))

    override fun save(imageStream: InputStream, name: String): ImageStore.Result {
        val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val newImageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, mediaStoreDir)
            }
        }

        return try {
            val uri = contentResolver.insert(imageCollection, newImageDetails)
                ?: return ImageStore.Result.Failed(null)
            contentResolver.openOutputStream(uri).use { outStream ->
                outStream?.let {
                    imageStream.copyTo(outStream)
                    ImageStore.Result.Success(uri)
                } ?: ImageStore.Result.Failed(null)
            }
        } catch (e: IOException) {
            ImageStore.Result.Failed(e)
        }
    }
}
