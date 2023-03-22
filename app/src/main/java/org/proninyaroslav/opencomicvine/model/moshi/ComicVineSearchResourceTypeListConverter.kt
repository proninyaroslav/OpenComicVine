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

package org.proninyaroslav.opencomicvine.model.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.ToJson
import org.proninyaroslav.opencomicvine.data.ComicVineSearchResourceType
import org.proninyaroslav.opencomicvine.data.ComicVineSearchResourceTypeList

object ComicVineSearchResourceTypeListConverter {
    @ToJson
    fun toJson(value: ComicVineSearchResourceTypeList?): String? =
        value?.let { value.list.joinToString(",") { it.value } }

    @FromJson
    fun fromJson(json: String?): ComicVineSearchResourceTypeList? =
        json?.let {
            ComicVineSearchResourceTypeList(
                json.split(",").map {
                    try {
                        ComicVineSearchResourceType.from(it)
                    } catch (e: IllegalArgumentException) {
                        throw JsonDataException(e)
                    }
                }
            )
        }
}
