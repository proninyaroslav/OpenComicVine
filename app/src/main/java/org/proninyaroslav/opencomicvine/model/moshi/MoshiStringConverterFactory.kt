package org.proninyaroslav.opencomicvine.model.moshi

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class MoshiStringConverterFactory(private val moshi: Moshi) : Converter.Factory() {
    override fun stringConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): Converter<*, String> {
        val adapter: JsonAdapter<Type> = moshi.adapter(type)
        return StringConverter(adapter)
    }

    private class StringConverter<T>(private val adapter: JsonAdapter<T>) : Converter<T, String> {
        override fun convert(value: T): String? {
            return adapter.toJson(value)?.let { stripEnclosingQuotes(it) }
        }
    }
}