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