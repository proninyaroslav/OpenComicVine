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

package org.proninyaroslav.opencomicvine.model.repo

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.StatusCode
import org.proninyaroslav.opencomicvine.model.network.NoNetworkConnectionException
import java.net.SocketTimeoutException

sealed interface ComicVineResult<out T> {
    data class Success<T>(val response: T) : ComicVineResult<T>

    sealed interface Failed : ComicVineResult<Nothing> {
        object NoNetworkConnection : Failed

        object RequestTimeout : Failed

        data class HttpError(val statusCode: StatusCode) : Failed

        data class ApiKeyError(val error: ApiKeyRepository.GetResult.Failed) : Failed

        data class Exception(val exception: Throwable) : Failed
    }
}

fun <T> ApiResponse<T>.toComicVineResult(): ComicVineResult<T> =
    when (this) {
        is ApiResponse.Success -> {
            ComicVineResult.Success(data)
        }
        is ApiResponse.Failure.Error -> {
            ComicVineResult.Failed.HttpError(statusCode)
        }
        is ApiResponse.Failure.Exception -> {
            when (exception) {
                is NoNetworkConnectionException ->
                    ComicVineResult.Failed.NoNetworkConnection
                is SocketTimeoutException ->
                    ComicVineResult.Failed.RequestTimeout
                else -> ComicVineResult.Failed.Exception(exception)
            }
        }
    }
