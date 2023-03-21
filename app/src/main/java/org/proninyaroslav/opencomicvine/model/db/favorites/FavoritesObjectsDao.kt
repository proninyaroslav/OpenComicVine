package org.proninyaroslav.opencomicvine.model.db.favorites

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesObjectItem

@Dao
interface FavoritesObjectsDao {
    @Query("SELECT * FROM PagingFavoritesObjectItem ORDER BY `index` ASC")
    fun getAll(): PagingSource<Int, PagingFavoritesObjectItem>

    @Query("SELECT * FROM PagingFavoritesObjectItem ORDER BY `index` ASC LIMIT :count")
    fun get(count: Int): PagingSource<Int, PagingFavoritesObjectItem>

    @Query("SELECT * FROM PagingFavoritesObjectItem WHERE `item_info_id` = :id")
    fun getById(id: Int): PagingFavoritesObjectItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(objects: List<PagingFavoritesObjectItem>)

    @Query("DELETE FROM PagingFavoritesObjectItem")
    suspend fun deleteAll()

    @Query("DELETE FROM PagingFavoritesObjectItem WHERE `item_info_id` IN (:idList)")
    suspend fun deleteList(idList: List<Int>)
}