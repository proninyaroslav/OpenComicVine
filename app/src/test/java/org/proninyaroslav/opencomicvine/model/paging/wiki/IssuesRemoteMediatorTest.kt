package org.proninyaroslav.opencomicvine.model.paging.wiki

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.paging.ComicVineRemoteMediator
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.IssuesRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingIssueRepository
import org.proninyaroslav.opencomicvine.types.IssueInfo
import org.proninyaroslav.opencomicvine.types.IssuesResponse
import org.proninyaroslav.opencomicvine.types.StatusCode
import org.proninyaroslav.opencomicvine.types.filter.IssuesFilter
import org.proninyaroslav.opencomicvine.types.paging.wiki.PagingWikiIssueItem
import org.proninyaroslav.opencomicvine.types.paging.wiki.WikiIssueItemRemoteKeys
import org.proninyaroslav.opencomicvine.types.preferences.PrefSortDirection
import org.proninyaroslav.opencomicvine.types.preferences.PrefWikiIssuesFilter
import org.proninyaroslav.opencomicvine.types.preferences.PrefWikiIssuesFilterBundle
import org.proninyaroslav.opencomicvine.types.preferences.PrefWikiIssuesSort
import org.proninyaroslav.opencomicvine.types.sort.ComicVineSortDirection
import org.proninyaroslav.opencomicvine.types.sort.IssuesSort

class IssuesRemoteMediatorTest {
    lateinit var mediator: IssuesRemoteMediator

    @MockK
    lateinit var issuesRepo: IssuesRepository

    @MockK
    lateinit var issueItemRepo: PagingIssueRepository

    @MockK
    lateinit var pref: AppPreferences

    val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mediator =
            IssuesRemoteMediator(
                endOfPaginationOffset = null,
                issuesRepo,
                issueItemRepo,
                pref,
                dispatcher,
            )
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Load success result when more data is present`() = runTest {
        val pageSize = 10
        val response = mockk<IssuesResponse>()
        val issuesList = List(pageSize) { mockk<IssueInfo>() }
        val itemsList = issuesList.mapIndexed { i, info ->
            val character = PagingWikiIssueItem(
                index = i,
                info = info,
            )
            character
        }
        val remoteKeysList = itemsList.map { character ->
            WikiIssueItemRemoteKeys(
                id = character.index,
                prevOffset = null,
                nextOffset = pageSize,
            )
        }
        val pagingState = PagingState<Int, PagingWikiIssueItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )
        val sort = PrefWikiIssuesSort.Alphabetical(
            direction = PrefSortDirection.Asc,
        )
        val filter = PrefWikiIssuesFilterBundle(
            name = PrefWikiIssuesFilter.Name.Unknown,
            dateAdded = PrefWikiIssuesFilter.DateAdded.Unknown,
            dateLastUpdated = PrefWikiIssuesFilter.DateLastUpdated.Unknown,
            coverDate = PrefWikiIssuesFilter.CoverDate.Unknown,
            storeDate = PrefWikiIssuesFilter.StoreDate.Unknown,
            issueNumber = PrefWikiIssuesFilter.IssueNumber.Contains("1"),
        )

        every { response.statusCode } returns StatusCode.OK
        every { response.results } returns issuesList
        every { response.numberOfPageResults } returns pageSize
        coEvery {
            issuesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = IssuesSort.Name(ComicVineSortDirection.Asc),
                filters = listOf(IssuesFilter.IssueNumber("1")),
            )
        } returns ComicVineResult.Success(response)
        coEvery {
            issueItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        } returns ComicVinePagingRepository.Result.Success(Unit)
        itemsList.forEach { character ->
            coEvery {
                issueItemRepo.getRemoteKeysById(character.index)
            } returns ComicVinePagingRepository.Result.Success(remoteKeysList[character.index])
        }
        every { pref.wikiIssuesSort } returns flowOf(sort)
        every { pref.wikiIssuesFilters } returns flowOf(filter)

        val result = mediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        verifyAll {
            response.statusCode
            response.results
            response.numberOfPageResults
        }
        coVerify {
            issuesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = IssuesSort.Name(ComicVineSortDirection.Asc),
                filters = listOf(IssuesFilter.IssueNumber("1")),
            )
        }
        coVerify {
            issueItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        }
        verify { pref.wikiIssuesSort }
        verify { pref.wikiIssuesFilters }
        confirmVerified(issuesRepo, issueItemRepo, response)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Load success result and endOfPaginationReached when no more data`() = runTest {
        val pageSize = 10
        val response = mockk<IssuesResponse>()
        val issuesList = emptyList<IssueInfo>()
        val itemsList = emptyList<PagingWikiIssueItem>()
        val remoteKeysList = emptyList<WikiIssueItemRemoteKeys>()
        val pagingState = PagingState<Int, PagingWikiIssueItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )
        val sort = PrefWikiIssuesSort.Alphabetical(
            direction = PrefSortDirection.Asc,
        )
        val filter = PrefWikiIssuesFilterBundle(
            name = PrefWikiIssuesFilter.Name.Unknown,
            dateAdded = PrefWikiIssuesFilter.DateAdded.Unknown,
            dateLastUpdated = PrefWikiIssuesFilter.DateLastUpdated.Unknown,
            coverDate = PrefWikiIssuesFilter.CoverDate.Unknown,
            storeDate = PrefWikiIssuesFilter.StoreDate.Unknown,
            issueNumber = PrefWikiIssuesFilter.IssueNumber.Contains("1"),
        )

        every { response.statusCode } returns StatusCode.OK
        every { response.results } returns issuesList
        every { response.numberOfPageResults } returns 0
        coEvery {
            issuesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = IssuesSort.Name(ComicVineSortDirection.Asc),
                filters = listOf(IssuesFilter.IssueNumber("1")),
            )
        } returns ComicVineResult.Success(response)
        coEvery {
            issueItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        } returns ComicVinePagingRepository.Result.Success(Unit)
        issuesList.forEach { character ->
            coEvery {
                issueItemRepo.getRemoteKeysById(character.id)
            } returns ComicVinePagingRepository.Result.Success(null)
        }
        every { pref.wikiIssuesSort } returns flowOf(sort)
        every { pref.wikiIssuesFilters } returns flowOf(filter)

        val result = mediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        verifyAll {
            response.statusCode
            response.results
            response.numberOfPageResults
        }
        coVerify {
            issuesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = IssuesSort.Name(ComicVineSortDirection.Asc),
                filters = listOf(IssuesFilter.IssueNumber("1")),
            )
        }
        coVerify {
            issueItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        }
        verify { pref.wikiIssuesSort }
        verify { pref.wikiIssuesFilters }
        confirmVerified(issuesRepo, issueItemRepo, response)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Service error`() = runTest {
        val pageSize = 10
        val response = mockk<IssuesResponse>()
        val pagingState = PagingState<Int, PagingWikiIssueItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )
        val sort = PrefWikiIssuesSort.Alphabetical(
            direction = PrefSortDirection.Asc,
        )
        val filter = PrefWikiIssuesFilterBundle(
            name = PrefWikiIssuesFilter.Name.Unknown,
            dateAdded = PrefWikiIssuesFilter.DateAdded.Unknown,
            dateLastUpdated = PrefWikiIssuesFilter.DateLastUpdated.Unknown,
            coverDate = PrefWikiIssuesFilter.CoverDate.Unknown,
            storeDate = PrefWikiIssuesFilter.StoreDate.Unknown,
            issueNumber = PrefWikiIssuesFilter.IssueNumber.Contains("1"),
        )

        every { response.statusCode } returns StatusCode.InvalidAPIKey
        every { response.error } returns "Invalid API Key"
        coEvery {
            issuesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = IssuesSort.Name(ComicVineSortDirection.Asc),
                filters = listOf(IssuesFilter.IssueNumber("1")),
            )
        } returns ComicVineResult.Success(response)

        every { pref.wikiIssuesSort } returns flowOf(sort)
        every { pref.wikiIssuesFilters } returns flowOf(filter)

        val result = mediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Error)
        (result as RemoteMediator.MediatorResult.Error).run {
            val error = throwable as WikiEntityRemoteMediator.Error.Service
            assertEquals(StatusCode.InvalidAPIKey, error.statusCode)
            assertEquals("Invalid API Key", error.errorMessage)
        }

        verifyAll {
            response.statusCode
            response.error
        }
        verify { pref.wikiIssuesSort }
        verify { pref.wikiIssuesFilters }
        coVerify {
            issuesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = IssuesSort.Name(ComicVineSortDirection.Asc),
                filters = listOf(IssuesFilter.IssueNumber("1")),
            )
        }
        confirmVerified(issuesRepo, response)
    }

    @Test
    fun stateToError() {
        val error = WikiEntityRemoteMediator.Error.Service(
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
            IssuesRemoteMediator(
                endOfPaginationOffset = pageSize - 1,
                issuesRepo,
                issueItemRepo,
                pref,
                dispatcher,
            )
        val response = mockk<IssuesResponse>()
        val issuesList = emptyList<IssueInfo>()
        val itemsList = emptyList<PagingWikiIssueItem>()
        val remoteKeysList = emptyList<WikiIssueItemRemoteKeys>()
        val pagingState = PagingState<Int, PagingWikiIssueItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )
        val sort = PrefWikiIssuesSort.Alphabetical(
            direction = PrefSortDirection.Asc,
        )
        val filter = PrefWikiIssuesFilterBundle(
            name = PrefWikiIssuesFilter.Name.Unknown,
            dateAdded = PrefWikiIssuesFilter.DateAdded.Unknown,
            dateLastUpdated = PrefWikiIssuesFilter.DateLastUpdated.Unknown,
            coverDate = PrefWikiIssuesFilter.CoverDate.Unknown,
            storeDate = PrefWikiIssuesFilter.StoreDate.Unknown,
            issueNumber = PrefWikiIssuesFilter.IssueNumber.Contains("1"),
        )

        every { response.statusCode } returns StatusCode.OK
        every { response.results } returns issuesList
        coEvery {
            issuesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = IssuesSort.Name(ComicVineSortDirection.Asc),
                filters = listOf(IssuesFilter.IssueNumber("1")),
            )
        } returns ComicVineResult.Success(response)
        coEvery {
            issueItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        } returns ComicVinePagingRepository.Result.Success(Unit)
        issuesList.forEach { character ->
            coEvery {
                issueItemRepo.getRemoteKeysById(character.id)
            } returns ComicVinePagingRepository.Result.Success(null)
        }
        every { pref.wikiIssuesSort } returns flowOf(sort)
        every { pref.wikiIssuesFilters } returns flowOf(filter)

        val result = mediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        verifyAll {
            response.statusCode
            response.results
        }
        coVerify {
            issuesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = IssuesSort.Name(ComicVineSortDirection.Asc),
                filters = listOf(IssuesFilter.IssueNumber("1")),
            )
        }
        coVerify {
            issueItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        }
        verify { pref.wikiIssuesSort }
        verify { pref.wikiIssuesFilters }
        confirmVerified(issuesRepo, issueItemRepo, response)
    }
}