package org.proninyaroslav.opencomicvine.model.db.recent

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.recent.RecentIssueItemRemoteKeys

@Dao
interface RecentIssuesRemoteKeysDao {
    @Query("SELECT * FROM RecentIssueItemRemoteKeys where id = :id")
    fun getById(id: Int): RecentIssueItemRemoteKeys

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(remoteKeys: List<RecentIssueItemRemoteKeys>)

    @Query("DELETE FROM RecentIssueItemRemoteKeys")
    suspend fun deleteAll()
}