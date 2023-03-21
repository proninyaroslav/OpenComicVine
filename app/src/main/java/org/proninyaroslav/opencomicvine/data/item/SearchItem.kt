package org.proninyaroslav.opencomicvine.data.item

import kotlinx.coroutines.flow.Flow
import org.proninyaroslav.opencomicvine.data.SearchInfo
import org.proninyaroslav.opencomicvine.model.repo.FavoriteFetchResult

class SearchItem(
    override val id: Int,
    val info: SearchInfo,
    override val isFavorite: Flow<FavoriteFetchResult>,
) : BaseItem {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SearchItem

        if (info != other.info) return false

        return true
    }

    override fun hashCode(): Int = info.hashCode()

    override fun toString(): String =
        "SearchItem(info=$info, isFavorite=$isFavorite, id=$id)"
}
