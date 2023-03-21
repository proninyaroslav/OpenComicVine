package org.proninyaroslav.opencomicvine.model.paging.favorites

import org.proninyaroslav.opencomicvine.data.item.favorites.FavoritesItem
import org.proninyaroslav.opencomicvine.data.preferences.PrefFavoritesSort
import org.proninyaroslav.opencomicvine.data.preferences.PrefSortDirection

fun <T : FavoritesItem> List<T>.sort(sort: PrefFavoritesSort): List<T> {
    return when (sort) {
        PrefFavoritesSort.Unknown -> this
        is PrefFavoritesSort.DateAdded -> when (sort.direction) {
            PrefSortDirection.Unknown -> this
            PrefSortDirection.Asc -> sortedBy { it.dateAdded }
            PrefSortDirection.Desc -> sortedByDescending { it.dateAdded }
        }
    }
}
