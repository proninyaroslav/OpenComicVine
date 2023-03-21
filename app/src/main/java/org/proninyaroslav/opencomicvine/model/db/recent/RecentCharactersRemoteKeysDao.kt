package org.proninyaroslav.opencomicvine.model.db.recent

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.recent.RecentCharacterItemRemoteKeys

@Dao
interface RecentCharactersRemoteKeysDao {
    @Query("SELECT * FROM FavoritesCharacterItemRemoteKeys where id = :id")
    fun getById(id: Int): RecentCharacterItemRemoteKeys

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(remoteKeys: List<RecentCharacterItemRemoteKeys>)

    @Query("DELETE FROM FavoritesCharacterItemRemoteKeys")
    suspend fun deleteAll()
}