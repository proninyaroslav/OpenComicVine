package org.proninyaroslav.opencomicvine.model.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.ToJson
import java.util.*

object ComicVineDateRangeConverter {
    private val dateConverter = ComicVineDateConverter

    @FromJson
    fun fromJson(json: String): Pair<Date?, Date?> {
        val delimiter = json.indexOf('|')
        if (delimiter < 0 || delimiter >= json.length) {
            throw JsonDataException("Invalid format")
        }
        val start = json.substring(0 until delimiter)
        val end = json.substring(delimiter + 1)
        return dateConverter.fromJsonShort(start) to dateConverter.fromJsonShort(end)
    }

    @ToJson
    fun toJson(range: Pair<Date?, Date?>?): String? {
        return range?.run {
            "${dateConverter.toJsonShort(first)}|${dateConverter.toJsonShort(second)}"
        }
    }
}