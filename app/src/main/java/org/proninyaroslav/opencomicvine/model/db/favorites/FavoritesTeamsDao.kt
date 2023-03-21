package org.proninyaroslav.opencomicvine.model.db.favorites

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesTeamItem

@Dao
interface FavoritesTeamsDao {
    @Query("SELECT * FROM PagingFavoritesTeamItem ORDER BY `index` ASC")
    fun getAll(): PagingSource<Int, PagingFavoritesTeamItem>

    @Query("SELECT * FROM PagingFavoritesTeamItem ORDER BY `index` ASC LIMIT :count")
    fun get(count: Int): PagingSource<Int, PagingFavoritesTeamItem>

    @Query("SELECT * FROM PagingFavoritesTeamItem WHERE `item_info_id` = :id")
    fun getById(id: Int): PagingFavoritesTeamItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(teams: List<PagingFavoritesTeamItem>)

    @Query("DELETE FROM PagingFavoritesTeamItem")
    suspend fun deleteAll()

    @Query("DELETE FROM PagingFavoritesTeamItem WHERE `item_info_id` IN (:idList)")
    suspend fun deleteList(idList: List<Int>)
}