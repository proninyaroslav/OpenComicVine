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

package org.proninyaroslav.opencomicvine.model.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import org.proninyaroslav.opencomicvine.types.FavoriteInfo

@Dao
interface FavoritesDao {
    @Query("SELECT * FROM FavoriteInfo WHERE `entityId` = :entityId AND `entityType` = :entityType")
    fun get(entityId: Int, entityType: String): FavoriteInfo?

    @Query("SELECT * FROM FavoriteInfo WHERE `entityId` = :entityId AND `entityType` = :entityType")
    fun observe(entityId: Int, entityType: String): Flow<FavoriteInfo?>

    fun observeDistinctUntilChanged(entityId: Int, entityType: String) =
        observe(entityId, entityType).distinctUntilChanged()

    @Query("SELECT * FROM FavoriteInfo WHERE `entityType` = :entityType ORDER BY `dateAdded` ASC")
    fun observeByTypeAsc(entityType: String): Flow<List<FavoriteInfo>>

    @Query("SELECT * FROM FavoriteInfo WHERE `entityType` = :entityType ORDER BY `dateAdded` DESC")
    fun observeByTypeDesc(entityType: String): Flow<List<FavoriteInfo>>

    fun observeByTypeDistinctUntilChanged(entityType: String, isAsc: Boolean) =
        if (isAsc) {
            observeByTypeAsc(entityType)
        } else {
            observeByTypeDesc(entityType)
        }.distinctUntilChanged()

    @Delete
    suspend fun delete(item: FavoriteInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: FavoriteInfo)
}
