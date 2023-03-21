package org.proninyaroslav.opencomicvine.data.sort

sealed class StoryArcsSort(field: String, direction: ComicVineSortDirection) :
    ComicVineSort(field = field, direction = direction)
