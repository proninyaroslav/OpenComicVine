package org.proninyaroslav.opencomicvine.model.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.proninyaroslav.opencomicvine.data.SearchHistoryInfo
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import java.io.IOException
import javax.inject.Inject

interface SearchHistoryRepository {
    suspend fun getByQuery(query: String): Result<SearchHistoryInfo?>

    fun observeAll(): Flow<Result<List<SearchHistoryInfo>>>

    suspend fun delete(item: SearchHistoryInfo): Result<Unit>

    suspend fun deleteList(items: List<SearchHistoryInfo>): Result<Unit>

    suspend fun insert(item: SearchHistoryInfo): Result<Unit>

    sealed interface Result<out T> {
        data class Success<T>(val data: T) : Result<T>

        sealed interface Failed : Result<Nothing> {
            data class IO(val exception: IOException) : Failed
        }
    }
}

class SearchHistoryRepositoryImpl @Inject constructor(
    appDatabase: AppDatabase,
) : SearchHistoryRepository {
    private val searchHistoryDao = appDatabase.searchHistoryDao()
    override suspend fun getByQuery(query: String): SearchHistoryRepository.Result<SearchHistoryInfo?> {
        return try {
            SearchHistoryRepository.Result.Success(
                searchHistoryDao.getByQuery(query)
            )
        } catch (e: IOException) {
            SearchHistoryRepository.Result.Failed.IO(e)
        }
    }

    override fun observeAll(): Flow<SearchHistoryRepository.Result<List<SearchHistoryInfo>>> {
        return searchHistoryDao.observeAll()
            .map<List<SearchHistoryInfo>, SearchHistoryRepository.Result<List<SearchHistoryInfo>>> {
                SearchHistoryRepository.Result.Success(it)
            }
            .catch {
                if (it is IOException) {
                    emit(SearchHistoryRepository.Result.Failed.IO(it))
                } else {
                    throw it
                }
            }
    }

    override suspend fun delete(item: SearchHistoryInfo): SearchHistoryRepository.Result<Unit> {
        return try {
            searchHistoryDao.delete(item)
            SearchHistoryRepository.Result.Success(Unit)
        } catch (e: IOException) {
            SearchHistoryRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun deleteList(items: List<SearchHistoryInfo>): SearchHistoryRepository.Result<Unit> {
        return try {
            searchHistoryDao.deleteList(items)
            SearchHistoryRepository.Result.Success(Unit)
        } catch (e: IOException) {
            SearchHistoryRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun insert(item: SearchHistoryInfo): SearchHistoryRepository.Result<Unit> {
        return try {
            searchHistoryDao.insert(item)
            SearchHistoryRepository.Result.Success(Unit)
        } catch (e: IOException) {
            SearchHistoryRepository.Result.Failed.IO(e)
        }
    }
}