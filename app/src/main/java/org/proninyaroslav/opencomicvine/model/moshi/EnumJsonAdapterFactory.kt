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

import com.squareup.moshi.*
import java.io.IOException
import java.lang.reflect.Type

object EnumJsonAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<*>? {
        if (annotations.isNotEmpty()) {
            return null // Annotations? This factory doesn't apply.
        }

        if (type !is Class<*> || !type.isEnum) {
            return null // Not enum type? This factory doesn't apply.
        }

        @Suppress("UNCHECKED_CAST")
        return EnumIntSafeJsonAdapter(type as Class<Enum<*>>).nullSafe().lenient()
    }
}

class EnumIntSafeJsonAdapter<T : Enum<T>>(private val enumType: Class<Enum<*>>) :
    JsonAdapter<T>() {
    private val nameStrings: Array<String?>
    private val constants: Array<T>
    private var options: JsonReader.Options

    @Throws(IOException::class)
    override fun fromJson(reader: JsonReader): T {
        val name = reader.nextString()
        val index = options.strings().indexOf(name)
        if (index != -1) return constants[index]

        // We can consume the string safely, we are terminating anyway.
        val path = reader.path
        throw JsonDataException("Expected one of " + listOf(*nameStrings) + " but was " + name + " at path " + path)
    }

    @Throws(IOException::class)
    override fun toJson(writer: JsonWriter, value: T?) {
        writer.value(nameStrings[value!!.ordinal])
    }

    override fun toString(): String = "JsonAdapter(" + enumType.name + ")"

    init {
        try {
            @Suppress("UNCHECKED_CAST")
            constants = (enumType.enumConstants as Array<T>?)!!
            nameStrings = arrayOfNulls(constants.size)
            for (i in constants.indices) {
                val constant = constants[i]
                val annotation = enumType.getField(constant.name).getAnnotation(Json::class.java)
                val name = annotation?.name ?: constant.name
                nameStrings[i] = name
            }
            options = JsonReader.Options.of(*nameStrings)
        } catch (e: NoSuchFieldException) {
            throw AssertionError("Missing field in " + enumType.name, e)
        }
    }
}
