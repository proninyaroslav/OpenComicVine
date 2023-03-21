package org.proninyaroslav.opencomicvine.data.preferences

import com.squareup.moshi.JsonClass
import dev.zacsweers.moshix.sealed.annotations.TypeLabel

@JsonClass(generateAdapter = true, generator = "sealed:type")
sealed interface PrefFavoritesSort {
    val direction: PrefSortDirection

    @TypeLabel("unknown")
    object Unknown : PrefFavoritesSort {
        override val direction: PrefSortDirection
            get() = PrefSortDirection.Unknown
    }

    @TypeLabel("date_added")
    @JsonClass(generateAdapter = true)
    data class DateAdded(
        override val direction: PrefSortDirection
    ) : PrefFavoritesSort
}