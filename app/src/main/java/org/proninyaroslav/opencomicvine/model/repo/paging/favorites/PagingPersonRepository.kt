package org.proninyaroslav.opencomicvine.model.repo.paging.favorites

import androidx.paging.PagingSource
import androidx.room.withTransaction
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesPersonItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesPersonItem
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import java.io.IOException
import javax.inject.Inject

interface PagingPersonRepository :
    FavoritesPagingRepository<PagingFavoritesPersonItem, FavoritesPersonItemRemoteKeys>

class PagingPersonRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
) : PagingPersonRepository {

    private val peopleDao = appDatabase.favoritesPeopleDao()
    private val peopleRemoteKeysDao = appDatabase.favoritesPeopleRemoteKeysDao()

    override suspend fun getRemoteKeysById(id: Int): ComicVinePagingRepository.Result<FavoritesPersonItemRemoteKeys?> {
        return try {
            ComicVinePagingRepository.Result.Success(peopleRemoteKeysDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun getItemById(id: Int): ComicVinePagingRepository.Result<PagingFavoritesPersonItem?> {
        return try {
            ComicVinePagingRepository.Result.Success(peopleDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override fun getAllSavedItems(): PagingSource<Int, PagingFavoritesPersonItem> =
        peopleDao.getAll()

    override fun getSavedItems(count: Int): PagingSource<Int, PagingFavoritesPersonItem> =
        peopleDao.get(count)

    override suspend fun deleteByIdList(idList: List<Int>): ComicVinePagingRepository.Result<Unit> {
        return try {
            peopleDao.deleteList(idList)
            ComicVinePagingRepository.Result.Success(Unit)
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun saveItems(
        items: List<PagingFavoritesPersonItem>,
        remoteKeys: List<FavoritesPersonItemRemoteKeys>,
        clearBeforeSave: Boolean,
    ): ComicVinePagingRepository.Result<Unit> = try {
        appDatabase.withTransaction {
            if (clearBeforeSave) {
                peopleDao.deleteAll()
                peopleRemoteKeysDao.deleteAll()
            }
            peopleRemoteKeysDao.insertList(remoteKeys)
            peopleDao.insertList(items)
        }
        ComicVinePagingRepository.Result.Success(Unit)
    } catch (e: IOException) {
        ComicVinePagingRepository.Result.Failed.IO(e)
    }
}