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
        Response.success("").let {
            assertEquals(
                "Success",
                ComicVineResult.Success(it),
                ApiResponse.Success(it).toComicVineResult(),
            )
        }

        assertEquals(
            "HTTP error",
            ComicVineResult.Failed.HttpError(StatusCode.NotFound),
            ApiResponse.Failure.Error(
                Response.error<String>(
                    404, ByteString.of().toResponseBody()
                )
            ).toComicVineResult(),
        )

        assertEquals(
            "No network connection",
            ComicVineResult.Failed.NoNetworkConnection,
            ApiResponse.Failure.Exception(
                NoNetworkConnectionException()
            ).toComicVineResult(),
        )

        assertEquals(
            "Request timeout",
            ComicVineResult.Failed.RequestTimeout,
            ApiResponse.Failure.Exception(
                SocketTimeoutException()
            ).toComicVineResult(),
        )

        IOException().let {
            assertEquals(
                "Exception",
                ComicVineResult.Failed.Exception(it),
                ApiResponse.Failure.Exception(it).toComicVineResult(),
            )
        }
    }
}