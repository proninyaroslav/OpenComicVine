package org.proninyaroslav.opencomicvine.model.repo

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ApiKeyRepositoryTest {
    private lateinit var repo: ApiKeyRepository

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val dataStore = PreferenceDataStoreFactory.create(
        scope = coroutineScope,
        produceFile = { context.preferencesDataStoreFile("test_preferences") }
    )

    @Before
    fun setUp() {
        repo = ApiKeyRepositoryImpl(dataStore)
    }

    @After
    fun tearDown() = runTest {
        dataStore.edit { it.clear() }
        coroutineScope.cancel()
    }

    @Test
    fun noApiKey() = runTest {
        assertEquals(
            ApiKeyRepository.GetResult.Failed.NoApiKey,
            repo.get().first(),
        )
    }

    @Test
    fun set() = runTest {
        val key = "key"
        assertTrue(repo.set(key) is ApiKeyRepository.SaveResult.Success)
        assertEquals(
            ApiKeyRepository.GetResult.Success(key),
            repo.get().first(),
        )
    }
}