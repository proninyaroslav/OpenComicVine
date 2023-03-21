package org.proninyaroslav.opencomicvine.data.filter

import org.proninyaroslav.opencomicvine.model.moshi.ComicVineValuesListConverter

sealed class ConceptsFilter(key: String, value: String) :
    ComicVineFilter(field = key, value = value) {

    data class Id(val idList: List<Int>) :
        ConceptsFilter(
            key = "id",
            value = ComicVineValuesListConverter { it.toInt() }.toJson(idList)!!
        )
}
