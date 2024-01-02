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

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ComicVineResponse<T>(
    @Json(name = "status_code")
    val statusCode: StatusCode,

    @Json(name = "error")
    val error: String,

    @Json(name = "limit")
    val limit: Int,

    @Json(name = "offset")
    val offset: Int,

    @Json(name = "number_of_page_results")
    val numberOfPageResults: Int,

    @Json(name = "number_of_total_results")
    val numberOfTotalResults: Int,

    @Json(name = "results")
    val results: T,
)

@JsonClass(generateAdapter = false)
enum class StatusCode(val value: Int) {
    @Json(name = "1")
    OK(1),

    @Json(name = "100")
    InvalidAPIKey(100),

    @Json(name = "101")
    ObjectNotFound(101),

    @Json(name = "102")
    URLFormatError(102),

    @Json(name = "104")
    FilterError(104),
}

fun <T, R> ComicVineResponse<T>.copyResults(results: R): ComicVineResponse<R> =
    ComicVineResponse(
        statusCode = statusCode,
        error = error,
        limit = limit,
        offset = offset,
        numberOfPageResults = numberOfPageResults,
        numberOfTotalResults = numberOfTotalResults,
        results = results,
    )
