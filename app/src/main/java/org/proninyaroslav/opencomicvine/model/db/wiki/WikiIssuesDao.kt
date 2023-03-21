package org.proninyaroslav.opencomicvine.model.db.wiki

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.wiki.PagingWikiIssueItem

@Dao
interface WikiIssuesDao {
    @Query("SELECT * FROM PagingWikiIssueItem ORDER BY `index` ASC")
    fun getAll(): PagingSource<Int, PagingWikiIssueItem>

    @Query("SELECT * FROM PagingWikiIssueItem ORDER BY `index` ASC LIMIT :count")
    fun get(count: Int): PagingSource<Int, PagingWikiIssueItem>

    @Query("SELECT * FROM PagingWikiIssueItem where `info_id` = :id")
    fun getById(id: Int): PagingWikiIssueItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(issues: List<PagingWikiIssueItem>)

    @Query("DELETE FROM PagingWikiIssueItem")
    suspend fun deleteAll()
}