package org.proninyaroslav.opencomicvine.data

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

inline fun <reified T> Moshi.parse(json: String): T? {
    val adapter = adapter(T::class.java)
    return adapter.fromJson(json)
}

inline fun <reified T> Moshi.parse(json: Map<String, Any>): T? {
    val mapType = Types.newParameterizedType(
        MutableMap::class.java,
        String::class.java,
        Any::class.java,
    )
    val mapAdapter: JsonAdapter<Map<String, Any>> = adapter(mapType)
    val adapter = adapter(T::class.java)

    val jsonStr = mapAdapter.toJson(json)
    return adapter.fromJson(jsonStr)
}