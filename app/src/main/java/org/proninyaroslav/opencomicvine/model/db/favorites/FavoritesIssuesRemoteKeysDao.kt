package org.proninyaroslav.opencomicvine.model.db.favorites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesIssueItemRemoteKeys

@Dao
interface FavoritesIssuesRemoteKeysDao {
    @Query("SELECT * FROM FavoritesIssueItemRemoteKeys where id = :id")
    fun getById(id: Int): FavoritesIssueItemRemoteKeys

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(remoteKeys: List<FavoritesIssueItemRemoteKeys>)

    @Query("DELETE FROM FavoritesIssueItemRemoteKeys")
    suspend fun deleteAll()
}