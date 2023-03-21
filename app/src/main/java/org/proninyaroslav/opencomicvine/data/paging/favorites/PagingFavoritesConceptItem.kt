package org.proninyaroslav.opencomicvine.data.paging.favorites

import androidx.compose.runtime.Immutable
import androidx.room.*
import org.proninyaroslav.opencomicvine.data.item.favorites.FavoritesConceptItem
import org.proninyaroslav.opencomicvine.data.paging.ComicVinePagingItem
import org.proninyaroslav.opencomicvine.model.db.converter.DateConverter

@Entity
@TypeConverters(
    DateConverter::class,
)
@Immutable
data class PagingFavoritesConceptItem(
    @PrimaryKey
    override val index: Int,

    @Embedded(prefix = "item_")
    val item: FavoritesConceptItem,
) : ComicVinePagingItem