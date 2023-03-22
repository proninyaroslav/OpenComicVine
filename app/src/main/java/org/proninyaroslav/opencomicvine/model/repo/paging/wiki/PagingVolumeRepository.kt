/*
 * Copyright (C) 2023 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of OpenComicVine.
 *
 * OpenComicVine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenComicVine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenComicVine.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.proninyaroslav.opencomicvine.model.repo.paging.wiki

import androidx.paging.PagingSource
import androidx.room.withTransaction
import org.proninyaroslav.opencomicvine.data.paging.wiki.PagingWikiVolumeItem
import org.proninyaroslav.opencomicvine.data.paging.wiki.WikiVolumeItemRemoteKeys
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import java.io.IOException
import javax.inject.Inject

interface PagingVolumeRepository :
    ComicVinePagingRepository<PagingWikiVolumeItem, WikiVolumeItemRemoteKeys>

class PagingVolumeRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
) : PagingVolumeRepository {

    private val volumesDao = appDatabase.wikiVolumesDao()
    private val volumesRemoteKeysDao = appDatabase.wikiVolumesRemoteKeysDao()

    override suspend fun getRemoteKeysById(id: Int): ComicVinePagingRepository.Result<WikiVolumeItemRemoteKeys?> {
        return try {
            ComicVinePagingRepository.Result.Success(volumesRemoteKeysDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun getItemById(id: Int): ComicVinePagingRepository.Result<PagingWikiVolumeItem?> {
        return try {
            ComicVinePagingRepository.Result.Success(volumesDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override fun getAllSavedItems(): PagingSource<Int, PagingWikiVolumeItem> =
        volumesDao.getAll()

    override fun getSavedItems(count: Int): PagingSource<Int, PagingWikiVolumeItem> =
        volumesDao.get(count)

    override suspend fun saveItems(
        items: List<PagingWikiVolumeItem>,
        remoteKeys: List<WikiVolumeItemRemoteKeys>,
        clearBeforeSave: Boolean,
    ): ComicVinePagingRepository.Result<Unit> = try {
        appDatabase.withTransaction {
            if (clearBeforeSave) {
                volumesDao.deleteAll()
                volumesRemoteKeysDao.deleteAll()
            }
            volumesRemoteKeysDao.insertList(remoteKeys)
            volumesDao.insertList(items)
        }
        ComicVinePagingRepository.Result.Success(Unit)
    } catch (e: IOException) {
        ComicVinePagingRepository.Result.Failed.IO(e)
    }
}
