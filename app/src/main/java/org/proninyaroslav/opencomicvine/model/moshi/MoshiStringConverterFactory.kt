/*
 * Copyright (C) 2023 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of OpenComicVine.
 *
 * OpenComicVine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenComicVine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenComicVine.  If not, see <http://www.gnu.org/licenses/>.
 */

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
