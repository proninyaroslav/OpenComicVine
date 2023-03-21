package org.proninyaroslav.opencomicvine.model.repo.paging.wiki

import androidx.paging.PagingSource
import androidx.room.withTransaction
import org.proninyaroslav.opencomicvine.data.paging.wiki.PagingWikiCharacterItem
import org.proninyaroslav.opencomicvine.data.paging.wiki.WikiCharacterItemRemoteKeys
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import java.io.IOException
import javax.inject.Inject

interface PagingCharacterRepository :
    ComicVinePagingRepository<PagingWikiCharacterItem, WikiCharacterItemRemoteKeys>

class PagingCharacterRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
) : PagingCharacterRepository {

    private val charactersDao = appDatabase.wikiCharactersDao()
    private val charactersRemoteKeysDao = appDatabase.wikiCharactersRemoteKeysDao()

    override suspend fun getRemoteKeysById(id: Int): ComicVinePagingRepository.Result<WikiCharacterItemRemoteKeys?> {
        return try {
            ComicVinePagingRepository.Result.Success(charactersRemoteKeysDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun getItemById(id: Int): ComicVinePagingRepository.Result<PagingWikiCharacterItem?> {
        return try {
            ComicVinePagingRepository.Result.Success(charactersDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override fun getAllSavedItems(): PagingSource<Int, PagingWikiCharacterItem> =
        charactersDao.getAll()

    override fun getSavedItems(count: Int): PagingSource<Int, PagingWikiCharacterItem> =
        charactersDao.get(count)

    override suspend fun saveItems(
        items: List<PagingWikiCharacterItem>,
        remoteKeys: List<WikiCharacterItemRemoteKeys>,
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