package org.proninyaroslav.opencomicvine.model.db.favorites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesObjectItemRemoteKeys

@Dao
interface FavoritesObjectsRemoteKeysDao {
    @Query("SELECT * FROM FavoritesObjectItemRemoteKeys where id = :id")
    fun getById(id: Int): FavoritesObjectItemRemoteKeys

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(remoteKeys: List<FavoritesObjectItemRemoteKeys>)

    @Query("DELETE FROM FavoritesObjectItemRemoteKeys")
    suspend fun deleteAll()
}