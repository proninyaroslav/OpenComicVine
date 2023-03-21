package org.proninyaroslav.opencomicvine.data.filter

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
