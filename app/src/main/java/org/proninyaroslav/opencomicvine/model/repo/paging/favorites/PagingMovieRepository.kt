package org.proninyaroslav.opencomicvine.model.repo.paging.favorites

import androidx.paging.PagingSource
import androidx.room.withTransaction
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesMovieItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesMovieItem
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import java.io.IOException
import javax.inject.Inject

interface PagingMovieRepository :
    FavoritesPagingRepository<PagingFavoritesMovieItem, FavoritesMovieItemRemoteKeys>

class PagingMovieRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
) : PagingMovieRepository {

    private val moviesDao = appDatabase.favoritesMoviesDao()
    private val moviesRemoteKeysDao = appDatabase.favoritesMoviesRemoteKeysDao()

    override suspend fun getRemoteKeysById(id: Int): ComicVinePagingRepository.Result<FavoritesMovieItemRemoteKeys?> {
        return try {
            ComicVinePagingRepository.Result.Success(moviesRemoteKeysDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun getItemById(id: Int): ComicVinePagingRepository.Result<PagingFavoritesMovieItem?> {
        return try {
            ComicVinePagingRepository.Result.Success(moviesDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override fun getAllSavedItems(): PagingSource<Int, PagingFavoritesMovieItem> =
        moviesDao.getAll()

    override fun getSavedItems(count: Int): PagingSource<Int, PagingFavoritesMovieItem> =
        moviesDao.get(count)

    override suspend fun deleteByIdList(idList: List<Int>): ComicVinePagingRepository.Result<Unit> {
        return try {
            moviesDao.deleteList(idList)
            ComicVinePagingRepository.Result.Success(Unit)
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun saveItems(
        items: List<PagingFavoritesMovieItem>,
        remoteKeys: List<FavoritesMovieItemRemoteKeys>,
        clearBeforeSave: Boolean,
    ): ComicVinePagingRepository.Result<Unit> = try {
        appDatabase.withTransaction {
            if (clearBeforeSave) {
                moviesDao.deleteAll()
                moviesRemoteKeysDao.deleteAll()
            }
            moviesRemoteKeysDao.insertList(remoteKeys)
            moviesDao.insertList(items)
        }
        ComicVinePagingRepository.Result.Success(Unit)
    } catch (e: IOException) {
        ComicVinePagingRepository.Result.Failed.IO(e)
    }
}