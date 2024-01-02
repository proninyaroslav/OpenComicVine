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
import org.proninyaroslav.opencomicvine.types.sort.ComicVineSort
import org.proninyaroslav.opencomicvine.types.sort.ComicVineSortDirection

object ComicVineSortConverter {
    @Suppress("UNCHECKED_CAST")
    private val directionAdapter = EnumIntSafeJsonAdapter<ComicVineSortDirection>(
        enumType = ComicVineSortDirection::class.java as Class<Enum<*>>
    ).nullSafe().lenient()

    @ToJson
    fun toJson(value: ComicVineSort?): String? {
        return value?.run {
            "$field:${stripEnclosingQuotes(directionAdapter.toJson(value.direction))}"
        }
    }

    @FromJson
    fun fromJson(json: String): ComicVineSort {
        val delimiter = json.indexOf(':')
        if (delimiter < 0 || delimiter >= json.length) {
            throw JsonDataException("Invalid format")
        }
        val direction = directionAdapter.fromJson(json.substring(delimiter + 1))
            ?: throw JsonDataException("Direction must be non-null")
        return ComicVineSort(
            field = json.substring(0 until delimiter),
            direction = direction,
        )
    }
}
