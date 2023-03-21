package org.proninyaroslav.opencomicvine.model.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.ToJson
import org.proninyaroslav.opencomicvine.data.filter.ComicVineFilter

object ComicVineFilterConverter {
    @ToJson
    fun toJson(filter: ComicVineFilter?): String? = filter?.run { "$field:$value" }

    @FromJson
    fun fromJson(json: String): ComicVineFilter {
        val delimiter = json.indexOf(':')
        if (delimiter < 0 || delimiter >= json.length) {
            throw JsonDataException("Invalid format")
        }
        return ComicVineFilter(
            field = json.substring(0 until delimiter),
            value = json.substring(delimiter + 1),
        )
    }
}