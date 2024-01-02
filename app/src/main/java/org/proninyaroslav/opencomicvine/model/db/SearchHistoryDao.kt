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
import org.proninyaroslav.opencomicvine.types.SearchHistoryInfo

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM SearchHistoryInfo WHERE `query` = :query")
    suspend fun getByQuery(query: String): SearchHistoryInfo?

    @Query("SELECT * FROM SearchHistoryInfo ORDER BY `date` DESC")
    fun observeAll(): Flow<List<SearchHistoryInfo>>

    @Delete
    suspend fun delete(item: SearchHistoryInfo)

    @Delete
    suspend fun deleteList(items: List<SearchHistoryInfo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SearchHistoryInfo)
}
