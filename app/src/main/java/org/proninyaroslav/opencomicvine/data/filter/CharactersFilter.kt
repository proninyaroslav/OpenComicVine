package org.proninyaroslav.opencomicvine.data.filter

import org.proninyaroslav.opencomicvine.model.moshi.ComicVineDateRangeConverter
import org.proninyaroslav.opencomicvine.model.moshi.ComicVineValuesListConverter
import java.util.*
import org.proninyaroslav.opencomicvine.data.Gender as GenderEnum

sealed class CharactersFilter(key: String, value: String) :
    ComicVineFilter(field = key, value = value) {

    sealed class Gender(value: String) : CharactersFilter(key = "gender", value = value) {
        object Other : Gender(GenderEnum.Other.value.toString())

        object Male : Gender(GenderEnum.Male.value.toString())

        object Female : Gender(GenderEnum.Female.value.toString())
    }

    data class Name(val nameValue: String) : CharactersFilter(key = "name", value = nameValue)

    /**
     * @param   start  the start date of the range.
     * @param   end    the end date of the range, inclusive.
     */
    data class DateAdded(val start: Date, val end: Date) : CharactersFilter(
        key = "date_added",
        value = ComicVineDateRangeConverter.toJson(start to end)!!
    )

    /**
     * @param   start  the start date of the range.
     * @param   end    the end date of the range, inclusive.
     */
    data class DateLastUpdated(val start: Date, val end: Date) : CharactersFilter(
        key = "date_last_updated",
        value = ComicVineDateRangeConverter.toJson(start to end)!!
    )

    data class Id(val idList: List<Int>) :
        CharactersFilter(
            key = "id",
            value = ComicVineValuesListConverter { it.toInt() }.toJson(idList)!!
        )
}
