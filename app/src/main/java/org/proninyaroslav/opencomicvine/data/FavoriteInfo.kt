/*
 * Copyright (C) 2023 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of OpenComicVine.
 *
 * OpenComicVine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenComicVine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenComicVine.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.proninyaroslav.opencomicvine.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.proninyaroslav.opencomicvine.model.db.converter.DateConverter
import java.util.*

@Entity(indices = [Index(value = ["entityId", "entityType"], unique = true)])
@TypeConverters(
    DateConverter::class,
)
data class FavoriteInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val entityId: Int,

    val entityType: EntityType,

    val dateAdded: Date,
) {
    enum class EntityType {
        Character,
        Issue,
        Concept,
        Location,
        Movie,
        Object,
        Person,
        StoryArc,
        Team,
        Volume,
    }
}
