package org.proninyaroslav.opencomicvine.data.item.favorites

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import org.proninyaroslav.opencomicvine.data.StoryArcInfo
import java.util.*

@Immutable
data class FavoritesStoryArcItem(
    @Embedded(prefix = "info_")
    val info: StoryArcInfo,
    override val dateAdded: Date,
) : FavoritesItem {
    override val id: Int
        get() = info.id
}
