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

package org.proninyaroslav.opencomicvine.model.db.favorites

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesIssueItem

@Dao
interface FavoritesIssuesDao {
    @Query("SELECT * FROM PagingFavoritesIssueItem ORDER BY `index` ASC")
    fun getAll(): PagingSource<Int, PagingFavoritesIssueItem>

    @Query("SELECT * FROM PagingFavoritesIssueItem ORDER BY `index` ASC LIMIT :count")
    fun get(count: Int): PagingSource<Int, PagingFavoritesIssueItem>

    @Query("SELECT * FROM PagingFavoritesIssueItem WHERE `item_info_id` = :id")
    fun getById(id: Int): PagingFavoritesIssueItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(issues: List<PagingFavoritesIssueItem>)

    @Query("DELETE FROM PagingFavoritesIssueItem")
    suspend fun deleteAll()

    @Query("DELETE FROM PagingFavoritesIssueItem WHERE `item_info_id` IN (:idList)")
    suspend fun deleteList(idList: List<Int>)
}
