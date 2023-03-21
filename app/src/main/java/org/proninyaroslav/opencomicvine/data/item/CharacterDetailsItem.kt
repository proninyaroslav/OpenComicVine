package org.proninyaroslav.opencomicvine.data.item

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.Flow
import org.proninyaroslav.opencomicvine.data.CharacterDetails
import org.proninyaroslav.opencomicvine.model.repo.FavoriteFetchResult

@Immutable
class CharacterDetailsItem(
    val details: CharacterDetails,
    override val isFavorite: Flow<FavoriteFetchResult>,
) : BaseItem {
    override val id: Int
        get() = details.id

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CharacterDetailsItem

        if (details != other.details) return false

        return true
    }

    override fun hashCode(): Int = details.hashCode()

    override fun toString(): String =
        "CharacterDetailsItem(details=$details, isFavorite=$isFavorite, id=$id)"
}
