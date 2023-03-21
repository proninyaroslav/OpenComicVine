package org.proninyaroslav.opencomicvine.model.db.favorites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesCharacterItemRemoteKeys

@Dao
interface FavoritesCharactersRemoteKeysDao {
    @Query("SELECT * FROM FavoritesCharacterItemRemoteKeys where id = :id")
    fun getById(id: Int): FavoritesCharacterItemRemoteKeys

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(remoteKeys: List<FavoritesCharacterItemRemoteKeys>)

    @Query("DELETE FROM FavoritesCharacterItemRemoteKeys")
    suspend fun deleteAll()
}