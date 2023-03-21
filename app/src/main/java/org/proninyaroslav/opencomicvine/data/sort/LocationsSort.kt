package org.proninyaroslav.opencomicvine.data.sort

sealed class LocationsSort(field: String, direction: ComicVineSortDirection) :
    ComicVineSort(field = field, direction = direction)
