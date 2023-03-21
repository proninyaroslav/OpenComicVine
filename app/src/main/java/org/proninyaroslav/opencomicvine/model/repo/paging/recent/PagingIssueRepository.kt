package org.proninyaroslav.opencomicvine.model.repo.paging.recent

import androidx.paging.PagingSource
import androidx.room.withTransaction
import org.proninyaroslav.opencomicvine.data.paging.recent.PagingRecentIssueItem
import org.proninyaroslav.opencomicvine.data.paging.recent.RecentIssueItemRemoteKeys
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import java.io.IOException
import javax.inject.Inject

interface PagingIssueRepository :
    ComicVinePagingRepository<PagingRecentIssueItem, RecentIssueItemRemoteKeys>

class PagingIssueRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
) : PagingIssueRepository {

    private val issuesDao = appDatabase.recentIssuesDao()
    private val issuesRemoteKeysDao = appDatabase.recentIssuesRemoteKeysDao()

    override suspend fun getRemoteKeysById(id: Int): ComicVinePagingRepository.Result<RecentIssueItemRemoteKeys?> {
        return try {
            ComicVinePagingRepository.Result.Success(issuesRemoteKeysDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun getItemById(id: Int): ComicVinePagingRepository.Result<PagingRecentIssueItem?> {
        return try {
            ComicVinePagingRepository.Result.Success(issuesDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override fun getAllSavedItems(): PagingSource<Int, PagingRecentIssueItem> =
        issuesDao.getAll()

    override fun getSavedItems(count: Int): PagingSource<Int, PagingRecentIssueItem> =
        issuesDao.get(count)

    override suspend fun saveItems(
        items: List<PagingRecentIssueItem>,
        remoteKeys: List<RecentIssueItemRemoteKeys>,
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