package org.proninyaroslav.opencomicvine.model.repo

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.runs
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.db.FavoritesDao
import org.proninyaroslav.opencomicvine.types.FavoriteInfo
import java.util.Calendar

class FavoritesRepositoryTest {
    lateinit var repo: FavoritesRepository

    @RelaxedMockK
    lateinit var appDatabase: AppDatabase

    @MockK
    lateinit var favoritesDao: FavoritesDao

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(
            "androidx.room.RoomDatabaseKt"
        )
        every { appDatabase.favoritesDao() } returns favoritesDao

        repo = FavoritesRepositoryImpl(appDatabase)
    }

    @Test
    fun add() = runTest {
        val info = FavoriteInfo(
            entityId = 1,
            entityType = FavoriteInfo.EntityType.Character,
            dateAdded = Calendar.getInstance().time,
        )

        coEvery { favoritesDao.insert(info) } just runs

        assertEquals(FavoritesRepository.Result.Success(Unit), repo.add(info))

        coVerify { favoritesDao.insert(info) }
        confirmVerified(favoritesDao)
    }

    @Test
    fun get() = runTest {
        val info = FavoriteInfo(
            entityId = 1,
            entityType = FavoriteInfo.EntityType.Character,
            dateAdded = Calendar.getInstance().time,
        )

        coEvery {
            favoritesDao.get(
                entityId = info.entityId,
                entityType = info.entityType.name,
            )
        } returns info

        assertEquals(
            FavoritesRepository.Result.Success(info),
            repo.get(
                entityId = info.entityId,
                entityType = info.entityType,
            ),
        )

        coVerify {
            favoritesDao.get(
                entityId = info.entityId,
                entityType = info.entityType.name,
            )
        }
        confirmVerified(favoritesDao)
    }

    @Test
    fun delete() = runTest {
        val info = FavoriteInfo(
            entityId = 1,
            entityType = FavoriteInfo.EntityType.Character,
            dateAdded = Calendar.getInstance().time,
        )

        coEvery { favoritesDao.delete(info) } just runs

        assertEquals(FavoritesRepository.Result.Success(Unit), repo.delete(info))

        coVerify { favoritesDao.delete(info) }
        confirmVerified(favoritesDao)
    }

    @Test
    fun observe() = runTest {
        val info = FavoriteInfo(
            entityId = 1,
            entityType = FavoriteInfo.EntityType.Character,
            dateAdded = Calendar.getInstance().time,
        )

        coEvery {
            favoritesDao.observeDistinctUntilChanged(
                entityId = info.entityId,
                entityType = info.entityType.name,
            )
        } returns flowOf(info)

        assertEquals(
            FavoriteFetchResult.Success(isFavorite = true),
            repo.observe(
                entityId = info.entityId,
                entityType = info.entityType,
            ).first(),
        )

        coVerify {
            favoritesDao.observeDistinctUntilChanged(
                entityId = info.entityId,
                entityType = info.entityType.name,
            )
        }
        confirmVerified(favoritesDao)
    }
}