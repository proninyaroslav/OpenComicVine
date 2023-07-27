package org.proninyaroslav.opencomicvine.ui.viewmodel

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.model.DateProvider
import org.proninyaroslav.opencomicvine.model.paging.recent.*
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import java.io.IOException
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {
    @MockK
    lateinit var favoritesRepo: FavoritesRepository

    lateinit var viewModel: FavoritesViewModel

    @MockK
    lateinit var dateProvider: DateProvider

    val id = 1

    val dispatcher = StandardTestDispatcher()

    private val nowDate = Date(GregorianCalendar(2022, 0, 1).timeInMillis)

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)

        every { dateProvider.now } returns nowDate

        viewModel = FavoritesViewModel(
            favoritesRepo,
            dateProvider,
            dispatcher,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Set favorite`() = runTest {
        val info = FavoriteInfo(
            entityId = id,
            entityType = FavoriteInfo.EntityType.Character,
            dateAdded = nowDate,
        )

        val expectedStates = listOf(
            SwitchFavoriteState.Initial,
            SwitchFavoriteState.Added(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Character,
            )
        )
        val actualStates = mutableListOf<SwitchFavoriteState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.switchFavorite.state.toList(actualStates)
        }

        coEvery {
            favoritesRepo.get(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Character,
            )
        } returns FavoritesRepository.Result.Success(null)
        coEvery {
            favoritesRepo.add(info)
        } returns FavoritesRepository.Result.Success(Unit)

        dispatcher.scheduler.run {
            viewModel.switchFavorite(id, FavoriteInfo.EntityType.Character)
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        coVerify {
            favoritesRepo.get(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Character,
            )
        }
        coVerify { favoritesRepo.add(info) }
        confirmVerified(favoritesRepo)
    }

    @Test
    fun `Unset favorite`() = runTest {
        val info = FavoriteInfo(
            entityId = id,
            entityType = FavoriteInfo.EntityType.Character,
            dateAdded = nowDate,
        )

        val expectedStates = listOf(
            SwitchFavoriteState.Initial,
            SwitchFavoriteState.Removed(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Character,
            )
        )
        val actualStates = mutableListOf<SwitchFavoriteState>()

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.switchFavorite.state.toList(actualStates)
        }

        coEvery {
            favoritesRepo.get(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Character,
            )
        } returns FavoritesRepository.Result.Success(info)
        coEvery {
            favoritesRepo.delete(info)
        } returns FavoritesRepository.Result.Success(Unit)

        dispatcher.scheduler.run {
            viewModel.switchFavorite(id, FavoriteInfo.EntityType.Character)
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        coVerify {
            favoritesRepo.get(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Character,
            )
        }
        coVerify { favoritesRepo.delete(info) }
        confirmVerified(favoritesRepo)
    }

    @Test
    fun `Set favorite failed`() = runTest {
        val error = FavoritesRepository.Result.Failed.IO(IOException())

        val expectedStates = listOf(
            SwitchFavoriteState.Initial,
            SwitchFavoriteState.Failed(error = error)
        )
        val actualStates = mutableListOf<SwitchFavoriteState>()

        coEvery {
            favoritesRepo.get(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Character,
            )
        } returns error

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.switchFavorite.state.toList(actualStates)
        }

        viewModel.switchFavorite(id, FavoriteInfo.EntityType.Character)
        dispatcher.scheduler.runCurrent()

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        coVerify {
            favoritesRepo.get(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Character,
            )
        }
        confirmVerified(favoritesRepo)
    }
}