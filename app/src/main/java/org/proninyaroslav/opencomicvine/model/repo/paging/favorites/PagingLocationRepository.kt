package org.proninyaroslav.opencomicvine.model.repo.paging.favorites

import androidx.paging.PagingSource
import androidx.room.withTransaction
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesLocationItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesLocationItem
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import java.io.IOException
import javax.inject.Inject

interface PagingLocationRepository :
    FavoritesPagingRepository<PagingFavoritesLocationItem, FavoritesLocationItemRemoteKeys>

class PagingLocationRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
) : PagingLocationRepository {

    private val locationsDao = appDatabase.favoritesLocationsDao()
    private val locationsRemoteKeysDao = appDatabase.favoritesLocationsRemoteKeysDao()

    override suspend fun getRemoteKeysById(id: Int): ComicVinePagingRepository.Result<FavoritesLocationItemRemoteKeys?> {
        return try {
            ComicVinePagingRepository.Result.Success(locationsRemoteKeysDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun getItemById(id: Int): ComicVinePagingRepository.Result<PagingFavoritesLocationItem?> {
        return try {
            ComicVinePagingRepository.Result.Success(locationsDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override fun getAllSavedItems(): PagingSource<Int, PagingFavoritesLocationItem> =
        locationsDao.getAll()

    override fun getSavedItems(count: Int): PagingSource<Int, PagingFavoritesLocationItem> =
        locationsDao.get(count)

    override suspend fun deleteByIdList(idList: List<Int>): ComicVinePagingRepository.Result<Unit> {
        return try {
            locationsDao.deleteList(idList)
            ComicVinePagingRepository.Result.Success(Unit)
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun saveItems(
        items: List<PagingFavoritesLocationItem>,
        remoteKeys: List<FavoritesLocationItemRemoteKeys>,
        clearBeforeSave: Boolean,
    ): ComicVinePagingRepository.Result<Unit> = try {
        appDatabase.withTransaction {
            if (clearBeforeSave) {
                locationsDao.deleteAll()
                locationsRemoteKeysDao.deleteAll()
            }
            locationsRemoteKeysDao.insertList(remoteKeys)
            locationsDao.insertList(items)
        }
        ComicVinePagingRepository.Result.Success(Unit)
    } catch (e: IOException) {
        ComicVinePagingRepository.Result.Failed.IO(e)
    }
}