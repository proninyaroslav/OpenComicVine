package org.proninyaroslav.opencomicvine.data.preferences

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
enum class PrefSortDirection {
    Unknown,
    Asc,
    Desc,
}