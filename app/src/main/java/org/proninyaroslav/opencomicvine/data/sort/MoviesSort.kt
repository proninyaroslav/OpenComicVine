package org.proninyaroslav.opencomicvine.data.sort

sealed class MoviesSort(field: String, direction: ComicVineSortDirection) :
    ComicVineSort(field = field, direction = direction)
