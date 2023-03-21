package org.proninyaroslav.opencomicvine.model.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class ComicVineValuesListConverter<T : Any>(
    private val converter: (String) -> T,
) {
    @FromJson
    fun fromJson(json: String): List<T> = json.split('|').map { converter(it) }

    @ToJson
    fun toJson(list: List<T>?): String? = list?.joinToString("|")
}