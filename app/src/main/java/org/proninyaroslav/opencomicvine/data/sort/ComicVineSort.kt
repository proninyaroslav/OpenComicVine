package org.proninyaroslav.opencomicvine.data.sort

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

open class ComicVineSort(
    val field: String,
    open val direction: ComicVineSortDirection,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ComicVineSort

        if (field != other.field) return false
        if (direction != other.direction) return false

        return true
    }

    override fun hashCode(): Int {
        var result = field.hashCode()
        result = 31 * result + direction.hashCode()
        return result
    }

    override fun toString(): String {
        return "ComicVineSort(field='$field', direction=$direction)"
    }
}

@JsonClass(generateAdapter = false)
enum class ComicVineSortDirection {
    @Json(name = "asc")
    Asc,

    @Json(name = "desc")
    Desc,
}