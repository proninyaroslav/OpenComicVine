package org.proninyaroslav.opencomicvine.data

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Immutable
data class PersonInfo(
    @Json(name = "id")
    val id: Int,

    @Json(name = "name")
    val name: String,

    @Embedded(prefix = "image_")
    @Json(name = "image")
    val image: ImageInfo,
)
