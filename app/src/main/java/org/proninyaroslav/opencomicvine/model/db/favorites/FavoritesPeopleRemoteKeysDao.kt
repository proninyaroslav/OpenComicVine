package org.proninyaroslav.opencomicvine.model.db.favorites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesPersonItemRemoteKeys

@Dao
interface FavoritesPeopleRemoteKeysDao {
    @Query("SELECT * FROM FavoritesPersonItemRemoteKeys where id = :id")
    fun getById(id: Int): FavoritesPersonItemRemoteKeys

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(remoteKeys: List<FavoritesPersonItemRemoteKeys>)

    @Query("DELETE FROM FavoritesPersonItemRemoteKeys")
    suspend fun deleteAll()
}