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
import org.proninyaroslav.opencomicvine.data.*
import org.proninyaroslav.opencomicvine.data.filter.CharactersFilter
import org.proninyaroslav.opencomicvine.data.sort.CharactersSort
import org.proninyaroslav.opencomicvine.data.sort.ComicVineSortDirection
import org.proninyaroslav.opencomicvine.model.network.ComicVineService
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class CharactersRepositoryTest {
    lateinit var repo: CharactersRepository

    @MockK
    lateinit var apiKeyRepo: ApiKeyRepository

    @MockK
    lateinit var comicVineService: ComicVineService

    @MockK
    lateinit var charactersList: List<CharacterInfo>

    @MockK
    lateinit var characterDetails: CharacterDetails

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        repo = CharactersRepositoryImpl(comicVineService, apiKeyRepo)
    }

    @Test
    fun getCharactersList() = runTest {
        val apiKey = "123"
        val response = CharactersResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 100,
            offset = 0,
            numberOfPageResults = 2,
            numberOfTotalResults = 2,
            results = charactersList,
        )
        val sort = CharactersSort.Name(ComicVineSortDirection.Asc)
        val filters = listOf(CharactersFilter.Gender.Male)

        every { apiKeyRepo.get() } returns flowOf(ApiKeyRepository.GetResult.Success(apiKey))
        coEvery {
            comicVineService.characters(
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
            comicVineService.characters(
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
    fun `getCharactersList API key error`() = runTest {
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
    fun getCharacterById() = runTest {
        val apiKey = "123"
        val id = 1
        val response = CharacterResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 1,
            offset = 0,
            numberOfPageResults = 1,
            numberOfTotalResults = 1,
            results = characterDetails,
        )

        every { apiKeyRepo.get() } returns flowOf(ApiKeyRepository.GetResult.Success(apiKey))
        coEvery {
            comicVineService.character(
                id = id,
                apiKey = apiKey,
            )
        } returns ApiResponse.Success(Response.success(response))

        val res = repo.getItemDetailsById(id)
        assertEquals(ComicVineResult.Success(response), res)

        verify { apiKeyRepo.get() }
        coVerify {
            comicVineService.character(
                id = id,
                apiKey = apiKey,
            )
        }
        confirmVerified(apiKeyRepo, comicVineService)
    }

    @Test
    fun `getCharacterById API key error`() = runTest {
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