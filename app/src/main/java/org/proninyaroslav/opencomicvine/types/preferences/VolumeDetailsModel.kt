/*
 * Copyright (C) 2023 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of OpenComicVine.
 *
 * OpenComicVine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenComicVine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenComicVine.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.proninyaroslav.opencomicvine.types.preferences

import com.squareup.moshi.JsonClass
import dev.zacsweers.moshix.sealed.annotations.TypeLabel
import org.proninyaroslav.opencomicvine.types.sort.ComicVineSortDirection
import org.proninyaroslav.opencomicvine.types.sort.IssuesSort

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
