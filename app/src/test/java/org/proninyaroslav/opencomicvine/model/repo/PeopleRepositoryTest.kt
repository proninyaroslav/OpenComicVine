package org.proninyaroslav.opencomicvine.model.repo

import com.skydoves.sandwich.ApiResponse
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.data.PeopleResponse
import org.proninyaroslav.opencomicvine.data.PersonInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.filter.PeopleFilter
import org.proninyaroslav.opencomicvine.model.network.ComicVineService
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class PeopleRepositoryTest {
    lateinit var repo: PeopleRepository

    @MockK
    lateinit var apiKeyRepo: ApiKeyRepository

    @MockK
    lateinit var comicVineService: ComicVineService

    @MockK
    lateinit var peopleList: List<PersonInfo>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        repo = PeopleRepositoryImpl(comicVineService, apiKeyRepo)
    }

    @Test
    fun getPeopleList() = runTest {
        val apiKey = "123"
        val response = PeopleResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 100,
            offset = 0,
            numberOfPageResults = 2,
            numberOfTotalResults = 2,
            results = peopleList,
        )
        val filters = listOf(PeopleFilter.Id(listOf(1)))

        every { apiKeyRepo.get() } returns flowOf(ApiKeyRepository.GetResult.Success(apiKey))
        coEvery {
            comicVineService.people(
                apiKey = apiKey,
                offset = response.offset,
                limit = response.limit,
                sort = null,
                filter = filters,
            )
        } returns ApiResponse.Success(Response.success(response))

        val res = repo.getItems(
            offset = response.offset,
            limit = response.limit,
            sort = null,
            filters = filters,
        )
        assertEquals(ComicVineResult.Success(response), res)

        verify { apiKeyRepo.get() }
        coVerify {
            comicVineService.people(
                apiKey = apiKey,
                offset = response.offset,
                limit = response.limit,
                sort = null,
                filter = filters,
            )
        }
        confirmVerified(apiKeyRepo, comicVineService)
    }

    @Test
    fun `getPeopleList API key error`() = runTest {
        val error = ApiKeyRepository.GetResult.Failed.NoApiKey

        every { apiKeyRepo.get() } returns flowOf(error)

        val res = repo.getItems(
            offset = 0,
            limit = 0,
            sort = null,
            filters = emptyList(),
        )
        assertEquals(
            ComicVineResult.Failed.ApiKeyError(error),
            res
        )

        verify { apiKeyRepo.get() }
        confirmVerified(apiKeyRepo)
    }
}