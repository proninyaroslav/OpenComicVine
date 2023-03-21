package org.proninyaroslav.opencomicvine.model.repo.paging.favorites

import androidx.paging.PagingSource
import androidx.room.withTransaction
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesIssueItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesIssueItem
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import java.io.IOException
import javax.inject.Inject

interface PagingIssueRepository :
    FavoritesPagingRepository<PagingFavoritesIssueItem, FavoritesIssueItemRemoteKeys>

class PagingIssueRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
) : PagingIssueRepository {

    private val issuesDao = appDatabase.favoritesIssuesDao()
    private val issuesRemoteKeysDao = appDatabase.favoritesIssuesRemoteKeysDao()

    override suspend fun getRemoteKeysById(id: Int): ComicVinePagingRepository.Result<FavoritesIssueItemRemoteKeys?> {
        return try {
            ComicVinePagingRepository.Result.Success(issuesRemoteKeysDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun getItemById(id: Int): ComicVinePagingRepository.Result<PagingFavoritesIssueItem?> {
        return try {
            ComicVinePagingRepository.Result.Success(issuesDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override fun getAllSavedItems(): PagingSource<Int, PagingFavoritesIssueItem> =
        issuesDao.getAll()

    override fun getSavedItems(count: Int): PagingSource<Int, PagingFavoritesIssueItem> =
        issuesDao.get(count)

    override suspend fun deleteByIdList(idList: List<Int>): ComicVinePagingRepository.Result<Unit> {
        return try {
            issuesDao.deleteList(idList)
            ComicVinePagingRepository.Result.Success(Unit)
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun saveItems(
        items: List<PagingFavoritesIssueItem>,
        remoteKeys: List<FavoritesIssueItemRemoteKeys>,
        clearBeforeSave: Boolean,
    ): ComicVinePagingRepository.Result<Unit> = try {
        appDatabase.withTransaction {
            if (clearBeforeSave) {
                issuesDao.deleteAll()
                issuesRemoteKeysDao.deleteAll()
            }
            issuesRemoteKeysDao.insertList(remoteKeys)
            issuesDao.insertList(items)
        }
        ComicVinePagingRepository.Result.Success(Unit)
    } catch (e: IOException) {
        ComicVinePagingRepository.Result.Failed.IO(e)
    }
}