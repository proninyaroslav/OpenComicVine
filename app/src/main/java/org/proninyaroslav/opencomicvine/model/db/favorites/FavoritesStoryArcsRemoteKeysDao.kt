package org.proninyaroslav.opencomicvine.model.db.favorites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesStoryArcItemRemoteKeys

@Dao
interface FavoritesStoryArcsRemoteKeysDao {
    @Query("SELECT * FROM FavoritesStoryArcItemRemoteKeys where id = :id")
    fun getById(id: Int): FavoritesStoryArcItemRemoteKeys

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(remoteKeys: List<FavoritesStoryArcItemRemoteKeys>)

    @Query("DELETE FROM FavoritesStoryArcItemRemoteKeys")
    suspend fun deleteAll()
}