package org.proninyaroslav.opencomicvine.model.db.favorites

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesIssueItem

@Dao
interface FavoritesIssuesDao {
    @Query("SELECT * FROM PagingFavoritesIssueItem ORDER BY `index` ASC")
    fun getAll(): PagingSource<Int, PagingFavoritesIssueItem>

    @Query("SELECT * FROM PagingFavoritesIssueItem ORDER BY `index` ASC LIMIT :count")
    fun get(count: Int): PagingSource<Int, PagingFavoritesIssueItem>

    @Query("SELECT * FROM PagingFavoritesIssueItem WHERE `item_info_id` = :id")
    fun getById(id: Int): PagingFavoritesIssueItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(issues: List<PagingFavoritesIssueItem>)

    @Query("DELETE FROM PagingFavoritesIssueItem")
    suspend fun deleteAll()

    @Query("DELETE FROM PagingFavoritesIssueItem WHERE `item_info_id` IN (:idList)")
    suspend fun deleteList(idList: List<Int>)
}