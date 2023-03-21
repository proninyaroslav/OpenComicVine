package org.proninyaroslav.opencomicvine.data.item

import kotlinx.coroutines.flow.Flow
import org.proninyaroslav.opencomicvine.model.repo.FavoriteFetchResult

interface BaseItem {
    val id: Int
    val isFavorite: Flow<FavoriteFetchResult>
}