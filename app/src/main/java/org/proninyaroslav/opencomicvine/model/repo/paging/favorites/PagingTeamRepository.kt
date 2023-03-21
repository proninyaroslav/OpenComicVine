package org.proninyaroslav.opencomicvine.model.repo.paging.favorites

import androidx.paging.PagingSource
import androidx.room.withTransaction
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesTeamItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesTeamItem
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import java.io.IOException
import javax.inject.Inject

interface PagingTeamRepository :
    FavoritesPagingRepository<PagingFavoritesTeamItem, FavoritesTeamItemRemoteKeys>

class PagingTeamRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
) : PagingTeamRepository {

    private val teamsDao = appDatabase.favoritesTeamsDao()
    private val teamsRemoteKeysDao = appDatabase.favoritesTeamsRemoteKeysDao()

    override suspend fun getRemoteKeysById(id: Int): ComicVinePagingRepository.Result<FavoritesTeamItemRemoteKeys?> {
        return try {
            ComicVinePagingRepository.Result.Success(teamsRemoteKeysDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun getItemById(id: Int): ComicVinePagingRepository.Result<PagingFavoritesTeamItem?> {
        return try {
            ComicVinePagingRepository.Result.Success(teamsDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override fun getAllSavedItems(): PagingSource<Int, PagingFavoritesTeamItem> =
        teamsDao.getAll()

    override fun getSavedItems(count: Int): PagingSource<Int, PagingFavoritesTeamItem> =
        teamsDao.get(count)

    override suspend fun deleteByIdList(idList: List<Int>): ComicVinePagingRepository.Result<Unit> {
        return try {
            teamsDao.deleteList(idList)
            ComicVinePagingRepository.Result.Success(Unit)
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun saveItems(
        items: List<PagingFavoritesTeamItem>,
        remoteKeys: List<FavoritesTeamItemRemoteKeys>,
        clearBeforeSave: Boolean,
    ): ComicVinePagingRepository.Result<Unit> = try {
        appDatabase.withTransaction {
            if (clearBeforeSave) {
                teamsDao.deleteAll()
                teamsRemoteKeysDao.deleteAll()
            }
            teamsRemoteKeysDao.insertList(remoteKeys)
            teamsDao.insertList(items)
        }
        ComicVinePagingRepository.Result.Success(Unit)
    } catch (e: IOException) {
        ComicVinePagingRepository.Result.Failed.IO(e)
    }
}