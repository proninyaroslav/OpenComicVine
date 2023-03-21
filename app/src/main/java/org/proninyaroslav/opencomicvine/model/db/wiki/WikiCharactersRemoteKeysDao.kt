package org.proninyaroslav.opencomicvine.model.db.wiki

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.wiki.WikiCharacterItemRemoteKeys

@Dao
interface WikiCharactersRemoteKeysDao {
    @Query("SELECT * FROM WikiCharacterItemRemoteKeys where id = :id")
    fun getById(id: Int): WikiCharacterItemRemoteKeys

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(remoteKeys: List<WikiCharacterItemRemoteKeys>)

    @Query("DELETE FROM WikiCharacterItemRemoteKeys")
    suspend fun deleteAll()
}