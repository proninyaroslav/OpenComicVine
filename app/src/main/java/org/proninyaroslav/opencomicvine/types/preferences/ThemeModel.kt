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

package org.proninyaroslav.opencomicvine.types.preferences

import com.squareup.moshi.JsonClass
import dev.zacsweers.moshix.sealed.annotations.TypeLabel

@JsonClass(generateAdapter = true, generator = "sealed:type")
sealed class PrefTheme(val id: Int) {
    @TypeLabel("unknown")
    data object Unknown : PrefTheme(-1)

    @TypeLabel("system")
    data object System : PrefTheme(0)

    @TypeLabel("dark")
    data object Dark : PrefTheme(1)

    @TypeLabel("light")
    data object Light : PrefTheme(2)

    companion object {
        fun fromId(id: Int): PrefTheme = when (id) {
            0 -> System
            1 -> Dark
            2 -> Light
            else -> Unknown
        }
    }
}
