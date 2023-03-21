package org.proninyaroslav.opencomicvine.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
enum class Gender(val value: Int) {
    @Json(name = "0")
    Other(0),

    @Json(name = "1")
    Male(1),

    @Json(name = "2")
    Female(2),
}