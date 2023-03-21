package org.proninyaroslav.opencomicvine.model.repo.paging.recent

import androidx.paging.PagingSource
import androidx.room.withTransaction
import org.proninyaroslav.opencomicvine.data.paging.recent.PagingRecentVolumeItem
import org.proninyaroslav.opencomicvine.data.paging.recent.RecentVolumeItemRemoteKeys
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import java.io.IOException
import javax.inject.Inject

interface PagingVolumeRepository :
    ComicVinePagingRepository<PagingRecentVolumeItem, RecentVolumeItemRemoteKeys>

class PagingVolumeRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
) : PagingVolumeRepository {

    private val volumesDao = appDatabase.recentVolumesDao()
    private val volumesRemoteKeysDao = appDatabase.recentVolumesRemoteKeysDao()

    override suspend fun getRemoteKeysById(id: Int): ComicVinePagingRepository.Result<RecentVolumeItemRemoteKeys?> {
        return try {
            ComicVinePagingRepository.Result.Success(volumesRemoteKeysDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun getItemById(id: Int): ComicVinePagingRepository.Result<PagingRecentVolumeItem?> {
        return try {
            ComicVinePagingRepository.Result.Success(volumesDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override fun getAllSavedItems(): PagingSource<Int, PagingRecentVolumeItem> =
        volumesDao.getAll()

    override fun getSavedItems(count: Int): PagingSource<Int, PagingRecentVolumeItem> =
        volumesDao.get(count)

    override suspend fun saveItems(
        items: List<PagingRecentVolumeItem>,
        remoteKeys: List<RecentVolumeItemRemoteKeys>,
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