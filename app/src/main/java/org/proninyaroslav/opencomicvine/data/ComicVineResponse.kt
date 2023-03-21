package org.proninyaroslav.opencomicvine.data

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