package org.proninyaroslav.opencomicvine.model.db.favorites

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesLocationItem

@Dao
interface FavoritesLocationsDao {
    @Query("SELECT * FROM PagingFavoritesLocationItem ORDER BY `index` ASC")
    fun getAll(): PagingSource<Int, PagingFavoritesLocationItem>

    @Query("SELECT * FROM PagingFavoritesLocationItem ORDER BY `index` ASC LIMIT :count")
    fun get(count: Int): PagingSource<Int, PagingFavoritesLocationItem>

    @Query("SELECT * FROM PagingFavoritesLocationItem WHERE `item_info_id` = :id")
    fun getById(id: Int): PagingFavoritesLocationItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(locations: List<PagingFavoritesLocationItem>)

    @Query("DELETE FROM PagingFavoritesLocationItem")
    suspend fun deleteAll()

    @Query("DELETE FROM PagingFavoritesLocationItem WHERE `item_info_id` IN (:idList)")
    suspend fun deleteList(idList: List<Int>)
}