package org.proninyaroslav.opencomicvine.model.db.favorites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesLocationItemRemoteKeys

@Dao
interface FavoritesLocationsRemoteKeysDao {
    @Query("SELECT * FROM FavoritesLocationItemRemoteKeys where id = :id")
    fun getById(id: Int): FavoritesLocationItemRemoteKeys

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(remoteKeys: List<FavoritesLocationItemRemoteKeys>)

    @Query("DELETE FROM FavoritesLocationItemRemoteKeys")
    suspend fun deleteAll()
}