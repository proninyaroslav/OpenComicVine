package org.proninyaroslav.opencomicvine.data.sort

sealed class IssuesSort(field: String, direction: ComicVineSortDirection) :
    ComicVineSort(field = field, direction = direction) {

    data class Name(
        override val direction: ComicVineSortDirection
    ) : IssuesSort(
        field = "name",
        direction = direction,
    )

    data class DateLastUpdated(
        override val direction: ComicVineSortDirection
    ) : IssuesSort(
        field = "date_last_updated",
        direction = direction,
    )

    data class DateAdded(
        override val direction: ComicVineSortDirection
    ) : IssuesSort(
        field = "date_added",
        direction = direction,
    )

    data class CoverDate(
        override val direction: ComicVineSortDirection
    ) : IssuesSort(
        field = "cover_date",
        direction = direction,
    )

    data class StoreDate(
        override val direction: ComicVineSortDirection
    ) : IssuesSort(
        field = "store_date",
        direction = direction,
    )
}
