package org.proninyaroslav.opencomicvine.data

import androidx.compose.runtime.Immutable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Immutable
data class Origin(
    @Json(name = "id")
    val id: Int,

    @Json(name = "name")
    val name: String,
)