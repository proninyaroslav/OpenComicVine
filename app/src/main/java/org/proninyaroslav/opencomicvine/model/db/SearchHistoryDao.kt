package org.proninyaroslav.opencomicvine.model.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.proninyaroslav.opencomicvine.data.SearchHistoryInfo

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM SearchHistoryInfo WHERE `query` = :query")
    suspend fun getByQuery(query: String): SearchHistoryInfo?

    @Query("SELECT * FROM SearchHistoryInfo ORDER BY `date` DESC")
    fun observeAll(): Flow<List<SearchHistoryInfo>>

    @Delete
    suspend fun delete(item: SearchHistoryInfo)

    @Delete
    suspend fun deleteList(items: List<SearchHistoryInfo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SearchHistoryInfo)
}