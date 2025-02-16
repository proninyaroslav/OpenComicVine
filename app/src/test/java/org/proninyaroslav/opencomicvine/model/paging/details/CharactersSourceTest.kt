package org.proninyaroslav.opencomicvine.model.paging.details

import androidx.paging.PagingSource
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.types.CharacterInfo
import org.proninyaroslav.opencomicvine.types.CharactersResponse
import org.proninyaroslav.opencomicvine.types.FavoriteInfo
import org.proninyaroslav.opencomicvine.types.StatusCode
import org.proninyaroslav.opencomicvine.types.filter.CharactersFilter
import org.proninyaroslav.opencomicvine.types.sort.CharactersSort
import org.proninyaroslav.opencomicvine.types.sort.ComicVineSortDirection
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.repo.CharactersRepository
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoriteFetchResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository

class CharactersSourceTest {
    lateinit var source: CharactersSource

    @MockK
    lateinit var charactersRepo: CharactersRepository

    @MockK
    lateinit var pref: AppPreferences

    @MockK
    lateinit var favoritesRepo: FavoritesRepository

    val idList = (1..10).toList()

    val sort = CharactersSort.DateAdded(direction = ComicVineSortDirection.Asc)

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        source = CharactersSource(
            idList,
            flowOf(sort),
            charactersRepo,
            favoritesRepo,
        )
    }

    @Test
    fun `Load success result when more data is present`() = runTest {
        val pageSize = 5
        val response = mockk<CharactersResponse>()
        val charactersList = List(pageSize) {
            val info = mockk<CharacterInfo>()
            every { info.id } returns it
            every {
                favoritesRepo.observe(
                    entityId = it,
                    entityType = FavoriteInfo.EntityType.Character,
                )
            } returns flowOf(FavoriteFetchResult.Success(isFavorite = true))
            info
        }

        every { response.statusCode } returns StatusCode.OK
        every { response.results } returns charactersList
        every { response.numberOfPageResults } returns pageSize
        every { response.error } returns "OK"
        every { response.limit } returns pageSize
        every { response.offset } returns 0
        every { response.numberOfTotalResults } returns pageSize
        coEvery {
            charactersRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = sort,
                filters = listOf(CharactersFilter.Id(idList.subList(0, pageSize))),
            )
        } returns ComicVineResult.Success(response)

        val result = source.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = pageSize,
                placeholdersEnabled = false,
            )
        )
        assertTrue(result is PagingSource.LoadResult.Page)
        assertNotNull((result as PagingSource.LoadResult.Page).nextKey)

        verifyAll {
            response.statusCode
            response.results
            response.numberOfPageResults
            response.error
            response.limit
            response.offset
            response.numberOfTotalResults
        }
        coVerify {
            charactersRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = sort,
                filters = listOf(CharactersFilter.Id(idList.subList(0, pageSize))),
            )
        }
        confirmVerified(charactersRepo, response)
    }

    @Test
    fun `Load success result and endOfPaginationReached when no more data`() = runTest {
        val pageSize = 10
        val response = mockk<CharactersResponse>()
        val charactersList = emptyList<CharacterInfo>()

        every { response.statusCode } returns StatusCode.OK
        every { response.results } returns charactersList
        every { response.error } returns "OK"
        every { response.limit } returns pageSize
        every { response.offset } returns 0
        every { response.numberOfPageResults } returns 0
        every { response.numberOfTotalResults } returns pageSize
        coEvery {
            charactersRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = sort,
                filters = listOf(CharactersFilter.Id(idList.subList(0, pageSize))),
            )
        } returns ComicVineResult.Success(response)

        val result = source.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = pageSize,
                placeholdersEnabled = false,
            )
        )
        assertTrue(result is PagingSource.LoadResult.Page)
        assertNull((result as PagingSource.LoadResult.Page).nextKey)

        verifyAll {
            response.statusCode
            response.results
            response.error
            response.limit
            response.offset
            response.numberOfPageResults
            response.numberOfTotalResults
        }
        coVerify {
            charactersRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = sort,
                filters = listOf(CharactersFilter.Id(idList.subList(0, pageSize))),
            )
        }
        confirmVerified(charactersRepo, response)
    }

    @Test
    fun `Service error`() = runTest {
        val pageSize = 10
        val response = mockk<CharactersResponse>()

        every { response.statusCode } returns StatusCode.InvalidAPIKey
        every { response.error } returns "Invalid API Key"
        coEvery {
            charactersRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = sort,
                filters = listOf(CharactersFilter.Id(idList.subList(0, pageSize))),
            )
        } returns ComicVineResult.Success(response)

        val result = source.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = pageSize,
                placeholdersEnabled = false,
            )
        )
        assertTrue(result is PagingSource.LoadResult.Error)
        (result as PagingSource.LoadResult.Error).run {
            val error = throwable as DetailsEntitySource.Error.Service
            assertEquals(StatusCode.InvalidAPIKey, error.statusCode)
            assertEquals("Invalid API Key", error.errorMessage)
        }

        verifyAll {
            response.statusCode
            response.error
        }
        coVerify {
            charactersRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = sort,
                filters = listOf(CharactersFilter.Id(idList.subList(0, pageSize))),
            )
        }
        confirmVerified(charactersRepo, response)
    }
}