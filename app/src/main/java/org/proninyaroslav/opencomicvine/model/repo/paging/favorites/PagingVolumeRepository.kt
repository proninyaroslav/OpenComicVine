package org.proninyaroslav.opencomicvine.model.repo.paging.favorites

import androidx.paging.PagingSource
import androidx.room.withTransaction
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesVolumeItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesVolumeItem
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import java.io.IOException
import javax.inject.Inject

interface PagingVolumeRepository :
    FavoritesPagingRepository<PagingFavoritesVolumeItem, FavoritesVolumeItemRemoteKeys>

class PagingVolumeRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
) : PagingVolumeRepository {

    private val volumesDao = appDatabase.favoritesVolumesDao()
    private val volumesRemoteKeysDao = appDatabase.favoritesVolumesRemoteKeysDao()

    override suspend fun getRemoteKeysById(id: Int): ComicVinePagingRepository.Result<FavoritesVolumeItemRemoteKeys?> {
        return try {
            ComicVinePagingRepository.Result.Success(volumesRemoteKeysDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun getItemById(id: Int): ComicVinePagingRepository.Result<PagingFavoritesVolumeItem?> {
        return try {
            ComicVinePagingRepository.Result.Success(volumesDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override fun getAllSavedItems(): PagingSource<Int, PagingFavoritesVolumeItem> =
        volumesDao.getAll()

    override fun getSavedItems(count: Int): PagingSource<Int, PagingFavoritesVolumeItem> =
        volumesDao.get(count)

    override suspend fun deleteByIdList(idList: List<Int>): ComicVinePagingRepository.Result<Unit> {
        return try {
            volumesDao.deleteList(idList)
            ComicVinePagingRepository.Result.Success(Unit)
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun saveItems(
        items: List<PagingFavoritesVolumeItem>,
        remoteKeys: List<FavoritesVolumeItemRemoteKeys>,
        clearBeforeSave: Boolean,
    ): ComicVinePagingRepository.Result<Unit> = try {
        appDatabase.withTransaction {
            if (clearBeforeSave) {
                volumesDao.deleteAll()
                volumesRemoteKeysDao.deleteAll()
            }
            volumesRemoteKeysDao.insertList(remoteKeys)
            volumesDao.insertList(items)
        }
        ComicVinePagingRepository.Result.Success(Unit)
    } catch (e: IOException) {
        ComicVinePagingRepository.Result.Failed.IO(e)
    }
}