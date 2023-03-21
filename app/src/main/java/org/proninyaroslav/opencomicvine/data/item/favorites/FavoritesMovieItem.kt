package org.proninyaroslav.opencomicvine.data.item.favorites

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import org.proninyaroslav.opencomicvine.data.MovieInfo
import java.util.*

@Immutable
data class FavoritesMovieItem(
    @Embedded(prefix = "info_")
    val info: MovieInfo,
    override val dateAdded: Date,
) : FavoritesItem {
    override val id: Int
        get() = info.id
}
