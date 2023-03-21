package org.proninyaroslav.opencomicvine.data.sort

sealed class ObjectsSort(field: String, direction: ComicVineSortDirection) :
    ComicVineSort(field = field, direction = direction)
