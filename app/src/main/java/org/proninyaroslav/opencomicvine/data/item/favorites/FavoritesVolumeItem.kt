package org.proninyaroslav.opencomicvine.data.item.favorites

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import org.proninyaroslav.opencomicvine.data.VolumeInfo
import java.util.*

@Immutable
data class FavoritesVolumeItem(
    @Embedded(prefix = "info_")
    val info: VolumeInfo,
    override val dateAdded: Date,
) : FavoritesItem {
    override val id: Int
        get() = info.id
}
