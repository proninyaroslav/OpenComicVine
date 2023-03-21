package org.proninyaroslav.opencomicvine.model.repo

import com.skydoves.sandwich.ApiResponse
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.ByteString
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.data.*
import org.proninyaroslav.opencomicvine.data.filter.VolumesFilter
import org.proninyaroslav.opencomicvine.data.sort.ComicVineSortDirection
import org.proninyaroslav.opencomicvine.data.sort.VolumesSort
import org.proninyaroslav.opencomicvine.model.network.ComicVineService
import org.proninyaroslav.opencomicvine.model.network.NoNetworkConnectionException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class VolumesRepositoryTest {
    lateinit var repo: VolumesRepository

    @MockK
    lateinit var apiKeyRepo: ApiKeyRepository

    @MockK
    lateinit var comicVineService: ComicVineService

    @MockK
    lateinit var volumesList: List<VolumeInfo>

    @MockK
    lateinit var volumeDetails: VolumeDetails

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        repo = VolumesRepositoryImpl(comicVineService, apiKeyRepo)
    }

    @Test
    fun getVolumesList() = runTest {
        val apiKey = "123"
        val response = VolumesResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 100,
            offset = 0,
            numberOfPageResults = 2,
            numberOfTotalResults = 2,
            results = volumesList,
        )
        val sort = VolumesSort.Name(ComicVineSortDirection.Asc)
        val filters = listOf(VolumesFilter.Name("test"))

        every { apiKeyRepo.get() } returns flowOf(ApiKeyRepository.GetResult.Success(apiKey))
        coEvery {
            comicVineService.volumes(
                apiKey = apiKey,
                offset = response.offset,
                limit = response.limit,
                sort = sort,
                filter = filters,
            )
        } returns ApiResponse.Success(Response.success(response))

        val res = repo.getItems(
            offset = response.offset,
            limit = response.limit,
            sort = sort,
            filters = filters,
        )
        assertEquals(ComicVineResult.Success(response), res)

        verify { apiKeyRepo.get() }
        coVerify {
            comicVineService.volumes(
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
            comicVineService.volumes(
                apiKey = apiKey,
                offset = 0,
                limit = 100,
                sort = null,
                filter = null,
            )
        } returns ApiResponse.Failure.Error(
            Response.error(
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
            comicVineService.volumes(
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
            comicVineService.volumes(
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
            comicVineService.volumes(
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
            comicVineService.volumes(
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
            comicVineService.volumes(
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
    fun getVolumeById() = runTest {
        val apiKey = "123"
        val id = 1
        val response = VolumeResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 1,
            offset = 0,
            numberOfPageResults = 1,
            numberOfTotalResults = 1,
            results = volumeDetails,
        )

        every { apiKeyRepo.get() } returns flowOf(ApiKeyRepository.GetResult.Success(apiKey))
        coEvery {
            comicVineService.volume(
                id = id,
                apiKey = apiKey,
            )
        } returns ApiResponse.Success(Response.success(response))

        val res = repo.getItemDetailsById(id)
        assertEquals(ComicVineResult.Success(response), res)

        verify { apiKeyRepo.get() }
        coVerify {
            comicVineService.volume(
                id = id,
                apiKey = apiKey,
            )
        }
        confirmVerified(apiKeyRepo, comicVineService)
    }

    @Test
    fun `getVolumeById API key error`() = runTest {
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