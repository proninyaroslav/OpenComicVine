package org.proninyaroslav.opencomicvine.model.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

interface ApiKeyRepository {
    suspend fun set(apiKey: String): SaveResult<Unit>

    fun get(): Flow<GetResult<String>>

    sealed interface GetResult<out T> {
        data class Success<T>(val data: T) : GetResult<T>

        sealed interface Failed : GetResult<Nothing> {
            object NoApiKey : Failed

            data class IO(val exception: IOException) : Failed
        }
    }

    sealed interface SaveResult<out T> {
        data class Success<T>(val data: T) : SaveResult<T>

        sealed interface Failed : SaveResult<Nothing> {
            data class IO(val exception: IOException) : Failed
        }
    }
}

class ApiKeyRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ApiKeyRepository {
    private val apiKeyPref = stringPreferencesKey(
        "org.proninyaroslav.opencomicvine.ApiKeyRepository.pref_api_key"
    )

    override suspend fun set(apiKey: String): ApiKeyRepository.SaveResult<Unit> {
        return try {
            dataStore.edit { settings ->
                settings[apiKeyPref] = apiKey
            }
            ApiKeyRepository.SaveResult.Success(Unit)
        } catch (e: IOException) {
            ApiKeyRepository.SaveResult.Failed.IO(e)
        }
    }

    override fun get(): Flow<ApiKeyRepository.GetResult<String>> = dataStore.data
        .map { preferences ->
            val key = preferences[apiKeyPref]
            key?.let { ApiKeyRepository.GetResult.Success(key) }
                ?: ApiKeyRepository.GetResult.Failed.NoApiKey
        }
        .catch { e ->
            if (e is IOException) {
                emit(ApiKeyRepository.GetResult.Failed.IO(e))
            } else {
                throw e
            }
        }
}