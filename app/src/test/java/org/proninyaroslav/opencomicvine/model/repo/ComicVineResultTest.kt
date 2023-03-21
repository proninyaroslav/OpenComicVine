package org.proninyaroslav.opencomicvine.model.repo

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.StatusCode
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.ByteString
import okio.IOException
import org.junit.Assert.assertEquals
import org.junit.Test
import org.proninyaroslav.opencomicvine.model.network.NoNetworkConnectionException
import retrofit2.Response
import java.net.SocketTimeoutException

class ComicVineResultTest {
    @Test
    fun toComicVineResult() {
        assertEquals(
            "Success",
            ComicVineResult.Success(""),
            ApiResponse.Success(Response.success("")).toComicVineResult(),
        )
        assertEquals(
            "HTTP error",
            ComicVineResult.Failed.HttpError(StatusCode.NotFound),
            ApiResponse.Failure.Error<String>(
                Response.error(
                    404, ByteString.of().toResponseBody()
                )
            ).toComicVineResult(),
        )
        assertEquals(
            "No network connection",
            ComicVineResult.Failed.NoNetworkConnection,
            ApiResponse.Failure.Exception<String>(
                NoNetworkConnectionException()
            ).toComicVineResult(),
        )
        assertEquals(
            "Request timeout",
            ComicVineResult.Failed.RequestTimeout,
            ApiResponse.Failure.Exception<String>(
                SocketTimeoutException()
            ).toComicVineResult(),
        )
        val ioException = IOException()
        assertEquals(
            "Exception",
            ComicVineResult.Failed.Exception(ioException),
            ApiResponse.Failure.Exception<String>(
                ioException
            ).toComicVineResult(),
        )
    }
}