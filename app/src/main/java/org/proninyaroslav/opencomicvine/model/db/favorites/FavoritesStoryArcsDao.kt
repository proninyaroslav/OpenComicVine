package org.proninyaroslav.opencomicvine.model.db.favorites

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesStoryArcItem

@Dao
interface FavoritesStoryArcsDao {
    @Query("SELECT * FROM PagingFavoritesStoryArcItem ORDER BY `index` ASC")
    fun getAll(): PagingSource<Int, PagingFavoritesStoryArcItem>

    @Query("SELECT * FROM PagingFavoritesStoryArcItem ORDER BY `index` ASC LIMIT :count")
    fun get(count: Int): PagingSource<Int, PagingFavoritesStoryArcItem>

    @Query("SELECT * FROM PagingFavoritesStoryArcItem WHERE `item_info_id` = :id")
    fun getById(id: Int): PagingFavoritesStoryArcItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(storyArcs: List<PagingFavoritesStoryArcItem>)

    @Query("DELETE FROM PagingFavoritesStoryArcItem")
    suspend fun deleteAll()

    @Query("DELETE FROM PagingFavoritesStoryArcItem WHERE `item_info_id` IN (:idList)")
    suspend fun deleteList(idList: List<Int>)
}