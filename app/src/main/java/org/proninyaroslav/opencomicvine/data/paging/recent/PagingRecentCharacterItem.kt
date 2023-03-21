package org.proninyaroslav.opencomicvine.data.paging.recent

import androidx.compose.runtime.Immutable
import androidx.room.*
import org.proninyaroslav.opencomicvine.data.CharacterInfo
import org.proninyaroslav.opencomicvine.data.paging.ComicVinePagingItem
import org.proninyaroslav.opencomicvine.model.db.converter.DateConverter

@Entity
@TypeConverters(
    DateConverter::class,
)
@Immutable
data class PagingRecentCharacterItem(
    @PrimaryKey
    override val index: Int,

    @Embedded(prefix = "info_")
    val info: CharacterInfo,
) : ComicVinePagingItem