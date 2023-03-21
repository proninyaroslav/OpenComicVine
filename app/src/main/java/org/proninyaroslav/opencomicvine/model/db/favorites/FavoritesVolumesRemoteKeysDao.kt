package org.proninyaroslav.opencomicvine.model.db.favorites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesVolumeItemRemoteKeys

@Dao
interface FavoritesVolumesRemoteKeysDao {
    @Query("SELECT * FROM FavoritesVolumeItemRemoteKeys where id = :id")
    fun getById(id: Int): FavoritesVolumeItemRemoteKeys

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(remoteKeys: List<FavoritesVolumeItemRemoteKeys>)

    @Query("DELETE FROM FavoritesVolumeItemRemoteKeys")
    suspend fun deleteAll()
}