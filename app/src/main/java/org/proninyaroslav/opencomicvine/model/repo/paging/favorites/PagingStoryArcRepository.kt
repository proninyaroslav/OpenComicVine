package org.proninyaroslav.opencomicvine.model.repo.paging.favorites

import androidx.paging.PagingSource
import androidx.room.withTransaction
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesStoryArcItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesStoryArcItem
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import java.io.IOException
import javax.inject.Inject

interface PagingStoryArcRepository :
    FavoritesPagingRepository<PagingFavoritesStoryArcItem, FavoritesStoryArcItemRemoteKeys>

class PagingStoryArcRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
) : PagingStoryArcRepository {

    private val storyArcsDao = appDatabase.favoritesStoryArcsDao()
    private val storyArcsRemoteKeysDao = appDatabase.favoritesStoryArcsRemoteKeysDao()

    override suspend fun getRemoteKeysById(id: Int): ComicVinePagingRepository.Result<FavoritesStoryArcItemRemoteKeys?> {
        return try {
            ComicVinePagingRepository.Result.Success(storyArcsRemoteKeysDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun getItemById(id: Int): ComicVinePagingRepository.Result<PagingFavoritesStoryArcItem?> {
        return try {
            ComicVinePagingRepository.Result.Success(storyArcsDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override fun getAllSavedItems(): PagingSource<Int, PagingFavoritesStoryArcItem> =
        storyArcsDao.getAll()

    override fun getSavedItems(count: Int): PagingSource<Int, PagingFavoritesStoryArcItem> =
        storyArcsDao.get(count)

    override suspend fun deleteByIdList(idList: List<Int>): ComicVinePagingRepository.Result<Unit> {
        return try {
            storyArcsDao.deleteList(idList)
            ComicVinePagingRepository.Result.Success(Unit)
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun saveItems(
        items: List<PagingFavoritesStoryArcItem>,
        remoteKeys: List<FavoritesStoryArcItemRemoteKeys>,
        clearBeforeSave: Boolean,
    ): ComicVinePagingRepository.Result<Unit> = try {
        appDatabase.withTransaction {
            if (clearBeforeSave) {
                storyArcsDao.deleteAll()
                storyArcsRemoteKeysDao.deleteAll()
            }
            storyArcsRemoteKeysDao.insertList(remoteKeys)
            storyArcsDao.insertList(items)
        }
        ComicVinePagingRepository.Result.Success(Unit)
    } catch (e: IOException) {
        ComicVinePagingRepository.Result.Failed.IO(e)
    }
}