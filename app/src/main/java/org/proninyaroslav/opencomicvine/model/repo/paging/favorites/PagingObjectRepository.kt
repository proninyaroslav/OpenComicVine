package org.proninyaroslav.opencomicvine.model.repo.paging.favorites

import androidx.paging.PagingSource
import androidx.room.withTransaction
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesObjectItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesObjectItem
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import java.io.IOException
import javax.inject.Inject

interface PagingObjectRepository :
    FavoritesPagingRepository<PagingFavoritesObjectItem, FavoritesObjectItemRemoteKeys>

class PagingObjectRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
) : PagingObjectRepository {

    private val objectsDao = appDatabase.favoritesObjectsDao()
    private val objectsRemoteKeysDao = appDatabase.favoritesObjectsRemoteKeysDao()

    override suspend fun getRemoteKeysById(id: Int): ComicVinePagingRepository.Result<FavoritesObjectItemRemoteKeys?> {
        return try {
            ComicVinePagingRepository.Result.Success(objectsRemoteKeysDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun getItemById(id: Int): ComicVinePagingRepository.Result<PagingFavoritesObjectItem?> {
        return try {
            ComicVinePagingRepository.Result.Success(objectsDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override fun getAllSavedItems(): PagingSource<Int, PagingFavoritesObjectItem> =
        objectsDao.getAll()

    override fun getSavedItems(count: Int): PagingSource<Int, PagingFavoritesObjectItem> =
        objectsDao.get(count)

    override suspend fun deleteByIdList(idList: List<Int>): ComicVinePagingRepository.Result<Unit> {
        return try {
            objectsDao.deleteList(idList)
            ComicVinePagingRepository.Result.Success(Unit)
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun saveItems(
        items: List<PagingFavoritesObjectItem>,
        remoteKeys: List<FavoritesObjectItemRemoteKeys>,
        clearBeforeSave: Boolean,
    ): ComicVinePagingRepository.Result<Unit> = try {
        appDatabase.withTransaction {
            if (clearBeforeSave) {
                objectsDao.deleteAll()
                objectsRemoteKeysDao.deleteAll()
            }
            objectsRemoteKeysDao.insertList(remoteKeys)
            objectsDao.insertList(items)
        }
        ComicVinePagingRepository.Result.Success(Unit)
    } catch (e: IOException) {
        ComicVinePagingRepository.Result.Failed.IO(e)
    }
}