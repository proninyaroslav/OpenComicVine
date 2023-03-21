package org.proninyaroslav.opencomicvine.data.preferences

import com.squareup.moshi.JsonClass
import dev.zacsweers.moshix.sealed.annotations.TypeLabel
import org.proninyaroslav.opencomicvine.data.sort.ComicVineSortDirection
import org.proninyaroslav.opencomicvine.data.sort.IssuesSort

@JsonClass(generateAdapter = true, generator = "sealed:type")
sealed interface PrefVolumeIssuesSort {
    val direction: PrefSortDirection

    @TypeLabel("unknown")
    object Unknown : PrefVolumeIssuesSort {
        override val direction: PrefSortDirection
            get() = PrefSortDirection.Unknown
    }

    @TypeLabel("store_date")
    @JsonClass(generateAdapter = true)
    data class StoreDate(
        override val direction: PrefSortDirection
    ) : PrefVolumeIssuesSort
}

fun PrefVolumeIssuesSort.toComicVineSort(): IssuesSort? = when (this) {
    PrefVolumeIssuesSort.Unknown -> null
    is PrefVolumeIssuesSort.StoreDate -> when (direction) {
        PrefSortDirection.Unknown -> null
        PrefSortDirection.Asc -> IssuesSort.StoreDate(ComicVineSortDirection.Asc)
        PrefSortDirection.Desc -> IssuesSort.StoreDate(ComicVineSortDirection.Desc)
    }
}