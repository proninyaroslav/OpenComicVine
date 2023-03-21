package org.proninyaroslav.opencomicvine.model.db.recent

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.recent.PagingRecentIssueItem

@Dao
interface RecentIssuesDao {
    @Query("SELECT * FROM PagingRecentIssueItem ORDER BY `index` ASC")
    fun getAll(): PagingSource<Int, PagingRecentIssueItem>

    @Query("SELECT * FROM PagingRecentIssueItem ORDER BY `index` ASC LIMIT :count")
    fun get(count: Int): PagingSource<Int, PagingRecentIssueItem>

    @Query("SELECT * FROM PagingRecentIssueItem where `info_id` = :id")
    fun getById(id: Int): PagingRecentIssueItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(issues: List<PagingRecentIssueItem>)

    @Query("DELETE FROM PagingRecentIssueItem")
    suspend fun deleteAll()
}