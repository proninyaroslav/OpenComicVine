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

package org.proninyaroslav.opencomicvine.types.filter

import org.proninyaroslav.opencomicvine.model.moshi.ComicVineDateRangeConverter
import org.proninyaroslav.opencomicvine.model.moshi.ComicVineValuesListConverter
import java.util.*

sealed class VolumesFilter(key: String, value: String) :
    ComicVineFilter(field = key, value = value) {

    data class Name(val nameValue: String) : VolumesFilter(key = "name", value = nameValue)

    /**
     * @param   start  the start date of the range.
     * @param   end    the end date of the range, inclusive.
     */
    data class DateAdded(val start: Date, val end: Date) : VolumesFilter(
        key = "date_added",
        value = ComicVineDateRangeConverter.toJson(start to end)!!
    )

    /**
     * @param   start  the start date of the range.
     * @param   end    the end date of the range, inclusive.
     */
    data class DateLastUpdated(val start: Date, val end: Date) : VolumesFilter(
        key = "date_last_updated",
        value = ComicVineDateRangeConverter.toJson(start to end)!!
    )

    data class Id(val idList: List<Int>) :
        VolumesFilter(
            key = "id",
            value = ComicVineValuesListConverter { it.toInt() }.toJson(idList)!!
        )
}
