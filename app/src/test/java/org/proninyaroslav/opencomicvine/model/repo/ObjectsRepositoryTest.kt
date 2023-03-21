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
import org.proninyaroslav.opencomicvine.data.ObjectInfo
import org.proninyaroslav.opencomicvine.data.ObjectsResponse
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.filter.ObjectsFilter
import org.proninyaroslav.opencomicvine.model.network.ComicVineService
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ObjectsRepositoryTest {
    lateinit var repo: ObjectsRepository

    @MockK
    lateinit var apiKeyRepo: ApiKeyRepository

    @MockK
    lateinit var comicVineService: ComicVineService

    @MockK
    lateinit var objectsList: List<ObjectInfo>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        repo = ObjectsRepositoryImpl(comicVineService, apiKeyRepo)
    }

    @Test
    fun getObjectsList() = runTest {
        val apiKey = "123"
        val response = ObjectsResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 100,
            offset = 0,
            numberOfPageResults = 2,
            numberOfTotalResults = 2,
            results = objectsList,
        )
        val filters = listOf(ObjectsFilter.Id(listOf(1)))

        every { apiKeyRepo.get() } returns flowOf(ApiKeyRepository.GetResult.Success(apiKey))
        coEvery {
            comicVineService.objects(
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
            comicVineService.objects(
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
    fun `getObjectsList API key error`() = runTest {
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