package org.proninyaroslav.opencomicvine.model.db.favorites

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesCharacterItem

@Dao
interface FavoritesCharactersDao {
    @Query("SELECT * FROM PagingFavoritesCharacterItem ORDER BY `index` ASC")
    fun getAll(): PagingSource<Int, PagingFavoritesCharacterItem>

    @Query("SELECT * FROM PagingFavoritesCharacterItem ORDER BY `index` ASC LIMIT :count")
    fun get(count: Int): PagingSource<Int, PagingFavoritesCharacterItem>

    @Query("SELECT * FROM PagingFavoritesCharacterItem WHERE `item_info_id` = :id")
    fun getById(id: Int): PagingFavoritesCharacterItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(characters: List<PagingFavoritesCharacterItem>)

    @Query("DELETE FROM PagingFavoritesCharacterItem")
    suspend fun deleteAll()

    @Query("DELETE FROM PagingFavoritesCharacterItem WHERE `item_info_id` IN (:idList)")
    suspend fun deleteList(idList: List<Int>)
}