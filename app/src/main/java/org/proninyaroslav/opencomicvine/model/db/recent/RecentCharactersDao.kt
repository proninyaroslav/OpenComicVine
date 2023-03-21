package org.proninyaroslav.opencomicvine.model.db.recent

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.recent.PagingRecentCharacterItem

@Dao
interface RecentCharactersDao {
    @Query("SELECT * FROM PagingRecentCharacterItem ORDER BY `index` ASC")
    fun getAll(): PagingSource<Int, PagingRecentCharacterItem>

    @Query("SELECT * FROM PagingRecentCharacterItem ORDER BY `index` ASC LIMIT :count")
    fun get(count: Int): PagingSource<Int, PagingRecentCharacterItem>

    @Query("SELECT * FROM PagingRecentCharacterItem WHERE `info_id` = :id")
    fun getById(id: Int): PagingRecentCharacterItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(characters: List<PagingRecentCharacterItem>)

    @Query("DELETE FROM PagingRecentCharacterItem")
    suspend fun deleteAll()
}