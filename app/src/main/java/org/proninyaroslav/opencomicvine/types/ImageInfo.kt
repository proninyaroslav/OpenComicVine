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

package org.proninyaroslav.opencomicvine.types

import androidx.compose.runtime.Immutable
import androidx.room.Ignore
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Immutable
data class ImageInfo(
    @Json(name = "icon_url")
    val iconUrl: String,

    @Json(name = "medium_url")
    val mediumUrl: String,

    @Json(name = "screen_url")
    val screenUrl: String,

    @Json(name = "screen_large_url")
    val screenLargeUrl: String,

    @Json(name = "small_url")
    val smallUrl: String,

    @Json(name = "super_url")
    val superUrl: String,

    @Json(name = "thumb_url")
    val thumbUrl: String,

    @Json(name = "tiny_url")
    val tinyUrl: String,

    @Json(name = "original_url")
    val originalUrl: String,

    @Json(name = "image_tags")
    val imageTags: String?,
) {
    @Ignore
    val squareMedium: String =
        mediumUrl.replace("/(scale_|original)\\w*/".toRegex(), "/square_medium/")
}
