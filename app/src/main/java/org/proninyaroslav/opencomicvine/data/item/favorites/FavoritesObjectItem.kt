package org.proninyaroslav.opencomicvine.data.item.favorites

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import org.proninyaroslav.opencomicvine.data.ObjectInfo
import java.util.*

@Immutable
data class FavoritesObjectItem(
    @Embedded(prefix = "info_")
    val info: ObjectInfo,
    override val dateAdded: Date,
) : FavoritesItem {
    override val id: Int
        get() = info.id
}
