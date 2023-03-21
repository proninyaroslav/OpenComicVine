package org.proninyaroslav.opencomicvine.data

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.junit.Assert.assertEquals
import org.junit.Test
import org.proninyaroslav.opencomicvine.model.moshi.EnumJsonAdapterFactory

class ComicVineResponseTest {
    private fun Moshi.parse(json: Map<String, Any>): ComicVineResponse<String>? {
        val mapType = Types.newParameterizedType(
            MutableMap::class.java,
            String::class.java,
            Any::class.java,
        )
        val mapAdapter: JsonAdapter<Map<String, Any>> = adapter(mapType)
        val type = Types.newParameterizedType(ComicVineResponse::class.java, String::class.java)
        val adapter: JsonAdapter<ComicVineResponse<String>> = adapter(type)

        val jsonStr = mapAdapter.toJson(json)
        return adapter.fromJson(jsonStr)
    }

    @Test
    fun `Parse result`() {
        val expectedResult = ComicVineResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 100,
            offset = 0,
            numberOfPageResults = 100,
            numberOfTotalResults = 100000,
            results = "",
        )
        val json = mapOf(
            "status_code" to 1,
            "error" to "OK",
            "limit" to 100,
            "offset" to 0,
            "number_of_page_results" to 100,
            "number_of_total_results" to 100000,
            "results" to ""
        )

        val moshi = Moshi.Builder().add(EnumJsonAdapterFactory).build()
        assertEquals(expectedResult, moshi.parse(json))
    }

    @Test
    fun `Parse status code`() {
        val moshi = Moshi.Builder().add(EnumJsonAdapterFactory).build()

        assertEquals(
            StatusCode.OK.name,
            StatusCode.OK,
            moshi.parse<StatusCode>("1"),
        )
        assertEquals(
            StatusCode.InvalidAPIKey.name,
            StatusCode.InvalidAPIKey,
            moshi.parse<StatusCode>("100"),
        )
        assertEquals(
            StatusCode.ObjectNotFound.name,
            StatusCode.ObjectNotFound,
            moshi.parse<StatusCode>("101"),
        )
        assertEquals(
            StatusCode.URLFormatError.name,
            StatusCode.URLFormatError,
            moshi.parse<StatusCode>("102"),
        )
        assertEquals(
            StatusCode.FilterError.name,
            StatusCode.FilterError,
            moshi.parse<StatusCode>("104"),
        )
    }
}