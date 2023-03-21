package org.proninyaroslav.opencomicvine.model.db.favorites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesMovieItemRemoteKeys

@Dao
interface FavoritesMoviesRemoteKeysDao {
    @Query("SELECT * FROM FavoritesMovieItemRemoteKeys where id = :id")
    fun getById(id: Int): FavoritesMovieItemRemoteKeys

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(remoteKeys: List<FavoritesMovieItemRemoteKeys>)

    @Query("DELETE FROM FavoritesMovieItemRemoteKeys")
    suspend fun deleteAll()
}