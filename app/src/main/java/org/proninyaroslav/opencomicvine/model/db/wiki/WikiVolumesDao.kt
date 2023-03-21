package org.proninyaroslav.opencomicvine.model.db.wiki

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.wiki.PagingWikiVolumeItem

@Dao
interface WikiVolumesDao {
    @Query("SELECT * FROM PagingWikiVolumeItem ORDER BY `index` ASC")
    fun getAll(): PagingSource<Int, PagingWikiVolumeItem>

    @Query("SELECT * FROM PagingWikiVolumeItem ORDER BY `index` ASC LIMIT :count")
    fun get(count: Int): PagingSource<Int, PagingWikiVolumeItem>

    @Query("SELECT * FROM PagingWikiVolumeItem where `info_id` = :id")
    fun getById(id: Int): PagingWikiVolumeItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(issues: List<PagingWikiVolumeItem>)

    @Query("DELETE FROM PagingWikiVolumeItem")
    suspend fun deleteAll()
}