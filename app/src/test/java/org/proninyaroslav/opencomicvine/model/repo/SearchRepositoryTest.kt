package org.proninyaroslav.opencomicvine.model.repo

import com.skydoves.sandwich.ApiResponse
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.model.network.ComicVineService
import org.proninyaroslav.opencomicvine.types.ComicVineSearchResourceType
import org.proninyaroslav.opencomicvine.types.ComicVineSearchResourceTypeList
import org.proninyaroslav.opencomicvine.types.SearchInfo
import org.proninyaroslav.opencomicvine.types.SearchObjectsResponse
import org.proninyaroslav.opencomicvine.types.SearchResponse
import org.proninyaroslav.opencomicvine.types.SearchStoryArcsResponse
import org.proninyaroslav.opencomicvine.types.StatusCode
import org.proninyaroslav.opencomicvine.types.filter.ObjectsFilter
import org.proninyaroslav.opencomicvine.types.filter.StoryArcsFilter

@OptIn(ExperimentalCoroutinesApi::class)
class SearchRepositoryTest {
    lateinit var repo: SearchRepository

    @MockK
    lateinit var apiKeyRepo: ApiKeyRepository

    @MockK
    lateinit var comicVineService: ComicVineService

    val scope = TestScope(UnconfinedTestDispatcher())

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        repo = SearchRepositoryImpl(comicVineService, apiKeyRepo, scope)
    }

    @Test
    fun `Search all`() = runTest {
        val searchInfoList = List(4) {
            SearchInfo.Location(
                id = it,
                name = "test",
                descriptionShort = "test",
                image = mockk(),
            )
        }
        val storyArcsList = List(2) {
            SearchInfo.StoryArc(
                id = it,
                name = "test",
                descriptionShort = "test",
                image = mockk(),
                publisher = mockk(),
            )
        }
        val objectsList = List(2) {
            SearchInfo.Object(
                id = it,
                name = "test",
                descriptionShort = "test",
                image = mockk(),
            )
        }
        val totalItemsList = searchInfoList + storyArcsList + objectsList

        val apiKey = "123"
        val searchResponse = SearchResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 4,
            offset = 4,
            numberOfPageResults = searchInfoList.size,
            numberOfTotalResults = 8,
            results = searchInfoList,
        )
        val storyArcsResponse = SearchStoryArcsResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 4,
            offset = 4,
            numberOfPageResults = storyArcsList.size,
            numberOfTotalResults = 6,
            results = storyArcsList,
        )
        val objectsResponse = SearchObjectsResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 4,
            offset = 4,
            numberOfPageResults = objectsList.size,
            numberOfTotalResults = 6,
            results = objectsList,
        )
        val totalResponse = SearchResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 10,
            offset = 10,
            numberOfPageResults = totalItemsList.size,
            numberOfTotalResults = 20,
            results = totalItemsList,
        )
        val query = "test"

        every { apiKeyRepo.get() } returns flowOf(ApiKeyRepository.GetResult.Success(apiKey))
        coEvery {
            comicVineService.search(
                apiKey = apiKey,
                offset = 4,
                limit = 4,
                query = query,
                resources = null,
            )
        } returns ApiResponse.Success(searchResponse)
        coEvery {
            comicVineService.searchStoryArcs(
                apiKey = apiKey,
                offset = 4,
                limit = 4,
                filter = listOf(StoryArcsFilter.Name(query))
            )
        } returns ApiResponse.Success(storyArcsResponse)
        coEvery {
            comicVineService.searchObjects(
                apiKey = apiKey,
                offset = 4,
                limit = 4,
                filter = listOf(ObjectsFilter.Name(query))
            )
        } returns ApiResponse.Success(objectsResponse)

        val res = repo.search(
            offset = totalResponse.offset,
            limit = totalResponse.limit,
            query = query,
            resources = emptySet(),
        )
        assertEquals(ComicVineResult.Success(totalResponse), res)

        verify { apiKeyRepo.get() }
        coVerify {
            comicVineService.search(
                apiKey = apiKey,
                offset = 4,
                limit = 4,
                query = query,
                resources = null,
            )
        }
        coVerify {
            comicVineService.searchStoryArcs(
                apiKey = apiKey,
                offset = 4,
                limit = 4,
                filter = listOf(StoryArcsFilter.Name(query))
            )
        }
        coVerify {
            comicVineService.searchObjects(
                apiKey = apiKey,
                offset = 4,
                limit = 4,
                filter = listOf(ObjectsFilter.Name(query))
            )
        }
        confirmVerified(apiKeyRepo, comicVineService)
    }

    @Test
    fun `Search failed`() = runTest {
        val searchInfoList = List(4) {
            SearchInfo.Location(
                id = it,
                name = "test",
                descriptionShort = "test",
                image = mockk(),
            )
        }
        val storyArcsList = List(2) {
            SearchInfo.StoryArc(
                id = it,
                name = "test",
                descriptionShort = "test",
                image = mockk(),
                publisher = mockk(),
            )
        }

        val apiKey = "123"
        val searchResponse = SearchResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 4,
            offset = 4,
            numberOfPageResults = searchInfoList.size,
            numberOfTotalResults = 8,
            results = searchInfoList,
        )
        val storyArcsResponse = SearchStoryArcsResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 4,
            offset = 4,
            numberOfPageResults = storyArcsList.size,
            numberOfTotalResults = 6,
            results = storyArcsList,
        )
        val query = "test"

        every { apiKeyRepo.get() } returns flowOf(ApiKeyRepository.GetResult.Success(apiKey))
        coEvery {
            comicVineService.search(
                apiKey = apiKey,
                offset = 4,
                limit = 4,
                query = query,
                resources = null,
            )
        } returns ApiResponse.Success(searchResponse)
        coEvery {
            comicVineService.searchStoryArcs(
                apiKey = apiKey,
                offset = 4,
                limit = 4,
                filter = listOf(StoryArcsFilter.Name(query))
            )
        } returns ApiResponse.Success(storyArcsResponse)
        coEvery {
            comicVineService.searchObjects(
                apiKey = apiKey,
                offset = 4,
                limit = 4,
                filter = listOf(ObjectsFilter.Name(query))
            )
        } returns ApiResponse.Failure.Exception(IOException())

        val res = repo.search(
            offset = 10,
            limit = 10,
            query = query,
            resources = emptySet(),
        )
        assertTrue(res is ComicVineResult.Failed.Exception)

        verify { apiKeyRepo.get() }
        coVerify {
            comicVineService.search(
                apiKey = apiKey,
                offset = 4,
                limit = 4,
                query = query,
                resources = null,
            )
        }
        coVerify {
            comicVineService.searchStoryArcs(
                apiKey = apiKey,
                offset = 4,
                limit = 4,
                filter = listOf(StoryArcsFilter.Name(query))
            )
        }
        coVerify {
            comicVineService.searchObjects(
                apiKey = apiKey,
                offset = 4,
                limit = 4,
                filter = listOf(ObjectsFilter.Name(query))
            )
        }
        confirmVerified(apiKeyRepo, comicVineService)
    }

    @Test
    fun `Search some resources`() = runTest {
        val searchInfoList = List(4) {
            SearchInfo.Location(
                id = it,
                name = "test",
                descriptionShort = "test",
                image = mockk(),
            )
        }

        val apiKey = "123"
        val searchResponse = SearchResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 10,
            offset = 10,
            numberOfPageResults = searchInfoList.size,
            numberOfTotalResults = 8,
            results = searchInfoList,
        )
        val totalResponse = SearchResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 10,
            offset = 10,
            numberOfPageResults = searchInfoList.size,
            numberOfTotalResults = 8,
            results = searchInfoList,
        )
        val query = "test"

        every { apiKeyRepo.get() } returns flowOf(ApiKeyRepository.GetResult.Success(apiKey))
        coEvery {
            comicVineService.search(
                apiKey = apiKey,
                offset = 10,
                limit = 10,
                query = query,
                resources = ComicVineSearchResourceTypeList(
                    listOf(ComicVineSearchResourceType.Location)
                ),
            )
        } returns ApiResponse.Success(searchResponse)

        val res = repo.search(
            offset = totalResponse.offset,
            limit = totalResponse.limit,
            query = query,
            resources = setOf(ComicVineSearchResourceType.Location),
        )
        assertEquals(ComicVineResult.Success(totalResponse), res)

        verify { apiKeyRepo.get() }
        coVerify {
            comicVineService.search(
                apiKey = apiKey,
                offset = 10,
                limit = 10,
                query = query,
                resources = ComicVineSearchResourceTypeList(
                    listOf(ComicVineSearchResourceType.Location)
                ),
            )
        }
        confirmVerified(apiKeyRepo, comicVineService)
    }

    @Test
    fun `Search all except story arcs`() = runTest {
        val searchInfoList = List(4) {
            SearchInfo.Location(
                id = it,
                name = "test",
                descriptionShort = "test",
                image = mockk(),
            )
        }
        val objectsList = List(2) {
            SearchInfo.Object(
                id = it,
                name = "test",
                descriptionShort = "test",
                image = mockk(),
            )
        }
        val totalItemsList = searchInfoList + objectsList

        val apiKey = "123"
        val searchResponse = SearchResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 5,
            offset = 5,
            numberOfPageResults = searchInfoList.size,
            numberOfTotalResults = 10,
            results = searchInfoList,
        )
        val objectsResponse = SearchObjectsResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 5,
            offset = 5,
            numberOfPageResults = objectsList.size,
            numberOfTotalResults = 10,
            results = objectsList,
        )
        val totalResponse = SearchResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 10,
            offset = 10,
            numberOfPageResults = totalItemsList.size,
            numberOfTotalResults = 20,
            results = totalItemsList,
        )
        val query = "test"

        every { apiKeyRepo.get() } returns flowOf(ApiKeyRepository.GetResult.Success(apiKey))
        coEvery {
            comicVineService.search(
                apiKey = apiKey,
                offset = 5,
                limit = 5,
                query = query,
                resources = ComicVineSearchResourceTypeList(
                    listOf(
                        ComicVineSearchResourceType.Location,
                        ComicVineSearchResourceType.Object,
                    )
                ),
            )
        } returns ApiResponse.Success(searchResponse)
        coEvery {
            comicVineService.searchObjects(
                apiKey = apiKey,
                offset = 5,
                limit = 5,
                filter = listOf(ObjectsFilter.Name(query))
            )
        } returns ApiResponse.Success(objectsResponse)

        val res = repo.search(
            offset = totalResponse.offset,
            limit = totalResponse.limit,
            query = query,
            resources = setOf(
                ComicVineSearchResourceType.Location,
                ComicVineSearchResourceType.Object,
            ),
        )
        assertEquals(ComicVineResult.Success(totalResponse), res)

        verify { apiKeyRepo.get() }
        coVerify {
            comicVineService.search(
                apiKey = apiKey,
                offset = 5,
                limit = 5,
                query = query,
                resources = ComicVineSearchResourceTypeList(
                    listOf(
                        ComicVineSearchResourceType.Location,
                        ComicVineSearchResourceType.Object,
                    )
                ),
            )
        }
        coVerify {
            comicVineService.searchObjects(
                apiKey = apiKey,
                offset = 5,
                limit = 5,
                filter = listOf(ObjectsFilter.Name(query))
            )
        }
        confirmVerified(apiKeyRepo, comicVineService)
    }

    @Test
    fun `Search story arcs and objects only`() = runTest {
        val storyArcsList = List(4) {
            SearchInfo.StoryArc(
                id = it,
                name = "test",
                descriptionShort = "test",
                image = mockk(),
                publisher = mockk(),
            )
        }
        val objectsList = List(2) {
            SearchInfo.Object(
                id = it,
                name = "test",
                descriptionShort = "test",
                image = mockk(),
            )
        }
        val totalItemsList = storyArcsList + objectsList

        val apiKey = "123"
        val storyArcResponse = SearchStoryArcsResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 5,
            offset = 5,
            numberOfPageResults = storyArcsList.size,
            numberOfTotalResults = 10,
            results = storyArcsList,
        )
        val objectsResponse = SearchObjectsResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 5,
            offset = 5,
            numberOfPageResults = objectsList.size,
            numberOfTotalResults = 10,
            results = objectsList,
        )
        val totalResponse = SearchResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 10,
            offset = 10,
            numberOfPageResults = totalItemsList.size,
            numberOfTotalResults = 20,
            results = totalItemsList,
        )
        val query = "test"

        every { apiKeyRepo.get() } returns flowOf(ApiKeyRepository.GetResult.Success(apiKey))
        coEvery {
            comicVineService.searchStoryArcs(
                apiKey = apiKey,
                offset = 5,
                limit = 5,
                filter = listOf(StoryArcsFilter.Name(query)),
            )
        } returns ApiResponse.Success(storyArcResponse)
        coEvery {
            comicVineService.searchObjects(
                apiKey = apiKey,
                offset = 5,
                limit = 5,
                filter = listOf(ObjectsFilter.Name(query))
            )
        } returns ApiResponse.Success(objectsResponse)

        val res = repo.search(
            offset = totalResponse.offset,
            limit = totalResponse.limit,
            query = query,
            resources = setOf(
                ComicVineSearchResourceType.StoryArc,
                ComicVineSearchResourceType.Object,
            ),
        )
        assertEquals(ComicVineResult.Success(totalResponse), res)

        verify { apiKeyRepo.get() }
        coVerify {
            comicVineService.searchStoryArcs(
                apiKey = apiKey,
                offset = 5,
                limit = 5,
                filter = listOf(StoryArcsFilter.Name(query)),
            )
        }
        coVerify {
            comicVineService.searchObjects(
                apiKey = apiKey,
                offset = 5,
                limit = 5,
                filter = listOf(ObjectsFilter.Name(query))
            )
        }
        confirmVerified(apiKeyRepo, comicVineService)
    }
}