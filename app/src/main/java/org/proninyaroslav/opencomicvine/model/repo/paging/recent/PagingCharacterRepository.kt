package org.proninyaroslav.opencomicvine.model.repo.paging.recent

import androidx.paging.PagingSource
import androidx.room.withTransaction
import org.proninyaroslav.opencomicvine.data.paging.recent.PagingRecentCharacterItem
import org.proninyaroslav.opencomicvine.data.paging.recent.RecentCharacterItemRemoteKeys
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import java.io.IOException
import javax.inject.Inject

interface PagingCharacterRepository :
    ComicVinePagingRepository<PagingRecentCharacterItem, RecentCharacterItemRemoteKeys>

class PagingCharacterRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
) : PagingCharacterRepository {

    private val charactersDao = appDatabase.recentCharactersDao()
    private val charactersRemoteKeysDao = appDatabase.recentCharactersRemoteKeysDao()

    override suspend fun getRemoteKeysById(id: Int): ComicVinePagingRepository.Result<RecentCharacterItemRemoteKeys?> {
        return try {
            ComicVinePagingRepository.Result.Success(charactersRemoteKeysDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun getItemById(id: Int): ComicVinePagingRepository.Result<PagingRecentCharacterItem?> {
        return try {
            ComicVinePagingRepository.Result.Success(charactersDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override fun getAllSavedItems(): PagingSource<Int, PagingRecentCharacterItem> =
        charactersDao.getAll()

    override fun getSavedItems(count: Int): PagingSource<Int, PagingRecentCharacterItem> =
        charactersDao.get(count)

    override suspend fun saveItems(
        items: List<PagingRecentCharacterItem>,
        remoteKeys: List<RecentCharacterItemRemoteKeys>,
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