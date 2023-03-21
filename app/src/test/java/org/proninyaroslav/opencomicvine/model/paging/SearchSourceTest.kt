package org.proninyaroslav.opencomicvine.model.paging

import androidx.paging.PagingSource
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.SearchInfo
import org.proninyaroslav.opencomicvine.data.SearchResponse
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.data.preferences.PrefSearchFilter
import org.proninyaroslav.opencomicvine.data.preferences.PrefSearchFilterBundle
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoriteFetchResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.SearchRepository

@OptIn(ExperimentalCoroutinesApi::class)
class SearchSourceTest {
    lateinit var source: SearchSource

    @MockK
    lateinit var searchRepo: SearchRepository

    @MockK
    lateinit var favoritesRepo: FavoritesRepository

    @MockK
    lateinit var pref: AppPreferences

    private val queryState = flowOf(SearchSource.Query.Value("test"))

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        source = SearchSource(
            queryState,
            searchRepo,
            favoritesRepo,
            pref,
        )
    }

    @Test
    fun `Load success result when more data is present`() = runTest {
        val pageSize = 5
        val response = mockk<SearchResponse>()
        val searchInfoList = List(pageSize) {
            val info = mockk<SearchInfo>()
            every { info.id } returns it
            coEvery { favoritesRepo.observe(it, FavoriteInfo.EntityType.Character) } returns
                    flowOf(FavoriteFetchResult.Success(isFavorite = true))
            info
        }

        every { response.statusCode } returns StatusCode.OK
        every { response.results } returns searchInfoList
        every { response.error } returns ""
        every { response.offset } returns 0
        every { response.limit } returns pageSize
        every { response.numberOfTotalResults } returns pageSize
        every { response.numberOfPageResults } returns pageSize
        coEvery {
            searchRepo.search(
                offset = 0,
                limit = pageSize,
                query = queryState.first().query,
                resources = emptySet(),
            )
        } returns ComicVineResult.Success(response)
        coEvery { pref.searchFilter } returns
                flowOf(PrefSearchFilterBundle(resources = PrefSearchFilter.Resources.Unknown))

        val result = source.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = pageSize,
                placeholdersEnabled = false,
            )
        )
        Assert.assertTrue(result is PagingSource.LoadResult.Page)
        Assert.assertNotNull((result as PagingSource.LoadResult.Page).nextKey)

        verifyAll {
            response.statusCode
            response.results
            response.error
            response.offset
            response.limit
            response.numberOfTotalResults
            response.numberOfPageResults
        }
        coVerify {
            searchRepo.search(
                offset = 0,
                limit = pageSize,
                query = queryState.first().query,
                resources = emptySet(),
            )
        }
        coVerify { pref.searchFilter }
        confirmVerified(searchRepo, pref, response)
    }

    @Test
    fun `Load success result and endOfPaginationReached when no more data`() = runTest {
        val pageSize = 10
        val response = mockk<SearchResponse>()
        val searchList = emptyList<SearchInfo>()

        every { response.statusCode } returns StatusCode.OK
        every { response.error } returns ""
        every { response.offset } returns 0
        every { response.limit } returns pageSize
        every { response.results } returns searchList
        every { response.numberOfTotalResults } returns pageSize
        every { response.numberOfPageResults } returns pageSize
        coEvery {
            searchRepo.search(
                offset = 0,
                limit = pageSize,
                query = queryState.first().query,
                resources = emptySet(),
            )
        } returns ComicVineResult.Success(response)
        coEvery { pref.searchFilter } returns
                flowOf(PrefSearchFilterBundle(resources = PrefSearchFilter.Resources.Unknown))

        val result = source.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = pageSize,
                placeholdersEnabled = false,
            )
        )
        Assert.assertTrue(result is PagingSource.LoadResult.Page)
        Assert.assertNull((result as PagingSource.LoadResult.Page).nextKey)

        verifyAll {
            response.statusCode
            response.results
            response.error
            response.offset
            response.limit
            response.numberOfTotalResults
            response.numberOfPageResults
        }
        coVerify {
            searchRepo.search(
                offset = 0,
                limit = pageSize,
                query = queryState.first().query,
                resources = emptySet(),
            )
        }
        coVerify { pref.searchFilter }
        confirmVerified(searchRepo, pref, response)
    }

    @Test
    fun `Service error`() = runTest {
        val pageSize = 10
        val response = mockk<SearchResponse>()

        every { response.statusCode } returns StatusCode.InvalidAPIKey
        every { response.error } returns "Invalid API Key"
        coEvery {
            searchRepo.search(
                offset = 0,
                limit = pageSize,
                query = queryState.first().query,
                resources = emptySet(),
            )
        } returns ComicVineResult.Success(response)
        coEvery { pref.searchFilter } returns
                flowOf(PrefSearchFilterBundle(resources = PrefSearchFilter.Resources.Unknown))

        val result = source.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = pageSize,
                placeholdersEnabled = false,
            )
        )
        Assert.assertTrue(result is PagingSource.LoadResult.Error)
        (result as PagingSource.LoadResult.Error).run {
            val error = throwable as SearchSource.Error.Service
            Assert.assertEquals(StatusCode.InvalidAPIKey, error.statusCode)
            Assert.assertEquals("Invalid API Key", error.errorMessage)
        }

        verifyAll {
            response.statusCode
            response.error
        }
        coVerify {
            searchRepo.search(
                offset = 0,
                limit = pageSize,
                query = queryState.first().query,
                resources = emptySet(),
            )
        }
        coVerify { pref.searchFilter }
        confirmVerified(searchRepo, response)
    }
}