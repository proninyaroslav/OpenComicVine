package org.proninyaroslav.opencomicvine.model.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.ToJson
import org.proninyaroslav.opencomicvine.data.ComicVineSearchResourceType
import org.proninyaroslav.opencomicvine.data.ComicVineSearchResourceTypeList

object ComicVineSearchResourceTypeListConverter {
    @ToJson
    fun toJson(value: ComicVineSearchResourceTypeList?): String? =
        value?.let { value.list.joinToString(",") { it.value } }

    @FromJson
    fun fromJson(json: String?): ComicVineSearchResourceTypeList? =
        json?.let {
            ComicVineSearchResourceTypeList(
                json.split(",").map {
                    try {
                        ComicVineSearchResourceType.from(it)
                    } catch (e: IllegalArgumentException) {
                        throw JsonDataException(e)
                    }
                }
            )
        }
}