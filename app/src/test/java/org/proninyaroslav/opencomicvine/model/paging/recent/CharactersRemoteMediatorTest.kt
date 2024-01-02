package org.proninyaroslav.opencomicvine.model.paging.recent

import androidx.paging.*
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.types.*
import org.proninyaroslav.opencomicvine.types.paging.recent.PagingRecentCharacterItem
import org.proninyaroslav.opencomicvine.types.paging.recent.RecentCharacterItemRemoteKeys
import org.proninyaroslav.opencomicvine.types.preferences.PrefRecentCharactersFilter
import org.proninyaroslav.opencomicvine.types.preferences.PrefRecentCharactersFilterBundle
import org.proninyaroslav.opencomicvine.types.sort.CharactersSort
import org.proninyaroslav.opencomicvine.types.sort.ComicVineSortDirection
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.paging.ComicVineRemoteMediator
import org.proninyaroslav.opencomicvine.model.repo.CharactersRepository
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingCharacterRepository

@OptIn(ExperimentalCoroutinesApi::class)
class CharactersRemoteMediatorTest {
    lateinit var mediator: CharactersRemoteMediator

    @MockK
    lateinit var charactersRepo: CharactersRepository

    @MockK
    lateinit var characterItemRepo: PagingCharacterRepository

    @MockK
    lateinit var pref: AppPreferences

    val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mediator =
            CharactersRemoteMediator(
                endOfPaginationOffset = null,
                charactersRepo,
                characterItemRepo,
                pref,
                dispatcher,
            )
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Load success result when more data is present`() = runTest {
        val pageSize = 10
        val response = mockk<CharactersResponse>()
        val charactersList = List(pageSize) { mockk<CharacterInfo>() }
        val itemsList = charactersList.mapIndexed { i, info ->
            val character = PagingRecentCharacterItem(
                index = i,
                info = info,
            )
            character
        }
        val remoteKeysList = itemsList.map { character ->
            RecentCharacterItemRemoteKeys(
                id = character.index,
                prevOffset = null,
                nextOffset = pageSize,
            )
        }
        val pagingState = PagingState<Int, PagingRecentCharacterItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )
        val filter = PrefRecentCharactersFilterBundle(
            dateAdded = PrefRecentCharactersFilter.DateAdded.Unknown
        )

        every { response.statusCode } returns StatusCode.OK
        every { response.results } returns charactersList
        every { response.numberOfPageResults } returns pageSize
        coEvery {
            charactersRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = CharactersSort.DateAdded(ComicVineSortDirection.Desc),
                filters = emptyList(),
            )
        } returns ComicVineResult.Success(response)
        coEvery {
            characterItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        } returns ComicVinePagingRepository.Result.Success(Unit)
        itemsList.forEach { character ->
            coEvery {
                characterItemRepo.getRemoteKeysById(character.index)
            } returns ComicVinePagingRepository.Result.Success(remoteKeysList[character.index])
        }
        every { pref.recentCharactersFilters } returns flowOf(filter)

        val result = mediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        verifyAll {
            response.statusCode
            response.results
            response.numberOfPageResults
        }
        coVerify {
            charactersRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = CharactersSort.DateAdded(ComicVineSortDirection.Desc),
                filters = emptyList(),
            )
        }
        coVerify {
            characterItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        }
        verify { pref.recentCharactersFilters }
        confirmVerified(charactersRepo, characterItemRepo, response)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Load success result and endOfPaginationReached when no more data`() = runTest {
        val pageSize = 10
        val response = mockk<CharactersResponse>()
        val charactersList = emptyList<CharacterInfo>()
        val itemsList = emptyList<PagingRecentCharacterItem>()
        val remoteKeysList = emptyList<RecentCharacterItemRemoteKeys>()
        val pagingState = PagingState<Int, PagingRecentCharacterItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )
        val filter = PrefRecentCharactersFilterBundle(
            dateAdded = PrefRecentCharactersFilter.DateAdded.Unknown
        )

        every { response.statusCode } returns StatusCode.OK
        every { response.results } returns charactersList
        every { response.numberOfPageResults } returns 0
        coEvery {
            charactersRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = CharactersSort.DateAdded(ComicVineSortDirection.Desc),
                filters = emptyList(),
            )
        } returns ComicVineResult.Success(response)
        coEvery {
            characterItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        } returns ComicVinePagingRepository.Result.Success(Unit)
        charactersList.forEach { character ->
            coEvery {
                characterItemRepo.getRemoteKeysById(character.id)
            } returns ComicVinePagingRepository.Result.Success(null)
        }
        every { pref.recentCharactersFilters } returns flowOf(filter)

        val result = mediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        verifyAll {
            response.statusCode
            response.results
            response.numberOfPageResults
        }
        coVerify {
            charactersRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = CharactersSort.DateAdded(ComicVineSortDirection.Desc),
                filters = emptyList(),
            )
        }
        coVerify {
            characterItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        }
        verify { pref.recentCharactersFilters }
        confirmVerified(charactersRepo, characterItemRepo, response)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Service error`() = runTest {
        val pageSize = 10
        val response = mockk<CharactersResponse>()
        val pagingState = PagingState<Int, PagingRecentCharacterItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )
        val filter = PrefRecentCharactersFilterBundle(
            dateAdded = PrefRecentCharactersFilter.DateAdded.Unknown
        )

        every { response.statusCode } returns StatusCode.InvalidAPIKey
        every { response.error } returns "Invalid API Key"
        coEvery {
            charactersRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = CharactersSort.DateAdded(ComicVineSortDirection.Desc),
                filters = emptyList(),
            )
        } returns ComicVineResult.Success(response)

        every { pref.recentCharactersFilters } returns flowOf(filter)

        val result = mediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Error)
        (result as RemoteMediator.MediatorResult.Error).run {
            val error = throwable as RecentEntityRemoteMediator.Error.Service
            assertEquals(StatusCode.InvalidAPIKey, error.statusCode)
            assertEquals("Invalid API Key", error.errorMessage)
        }

        verifyAll {
            response.statusCode
            response.error
        }
        verify { pref.recentCharactersFilters }
        coVerify {
            charactersRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = CharactersSort.DateAdded(ComicVineSortDirection.Desc),
                filters = emptyList(),
            )
        }
        confirmVerified(charactersRepo, response)
    }

    @Test
    fun stateToError() {
        val error = RecentEntityRemoteMediator.Error.Service(
            statusCode = StatusCode.InvalidAPIKey,
            errorMessage = "Invalid API Key"
        )
        val state = LoadState.Error(error)
        assertEquals(error, ComicVineRemoteMediator.stateToError(state))
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Load success result and endOfPaginationReached when max offset is reached`() = runTest {
        val pageSize = 10
        mediator =
            CharactersRemoteMediator(
                endOfPaginationOffset = pageSize - 1,
                charactersRepo,
                characterItemRepo,
                pref,
                dispatcher,
            )
        val response = mockk<CharactersResponse>()
        val charactersList = emptyList<CharacterInfo>()
        val itemsList = emptyList<PagingRecentCharacterItem>()
        val remoteKeysList = emptyList<RecentCharacterItemRemoteKeys>()
        val pagingState = PagingState<Int, PagingRecentCharacterItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )
        val filter = PrefRecentCharactersFilterBundle(
            dateAdded = PrefRecentCharactersFilter.DateAdded.Unknown
        )

        every { response.statusCode } returns StatusCode.OK
        every { response.results } returns charactersList
        coEvery {
            charactersRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = CharactersSort.DateAdded(ComicVineSortDirection.Desc),
                filters = emptyList(),
            )
        } returns ComicVineResult.Success(response)
        coEvery {
            characterItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        } returns ComicVinePagingRepository.Result.Success(Unit)
        charactersList.forEach { character ->
            coEvery {
                characterItemRepo.getRemoteKeysById(character.id)
            } returns ComicVinePagingRepository.Result.Success(null)
        }
        every { pref.recentCharactersFilters } returns flowOf(filter)

        val result = mediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        verifyAll {
            response.statusCode
            response.results
        }
        coVerify {
            charactersRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = CharactersSort.DateAdded(ComicVineSortDirection.Desc),
                filters = emptyList(),
            )
        }
        coVerify {
            characterItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        }
        verify { pref.recentCharactersFilters }
        confirmVerified(charactersRepo, characterItemRepo, response)
    }
}