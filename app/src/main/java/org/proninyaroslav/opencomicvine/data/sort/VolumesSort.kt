package org.proninyaroslav.opencomicvine.data.sort

sealed class VolumesSort(field: String, direction: ComicVineSortDirection) :
    ComicVineSort(field = field, direction = direction) {

    data class Name(
        override val direction: ComicVineSortDirection
    ) : VolumesSort(
        field = "name",
        direction = direction,
    )

    data class DateLastUpdated(
        override val direction: ComicVineSortDirection
    ) : VolumesSort(
        field = "date_last_updated",
        direction = direction,
    )

    data class DateAdded(
        override val direction: ComicVineSortDirection
    ) : VolumesSort(
        field = "date_added",
        direction = direction,
    )
}
