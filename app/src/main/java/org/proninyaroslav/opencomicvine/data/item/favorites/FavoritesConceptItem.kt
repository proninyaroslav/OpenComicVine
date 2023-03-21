package org.proninyaroslav.opencomicvine.data.item.favorites

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import org.proninyaroslav.opencomicvine.data.ConceptInfo
import java.util.*

@Immutable
data class FavoritesConceptItem(
    @Embedded(prefix = "info_")
    val info: ConceptInfo,
    override val dateAdded: Date,
) : FavoritesItem {
    override val id: Int
        get() = info.id
}
