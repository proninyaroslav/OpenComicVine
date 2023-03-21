package org.proninyaroslav.opencomicvine.model.repo.paging.favorites

import androidx.paging.PagingSource
import androidx.room.withTransaction
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesCharacterItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesCharacterItem
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import java.io.IOException
import javax.inject.Inject

interface PagingCharacterRepository :
    FavoritesPagingRepository<PagingFavoritesCharacterItem, FavoritesCharacterItemRemoteKeys>

class PagingCharacterRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
) : PagingCharacterRepository {

    private val charactersDao = appDatabase.favoritesCharactersDao()
    private val charactersRemoteKeysDao = appDatabase.favoritesCharactersRemoteKeysDao()

    override suspend fun getRemoteKeysById(id: Int): ComicVinePagingRepository.Result<FavoritesCharacterItemRemoteKeys?> {
        return try {
            ComicVinePagingRepository.Result.Success(charactersRemoteKeysDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun getItemById(id: Int): ComicVinePagingRepository.Result<PagingFavoritesCharacterItem?> {
        return try {
            ComicVinePagingRepository.Result.Success(charactersDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override fun getAllSavedItems(): PagingSource<Int, PagingFavoritesCharacterItem> =
        charactersDao.getAll()

    override fun getSavedItems(count: Int): PagingSource<Int, PagingFavoritesCharacterItem> =
        charactersDao.get(count)

    override suspend fun deleteByIdList(idList: List<Int>): ComicVinePagingRepository.Result<Unit> {
        return try {
            charactersDao.deleteList(idList)
            ComicVinePagingRepository.Result.Success(Unit)
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun saveItems(
        items: List<PagingFavoritesCharacterItem>,
        remoteKeys: List<FavoritesCharacterItemRemoteKeys>,
        clearBeforeSave: Boolean,
    ): ComicVinePagingRepository.Result<Unit> = try {
        appDatabase.withTransaction {
            if (clearBeforeSave) {
                charactersDao.deleteAll()
                charactersRemoteKeysDao.deleteAll()
            }
            charactersRemoteKeysDao.insertList(remoteKeys)
            charactersDao.insertList(items)
        }
        ComicVinePagingRepository.Result.Success(Unit)
    } catch (e: IOException) {
        ComicVinePagingRepository.Result.Failed.IO(e)
    }
}