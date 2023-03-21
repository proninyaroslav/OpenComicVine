package org.proninyaroslav.opencomicvine.data.filter

import org.proninyaroslav.opencomicvine.model.moshi.ComicVineValuesListConverter

sealed class ObjectsFilter(key: String, value: String) :
    ComicVineFilter(field = key, value = value) {

    data class Id(val idList: List<Int>) :
        ObjectsFilter(
            key = "id",
            value = ComicVineValuesListConverter { it.toInt() }.toJson(idList)!!
        )

    data class Name(val nameValue: String) : ObjectsFilter(key = "name", value = nameValue)
}
