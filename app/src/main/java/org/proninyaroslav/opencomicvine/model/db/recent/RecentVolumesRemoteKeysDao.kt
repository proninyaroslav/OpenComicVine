package org.proninyaroslav.opencomicvine.model.db.recent

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.recent.RecentVolumeItemRemoteKeys

@Dao
interface RecentVolumesRemoteKeysDao {
    @Query("SELECT * FROM RecentVolumeItemRemoteKeys where id = :id")
    fun getById(id: Int): RecentVolumeItemRemoteKeys

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(remoteKeys: List<RecentVolumeItemRemoteKeys>)

    @Query("DELETE FROM RecentVolumeItemRemoteKeys")
    suspend fun deleteAll()
}