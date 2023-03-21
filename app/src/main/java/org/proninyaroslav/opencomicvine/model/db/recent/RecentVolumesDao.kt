package org.proninyaroslav.opencomicvine.model.db.recent

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.recent.PagingRecentVolumeItem

@Dao
interface RecentVolumesDao {
    @Query("SELECT * FROM PagingRecentVolumeItem ORDER BY `index` ASC")
    fun getAll(): PagingSource<Int, PagingRecentVolumeItem>

    @Query("SELECT * FROM PagingRecentVolumeItem ORDER BY `index` ASC LIMIT :count")
    fun get(count: Int): PagingSource<Int, PagingRecentVolumeItem>

    @Query("SELECT * FROM PagingRecentVolumeItem where `info_id` = :id")
    fun getById(id: Int): PagingRecentVolumeItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(issues: List<PagingRecentVolumeItem>)

    @Query("DELETE FROM PagingRecentVolumeItem")
    suspend fun deleteAll()
}