package org.proninyaroslav.opencomicvine.data

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
@Immutable
data class CharacterInfo(
    @Json(name = "id")
    val id: Int,

    @Json(name = "name")
    val name: String,

    @Json(name = "gender")
    val gender: Gender,

    @Embedded(prefix = "image_")
    @Json(name = "image")
    val image: ImageInfo,

    @Json(name = "date_added")
    val dateAdded: Date,

    @Json(name = "date_last_updated")
    val dateLastUpdated: Date,
)
