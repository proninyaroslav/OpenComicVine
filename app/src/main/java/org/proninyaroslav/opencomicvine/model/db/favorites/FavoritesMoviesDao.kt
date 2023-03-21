package org.proninyaroslav.opencomicvine.model.db.favorites

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesMovieItem

@Dao
interface FavoritesMoviesDao {
    @Query("SELECT * FROM PagingFavoritesMovieItem ORDER BY `index` ASC")
    fun getAll(): PagingSource<Int, PagingFavoritesMovieItem>

    @Query("SELECT * FROM PagingFavoritesMovieItem ORDER BY `index` ASC LIMIT :count")
    fun get(count: Int): PagingSource<Int, PagingFavoritesMovieItem>

    @Query("SELECT * FROM PagingFavoritesMovieItem WHERE `item_info_id` = :id")
    fun getById(id: Int): PagingFavoritesMovieItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(movies: List<PagingFavoritesMovieItem>)

    @Query("DELETE FROM PagingFavoritesMovieItem")
    suspend fun deleteAll()

    @Query("DELETE FROM PagingFavoritesMovieItem WHERE `item_info_id` IN (:idList)")
    suspend fun deleteList(idList: List<Int>)
}