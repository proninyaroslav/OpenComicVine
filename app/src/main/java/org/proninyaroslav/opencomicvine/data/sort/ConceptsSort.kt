package org.proninyaroslav.opencomicvine.data.sort

sealed class ConceptsSort(field: String, direction: ComicVineSortDirection) :
    ComicVineSort(field = field, direction = direction)
