package org.proninyaroslav.opencomicvine.model.paging.recent

import androidx.paging.*
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.types.IssueInfo
import org.proninyaroslav.opencomicvine.types.IssuesResponse
import org.proninyaroslav.opencomicvine.types.StatusCode
import org.proninyaroslav.opencomicvine.types.filter.IssuesFilter
import org.proninyaroslav.opencomicvine.types.paging.recent.PagingRecentIssueItem
import org.proninyaroslav.opencomicvine.types.paging.recent.RecentIssueItemRemoteKeys
import org.proninyaroslav.opencomicvine.types.preferences.*
import org.proninyaroslav.opencomicvine.types.sort.ComicVineSortDirection
import org.proninyaroslav.opencomicvine.types.sort.IssuesSort
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.getNextWeekFromCurrentDay
import org.proninyaroslav.opencomicvine.model.paging.ComicVineRemoteMediator
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.IssuesRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingIssueRepository

@OptIn(ExperimentalCoroutinesApi::class)
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
            val character = PagingRecentIssueItem(
                index = i,
                info = info,
            )
            character
        }
        val remoteKeysList = itemsList.map { character ->
            RecentIssueItemRemoteKeys(
                id = character.index,
                prevOffset = null,
                nextOffset = pageSize,
            )
        }
        val pagingState = PagingState<Int, PagingRecentIssueItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )
        val filter = PrefRecentIssuesFilterBundle(
            dateAdded = PrefRecentIssuesFilter.DateAdded.Unknown,
            storeDate = PrefRecentIssuesFilter.StoreDate.NextWeek,
        )
        val comicVineFilter = getNextWeekFromCurrentDay().run {
            IssuesFilter.StoreDate(
                start = first,
                end = second,
            )
        }
        val sort = PrefRecentIssuesSort.StoreDate(
            direction = PrefSortDirection.Desc,
        )
        val comicVineSort = IssuesSort.StoreDate(direction = ComicVineSortDirection.Desc)

        every { response.statusCode } returns StatusCode.OK
        every { response.results } returns issuesList
        every { response.numberOfPageResults } returns pageSize
        coEvery {
            issuesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = comicVineSort,
                filters = listOf(comicVineFilter),
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
        every { pref.recentIssuesFilters } returns flowOf(filter)
        every { pref.recentIssuesSort } returns flowOf(sort)

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
                sort = comicVineSort,
                filters = listOf(comicVineFilter),
            )
        }
        coVerify {
            issueItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        }
        verify { pref.recentIssuesFilters }
        verify { pref.recentIssuesSort }
        confirmVerified(issuesRepo, issueItemRepo, response)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Load success result and endOfPaginationReached when no more data`() = runTest {
        val pageSize = 10
        val response = mockk<IssuesResponse>()
        val issuesList = emptyList<IssueInfo>()
        val itemsList = emptyList<PagingRecentIssueItem>()
        val remoteKeysList = emptyList<RecentIssueItemRemoteKeys>()
        val pagingState = PagingState<Int, PagingRecentIssueItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )
        val filter = PrefRecentIssuesFilterBundle(
            dateAdded = PrefRecentIssuesFilter.DateAdded.Unknown,
            storeDate = PrefRecentIssuesFilter.StoreDate.NextWeek,
        )
        val comicVineFilter = getNextWeekFromCurrentDay().run {
            IssuesFilter.StoreDate(
                start = first,
                end = second,
            )
        }
        val sort = PrefRecentIssuesSort.StoreDate(
            direction = PrefSortDirection.Desc,
        )
        val comicVineSort = IssuesSort.StoreDate(direction = ComicVineSortDirection.Desc)

        every { response.statusCode } returns StatusCode.OK
        every { response.results } returns issuesList
        every { response.numberOfPageResults } returns 0
        coEvery {
            issuesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = comicVineSort,
                filters = listOf(comicVineFilter),
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
        every { pref.recentIssuesFilters } returns flowOf(filter)
        every { pref.recentIssuesSort } returns flowOf(sort)

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
                sort = comicVineSort,
                filters = listOf(comicVineFilter),
            )
        }
        coVerify {
            issueItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        }
        verify { pref.recentIssuesFilters }
        verify { pref.recentIssuesSort }
        confirmVerified(issuesRepo, issueItemRepo, response)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Service error`() = runTest {
        val pageSize = 10
        val response = mockk<IssuesResponse>()
        val pagingState = PagingState<Int, PagingRecentIssueItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )
        val filter = PrefRecentIssuesFilterBundle(
            dateAdded = PrefRecentIssuesFilter.DateAdded.Unknown,
            storeDate = PrefRecentIssuesFilter.StoreDate.NextWeek,
        )
        val comicVineFilter = getNextWeekFromCurrentDay().run {
            IssuesFilter.StoreDate(
                start = first,
                end = second,
            )
        }
        val sort = PrefRecentIssuesSort.StoreDate(
            direction = PrefSortDirection.Desc,
        )
        val comicVineSort = IssuesSort.StoreDate(direction = ComicVineSortDirection.Desc)

        every { response.statusCode } returns StatusCode.InvalidAPIKey
        every { response.error } returns "Invalid API Key"
        coEvery {
            issuesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = comicVineSort,
                filters = listOf(comicVineFilter),
            )
        } returns ComicVineResult.Success(response)

        every { pref.recentIssuesFilters } returns flowOf(filter)
        every { pref.recentIssuesSort } returns flowOf(sort)

        val result = mediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Error)
        (result as RemoteMediator.MediatorResult.Error).run {
            val error = throwable as RecentEntityRemoteMediator.Error.Service
            assertEquals(StatusCode.InvalidAPIKey, error.statusCode)
            assertEquals("Invalid API Key", error.errorMessage)
        }

        verifyAll {
            response.statusCode
            response.error
        }
        verify { pref.recentIssuesFilters }
        verify { pref.recentIssuesSort }
        coVerify {
            issuesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = comicVineSort,
                filters = listOf(comicVineFilter),
            )
        }
        confirmVerified(issuesRepo, response)
    }

    @Test
    fun stateToError() {
        val error = RecentEntityRemoteMediator.Error.Service(
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
        val itemsList = emptyList<PagingRecentIssueItem>()
        val remoteKeysList = emptyList<RecentIssueItemRemoteKeys>()
        val pagingState = PagingState<Int, PagingRecentIssueItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )
        val filter = PrefRecentIssuesFilterBundle(
            dateAdded = PrefRecentIssuesFilter.DateAdded.Unknown,
            storeDate = PrefRecentIssuesFilter.StoreDate.NextWeek,
        )
        val comicVineFilter = getNextWeekFromCurrentDay().run {
            IssuesFilter.StoreDate(
                start = first,
                end = second,
            )
        }
        val sort = PrefRecentIssuesSort.StoreDate(
            direction = PrefSortDirection.Desc,
        )
        val comicVineSort = IssuesSort.StoreDate(direction = ComicVineSortDirection.Desc)

        every { response.statusCode } returns StatusCode.OK
        every { response.results } returns issuesList
        coEvery {
            issuesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = comicVineSort,
                filters = listOf(comicVineFilter),
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
        every { pref.recentIssuesFilters } returns flowOf(filter)
        every { pref.recentIssuesSort } returns flowOf(sort)

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
                sort = comicVineSort,
                filters = listOf(comicVineFilter),
            )
        }
        coVerify {
            issueItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        }
        verify { pref.recentIssuesFilters }
        verify { pref.recentIssuesSort }
        confirmVerified(issuesRepo, issueItemRepo, response)
    }
}