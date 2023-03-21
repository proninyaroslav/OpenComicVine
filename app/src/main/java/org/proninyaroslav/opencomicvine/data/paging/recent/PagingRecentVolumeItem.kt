package org.proninyaroslav.opencomicvine.data.paging.recent

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.proninyaroslav.opencomicvine.data.VolumeInfo
import org.proninyaroslav.opencomicvine.data.paging.ComicVinePagingItem
import org.proninyaroslav.opencomicvine.model.db.converter.DateConverter

@Entity
@TypeConverters(
    DateConverter::class,
)
@Immutable
data class PagingRecentVolumeItem(
    @PrimaryKey
    override val index: Int,

    @Embedded(prefix = "info_")
    val info: VolumeInfo,
) : ComicVinePagingItem
