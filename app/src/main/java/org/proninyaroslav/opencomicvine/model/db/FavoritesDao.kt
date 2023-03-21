package org.proninyaroslav.opencomicvine.model.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import org.proninyaroslav.opencomicvine.data.FavoriteInfo

@Dao
interface FavoritesDao {
    @Query("SELECT * FROM FavoriteInfo WHERE `entityId` = :entityId AND `entityType` = :entityType")
    fun get(entityId: Int, entityType: String): FavoriteInfo?

    @Query("SELECT * FROM FavoriteInfo WHERE `entityId` = :entityId AND `entityType` = :entityType")
    fun observe(entityId: Int, entityType: String): Flow<FavoriteInfo?>

    fun observeDistinctUntilChanged(entityId: Int, entityType: String) =
        observe(entityId, entityType).distinctUntilChanged()

    @Query("SELECT * FROM FavoriteInfo WHERE `entityType` = :entityType ORDER BY `dateAdded` ASC")
    fun observeByTypeAsc(entityType: String): Flow<List<FavoriteInfo>>

    @Query("SELECT * FROM FavoriteInfo WHERE `entityType` = :entityType ORDER BY `dateAdded` DESC")
    fun observeByTypeDesc(entityType: String): Flow<List<FavoriteInfo>>

    fun observeByTypeDistinctUntilChanged(entityType: String, isAsc: Boolean) =
        if (isAsc) {
            observeByTypeAsc(entityType)
        } else {
            observeByTypeDesc(entityType)
        }.distinctUntilChanged()

    @Delete
    suspend fun delete(item: FavoriteInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: FavoriteInfo)
}