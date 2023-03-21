package org.proninyaroslav.opencomicvine.data.item.favorites

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import org.proninyaroslav.opencomicvine.data.CharacterInfo
import java.util.*

@Immutable
data class FavoritesCharacterItem(
    @Embedded(prefix = "info_")
    val info: CharacterInfo,
    override val dateAdded: Date,
) : FavoritesItem {
    override val id: Int
        get() = info.id
}
