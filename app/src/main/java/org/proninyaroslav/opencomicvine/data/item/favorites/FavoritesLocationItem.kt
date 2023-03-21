package org.proninyaroslav.opencomicvine.data.item.favorites

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import org.proninyaroslav.opencomicvine.data.LocationInfo
import java.util.*

@Immutable
data class FavoritesLocationItem(
    @Embedded(prefix = "info_")
    val info: LocationInfo,
    override val dateAdded: Date,
) : FavoritesItem {
    override val id: Int
        get() = info.id
}
