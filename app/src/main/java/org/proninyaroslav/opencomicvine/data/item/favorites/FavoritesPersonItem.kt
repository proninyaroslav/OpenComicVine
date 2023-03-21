package org.proninyaroslav.opencomicvine.data.item.favorites

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import org.proninyaroslav.opencomicvine.data.PersonInfo
import java.util.*

@Immutable
data class FavoritesPersonItem(
    @Embedded(prefix = "info_")
    val info: PersonInfo,
    override val dateAdded: Date,
) : FavoritesItem {
    override val id: Int
        get() = info.id
}
