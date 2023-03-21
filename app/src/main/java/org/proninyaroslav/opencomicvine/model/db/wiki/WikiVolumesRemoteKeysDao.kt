package org.proninyaroslav.opencomicvine.model.db.wiki

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.wiki.WikiVolumeItemRemoteKeys

@Dao
interface WikiVolumesRemoteKeysDao {
    @Query("SELECT * FROM WikiVolumeItemRemoteKeys where id = :id")
    fun getById(id: Int): WikiVolumeItemRemoteKeys

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(remoteKeys: List<WikiVolumeItemRemoteKeys>)

    @Query("DELETE FROM WikiVolumeItemRemoteKeys")
    suspend fun deleteAll()
}