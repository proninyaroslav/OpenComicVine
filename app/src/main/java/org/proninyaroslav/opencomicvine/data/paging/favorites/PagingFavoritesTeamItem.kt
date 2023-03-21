package org.proninyaroslav.opencomicvine.data.paging.favorites

import androidx.compose.runtime.Immutable
import androidx.room.*
import org.proninyaroslav.opencomicvine.data.item.favorites.FavoritesTeamItem
import org.proninyaroslav.opencomicvine.data.paging.ComicVinePagingItem
import org.proninyaroslav.opencomicvine.model.db.converter.DateConverter

@Entity
@TypeConverters(
    DateConverter::class,
)
@Immutable
data class PagingFavoritesTeamItem(
    @PrimaryKey
    override val index: Int,

    @Embedded(prefix = "item_")
    val item: FavoritesTeamItem,
) : ComicVinePagingItem