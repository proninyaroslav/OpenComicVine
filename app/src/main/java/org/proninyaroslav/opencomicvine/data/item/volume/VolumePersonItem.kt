package org.proninyaroslav.opencomicvine.data.item.volume

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.Flow
import org.proninyaroslav.opencomicvine.data.PersonInfo
import org.proninyaroslav.opencomicvine.model.repo.FavoriteFetchResult

@Immutable
class VolumePersonItem(
    val info: PersonInfo,
    override val countOfAppearances: Int,
    override val isFavorite: Flow<FavoriteFetchResult>,
) : VolumeItem {
    override val id: Int
        get() = info.id

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VolumePersonItem

        if (info != other.info) return false
        if (countOfAppearances != other.countOfAppearances) return false

        return true
    }

    override fun hashCode(): Int {
        var result = info.hashCode()
        result = 31 * result + countOfAppearances
        return result
    }

    override fun toString(): String =
        "VolumePersonItem(info=$info, countOfAppearances=$countOfAppearances, isFavorite=$isFavorite, id=$id)"
}