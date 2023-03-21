package org.proninyaroslav.opencomicvine.data.sort

sealed class PeopleSort(field: String, direction: ComicVineSortDirection) :
    ComicVineSort(field = field, direction = direction)
