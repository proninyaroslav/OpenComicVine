package org.proninyaroslav.opencomicvine.model.db.favorites

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesVolumeItem

@Dao
interface FavoritesVolumesDao {
    @Query("SELECT * FROM PagingFavoritesVolumeItem ORDER BY `index` ASC")
    fun getAll(): PagingSource<Int, PagingFavoritesVolumeItem>

    @Query("SELECT * FROM PagingFavoritesVolumeItem ORDER BY `index` ASC LIMIT :count")
    fun get(count: Int): PagingSource<Int, PagingFavoritesVolumeItem>

    @Query("SELECT * FROM PagingFavoritesVolumeItem WHERE `item_info_id` = :id")
    fun getById(id: Int): PagingFavoritesVolumeItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(volumes: List<PagingFavoritesVolumeItem>)

    @Query("DELETE FROM PagingFavoritesVolumeItem")
    suspend fun deleteAll()

    @Query("DELETE FROM PagingFavoritesVolumeItem WHERE `item_info_id` IN (:idList)")
    suspend fun deleteList(idList: List<Int>)
}