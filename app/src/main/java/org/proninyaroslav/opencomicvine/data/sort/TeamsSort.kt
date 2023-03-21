package org.proninyaroslav.opencomicvine.data.sort

sealed class TeamsSort(field: String, direction: ComicVineSortDirection) :
    ComicVineSort(field = field, direction = direction)
