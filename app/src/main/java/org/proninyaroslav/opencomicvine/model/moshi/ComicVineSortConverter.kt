package org.proninyaroslav.opencomicvine.model.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.ToJson
import org.proninyaroslav.opencomicvine.data.sort.ComicVineSort
import org.proninyaroslav.opencomicvine.data.sort.ComicVineSortDirection

object ComicVineSortConverter {
    @Suppress("UNCHECKED_CAST")
    private val directionAdapter = EnumIntSafeJsonAdapter<ComicVineSortDirection>(
        enumType = ComicVineSortDirection::class.java as Class<Enum<*>>
    ).nullSafe().lenient()

    @ToJson
    fun toJson(value: ComicVineSort?): String? {
        return value?.run {
            "$field:${stripEnclosingQuotes(directionAdapter.toJson(value.direction))}"
        }
    }

    @FromJson
    fun fromJson(json: String): ComicVineSort {
        val delimiter = json.indexOf(':')
        if (delimiter < 0 || delimiter >= json.length) {
            throw JsonDataException("Invalid format")
        }
        val direction = directionAdapter.fromJson(json.substring(delimiter + 1))
            ?: throw JsonDataException("Direction must be non-null")
        return ComicVineSort(
            field = json.substring(0 until delimiter),
            direction = direction,
        )
    }
}