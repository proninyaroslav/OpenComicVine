package org.proninyaroslav.opencomicvine.model.db.wiki

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.wiki.WikiIssueItemRemoteKeys

@Dao
interface WikiIssuesRemoteKeysDao {
    @Query("SELECT * FROM WikiIssueItemRemoteKeys where id = :id")
    fun getById(id: Int): WikiIssueItemRemoteKeys

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(remoteKeys: List<WikiIssueItemRemoteKeys>)

    @Query("DELETE FROM WikiIssueItemRemoteKeys")
    suspend fun deleteAll()
}