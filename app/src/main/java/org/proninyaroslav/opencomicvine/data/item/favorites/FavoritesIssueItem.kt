package org.proninyaroslav.opencomicvine.data.item.favorites

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import org.proninyaroslav.opencomicvine.data.IssueInfo
import java.util.*

@Immutable
data class FavoritesIssueItem(
    @Embedded(prefix = "info_")
    val info: IssueInfo,
    override val dateAdded: Date,
) : FavoritesItem {
    override val id: Int
        get() = info.id
}
