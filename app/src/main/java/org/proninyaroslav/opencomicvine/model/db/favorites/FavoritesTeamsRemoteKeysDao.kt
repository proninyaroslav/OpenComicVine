package org.proninyaroslav.opencomicvine.model.db.favorites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesTeamItemRemoteKeys

@Dao
interface FavoritesTeamsRemoteKeysDao {
    @Query("SELECT * FROM FavoritesTeamItemRemoteKeys where id = :id")
    fun getById(id: Int): FavoritesTeamItemRemoteKeys

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(remoteKeys: List<FavoritesTeamItemRemoteKeys>)

    @Query("DELETE FROM FavoritesTeamItemRemoteKeys")
    suspend fun deleteAll()
}