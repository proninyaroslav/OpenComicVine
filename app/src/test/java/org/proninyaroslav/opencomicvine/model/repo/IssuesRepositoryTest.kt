package org.proninyaroslav.opencomicvine.model.repo

import com.skydoves.sandwich.ApiResponse
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.ByteString
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.model.network.ComicVineService
import org.proninyaroslav.opencomicvine.model.network.NoNetworkConnectionException
import org.proninyaroslav.opencomicvine.types.IssueDetails
import org.proninyaroslav.opencomicvine.types.IssueInfo
import org.proninyaroslav.opencomicvine.types.IssueResponse
import org.proninyaroslav.opencomicvine.types.IssuesResponse
import org.proninyaroslav.opencomicvine.types.StatusCode
import org.proninyaroslav.opencomicvine.types.filter.IssuesFilter
import org.proninyaroslav.opencomicvine.types.sort.ComicVineSortDirection
import org.proninyaroslav.opencomicvine.types.sort.IssuesSort
import retrofit2.Response
import java.io.IOException

class IssuesRepositoryTest {
    lateinit var repo: IssuesRepository

    @MockK
    lateinit var apiKeyRepo: ApiKeyRepository

    @MockK
    lateinit var comicVineService: ComicVineService

    @MockK
    lateinit var issuesList: List<IssueInfo>

    @MockK
    lateinit var issueDetails: IssueDetails

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        repo = IssuesRepositoryImpl(comicVineService, apiKeyRepo)
    }

    @Test
    fun getIssuesList() = runTest {
        val apiKey = "123"
        val response = IssuesResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 100,
            offset = 0,
            numberOfPageResults = 2,
            numberOfTotalResults = 2,
            results = issuesList,
        )
        val sort = IssuesSort.Name(ComicVineSortDirection.Asc)
        val filters = listOf(IssuesFilter.IssueNumber("1"))

        every { apiKeyRepo.get() } returns flowOf(ApiKeyRepository.GetResult.Success(apiKey))
        coEvery {
            comicVineService.issues(
                apiKey = apiKey,
                offset = response.offset,
                limit = response.limit,
                sort = sort,
                filter = filters,
            )
        } returns ApiResponse.Success(response)

        val res = repo.getItems(
            offset = response.offset,
            limit = response.limit,
            sort = sort,
            filters = filters,
        )
        assertEquals(ComicVineResult.Success(response), res)

        verify { apiKeyRepo.get() }
        coVerify {
            comicVineService.issues(
                apiKey = apiKey,
                offset = response.offset,
                limit = response.limit,
                sort = sort,
                filter = filters,
            )
        }
        confirmVerified(apiKeyRepo, comicVineService)
    }

    @Test
    fun `API key error`() = runTest {
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

    @Test
    fun `HTTP error`() = runTest {
        val apiKey = "123"

        every { apiKeyRepo.get() } returns flowOf(ApiKeyRepository.GetResult.Success(apiKey))
        coEvery {
            comicVineService.issues(
                apiKey = apiKey,
                offset = 0,
                limit = 100,
                sort = null,
                filter = null,
            )
        } returns ApiResponse.Failure.Error(
            Response.error<IssuesResponse>(
                404,
                ByteString.of().toResponseBody()
            )
        )

        val res = repo.getItems(
            offset = 0,
            limit = 100,
            sort = null,
            filters = emptyList(),
        )
        assertEquals(
            ComicVineResult.Failed.HttpError(
                com.skydoves.sandwich.StatusCode.NotFound
            ),
            res
        )

        verify { apiKeyRepo.get() }
        coVerify {
            comicVineService.issues(
                apiKey = apiKey,
                offset = 0,
                limit = 100,
                sort = null,
                filter = null,
            )
        }
        confirmVerified(apiKeyRepo, comicVineService)
    }

    @Test
    fun exception() = runTest {
        val apiKey = "123"
        val exception = IOException()

        every { apiKeyRepo.get() } returns flowOf(ApiKeyRepository.GetResult.Success(apiKey))
        coEvery {
            comicVineService.issues(
                apiKey = apiKey,
                offset = 0,
                limit = 100,
                sort = null,
                filter = null,
            )
        } returns ApiResponse.Failure.Exception(exception)

        val res = repo.getItems(
            offset = 0,
            limit = 100,
            sort = null,
            filters = emptyList(),
        )
        assertEquals(
            ComicVineResult.Failed.Exception(exception),
            res
        )

        verify { apiKeyRepo.get() }
        coVerify {
            comicVineService.issues(
                apiKey = apiKey,
                offset = 0,
                limit = 100,
                sort = null,
                filter = null,
            )
        }
        confirmVerified(apiKeyRepo, comicVineService)
    }

    @Test
    fun `No network connection`() = runTest {
        val apiKey = "123"
        val exception = NoNetworkConnectionException()

        every { apiKeyRepo.get() } returns flowOf(ApiKeyRepository.GetResult.Success(apiKey))
        coEvery {
            comicVineService.issues(
                apiKey = apiKey,
                offset = 0,
                limit = 100,
                sort = null,
                filter = null,
            )
        } returns ApiResponse.Failure.Exception(exception)

        val res = repo.getItems(
            offset = 0,
            limit = 100,
            sort = null,
            filters = emptyList(),
        )
        assertEquals(
            ComicVineResult.Failed.NoNetworkConnection,
            res
        )

        verify { apiKeyRepo.get() }
        coVerify {
            comicVineService.issues(
                apiKey = apiKey,
                offset = 0,
                limit = 100,
                sort = null,
                filter = null,
            )
        }
        confirmVerified(apiKeyRepo, comicVineService)
    }

    @Test
    fun getIssueById() = runTest {
        val apiKey = "123"
        val id = 1
        val response = IssueResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 1,
            offset = 0,
            numberOfPageResults = 1,
            numberOfTotalResults = 1,
            results = issueDetails,
        )

        every { apiKeyRepo.get() } returns flowOf(ApiKeyRepository.GetResult.Success(apiKey))
        coEvery {
            comicVineService.issue(
                id = id,
                apiKey = apiKey,
            )
        } returns ApiResponse.Success(response)

        val res = repo.getItemDetailsById(id)
        assertEquals(ComicVineResult.Success(response), res)

        verify { apiKeyRepo.get() }
        coVerify {
            comicVineService.issue(
                id = id,
                apiKey = apiKey,
            )
        }
        confirmVerified(apiKeyRepo, comicVineService)
    }

    @Test
    fun `getIssueById API key error`() = runTest {
        val error = ApiKeyRepository.GetResult.Failed.NoApiKey

        every { apiKeyRepo.get() } returns flowOf(error)

        val res = repo.getItemDetailsById(1)
        assertEquals(
            ComicVineResult.Failed.ApiKeyError(error),
            res
        )

        verify { apiKeyRepo.get() }
        confirmVerified(apiKeyRepo)
    }
}