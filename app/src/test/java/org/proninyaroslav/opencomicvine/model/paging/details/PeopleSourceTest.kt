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
import org.proninyaroslav.opencomicvine.types.PersonInfo
import org.proninyaroslav.opencomicvine.types.PeopleResponse
import org.proninyaroslav.opencomicvine.types.FavoriteInfo
import org.proninyaroslav.opencomicvine.types.StatusCode
import org.proninyaroslav.opencomicvine.types.filter.PeopleFilter
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.PeopleRepository
import org.proninyaroslav.opencomicvine.model.repo.FavoriteFetchResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository

class PeopleSourceTest {
    lateinit var source: PeopleSource

    @MockK
    lateinit var peopleRepo: PeopleRepository

    @MockK
    lateinit var pref: AppPreferences

    @MockK
    lateinit var favoritesRepo: FavoritesRepository

    val idList = (1..10).toList()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        source = PeopleSource(
            idList,
            peopleRepo,
            favoritesRepo,
        )
    }

    @Test
    fun `Load success result when more data is present`() = runTest {
        val pageSize = 5
        val response = mockk<PeopleResponse>()
        val peopleList = List(pageSize) {
            val info = mockk<PersonInfo>()
            every { info.id } returns it
            every {
                favoritesRepo.observe(
                    entityId = it,
                    entityType = FavoriteInfo.EntityType.Person,
                )
            } returns flowOf(FavoriteFetchResult.Success(isFavorite = true))
            info
        }

        every { response.statusCode } returns StatusCode.OK
        every { response.results } returns peopleList
        every { response.numberOfPageResults } returns pageSize
        every { response.error } returns "OK"
        every { response.limit } returns pageSize
        every { response.offset } returns 0
        every { response.numberOfTotalResults } returns pageSize
        coEvery {
            peopleRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = null,
                filters = listOf(PeopleFilter.Id(idList.subList(0, pageSize))),
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
            peopleRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = null,
                filters = listOf(PeopleFilter.Id(idList.subList(0, pageSize))),
            )
        }
        confirmVerified(peopleRepo, response)
    }

    @Test
    fun `Load success result and endOfPaginationReached when no more data`() = runTest {
        val pageSize = 10
        val response = mockk<PeopleResponse>()
        val peopleList = emptyList<PersonInfo>()

        every { response.statusCode } returns StatusCode.OK
        every { response.results } returns peopleList
        every { response.error } returns "OK"
        every { response.limit } returns pageSize
        every { response.offset } returns 0
        every { response.numberOfPageResults } returns 0
        every { response.numberOfTotalResults } returns pageSize
        coEvery {
            peopleRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = null,
                filters = listOf(PeopleFilter.Id(idList.subList(0, pageSize))),
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
            peopleRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = null,
                filters = listOf(PeopleFilter.Id(idList.subList(0, pageSize))),
            )
        }
        confirmVerified(peopleRepo, response)
    }

    @Test
    fun `Service error`() = runTest {
        val pageSize = 10
        val response = mockk<PeopleResponse>()

        every { response.statusCode } returns StatusCode.InvalidAPIKey
        every { response.error } returns "Invalid API Key"
        coEvery {
            peopleRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = null,
                filters = listOf(PeopleFilter.Id(idList.subList(0, pageSize))),
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
            peopleRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = null,
                filters = listOf(PeopleFilter.Id(idList.subList(0, pageSize))),
            )
        }
        confirmVerified(peopleRepo, response)
    }
}