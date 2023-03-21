package org.proninyaroslav.opencomicvine.model.db.favorites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesConceptItemRemoteKeys

@Dao
interface FavoritesConceptsRemoteKeysDao {
    @Query("SELECT * FROM FavoritesConceptItemRemoteKeys where id = :id")
    fun getById(id: Int): FavoritesConceptItemRemoteKeys

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(remoteKeys: List<FavoritesConceptItemRemoteKeys>)

    @Query("DELETE FROM FavoritesConceptItemRemoteKeys")
    suspend fun deleteAll()
}