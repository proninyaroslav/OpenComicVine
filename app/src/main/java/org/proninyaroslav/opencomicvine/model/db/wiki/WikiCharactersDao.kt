package org.proninyaroslav.opencomicvine.model.db.wiki

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.wiki.PagingWikiCharacterItem

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