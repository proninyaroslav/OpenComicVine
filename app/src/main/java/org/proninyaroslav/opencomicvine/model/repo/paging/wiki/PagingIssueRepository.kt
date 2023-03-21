package org.proninyaroslav.opencomicvine.model.repo.paging.wiki

import androidx.paging.PagingSource
import androidx.room.withTransaction
import org.proninyaroslav.opencomicvine.data.paging.wiki.PagingWikiIssueItem
import org.proninyaroslav.opencomicvine.data.paging.wiki.WikiIssueItemRemoteKeys
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import java.io.IOException
import javax.inject.Inject

interface PagingIssueRepository :
    ComicVinePagingRepository<PagingWikiIssueItem, WikiIssueItemRemoteKeys>

class PagingIssueRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
) : PagingIssueRepository {

    private val issuesDao = appDatabase.wikiIssuesDao()
    private val issuesRemoteKeysDao = appDatabase.wikiIssuesRemoteKeysDao()

    override suspend fun getRemoteKeysById(id: Int): ComicVinePagingRepository.Result<WikiIssueItemRemoteKeys?> {
        return try {
            ComicVinePagingRepository.Result.Success(issuesRemoteKeysDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun getItemById(id: Int): ComicVinePagingRepository.Result<PagingWikiIssueItem?> {
        return try {
            ComicVinePagingRepository.Result.Success(issuesDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override fun getAllSavedItems(): PagingSource<Int, PagingWikiIssueItem> =
        issuesDao.getAll()

    override fun getSavedItems(count: Int): PagingSource<Int, PagingWikiIssueItem> =
        issuesDao.get(count)

    override suspend fun saveItems(
        items: List<PagingWikiIssueItem>,
        remoteKeys: List<WikiIssueItemRemoteKeys>,
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