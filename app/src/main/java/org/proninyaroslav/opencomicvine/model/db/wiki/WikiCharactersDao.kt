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

package org.proninyaroslav.opencomicvine.model.db.wiki

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.types.paging.wiki.PagingWikiCharacterItem

@Dao
interface WikiCharactersDao {
    @Query("SELECT * FROM PagingWikiCharacterItem ORDER BY `index` ASC")
    fun getAll(): PagingSource<Int, PagingWikiCharacterItem>

    @Query("SELECT * FROM PagingWikiCharacterItem ORDER BY `index` ASC LIMIT :count")
    fun get(count: Int): PagingSource<Int, PagingWikiCharacterItem>

    @Query("SELECT * FROM PagingWikiCharacterItem where `info_id` = :id")
    fun getById(id: Int): PagingWikiCharacterItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(characters: List<PagingWikiCharacterItem>)

    @Query("DELETE FROM PagingWikiCharacterItem")
    suspend fun deleteAll()
}
