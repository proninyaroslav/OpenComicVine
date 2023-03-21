package org.proninyaroslav.opencomicvine.model.paging.wiki

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
import org.proninyaroslav.opencomicvine.data.*
import org.proninyaroslav.opencomicvine.data.filter.VolumesFilter
import org.proninyaroslav.opencomicvine.data.paging.wiki.PagingWikiVolumeItem
import org.proninyaroslav.opencomicvine.data.paging.wiki.WikiVolumeItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.preferences.*
import org.proninyaroslav.opencomicvine.data.sort.ComicVineSortDirection
import org.proninyaroslav.opencomicvine.data.sort.VolumesSort
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.paging.ComicVineRemoteMediator
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.VolumesRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingVolumeRepository

@OptIn(ExperimentalCoroutinesApi::class)
class VolumesRemoteMediatorTest {
    lateinit var mediator: VolumesRemoteMediator

    @MockK
    lateinit var volumesRepo: VolumesRepository

    @MockK
    lateinit var volumeItemRepo: PagingVolumeRepository

    @MockK
    lateinit var pref: AppPreferences

    val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mediator =
            VolumesRemoteMediator(
                endOfPaginationOffset = null,
                volumesRepo,
                volumeItemRepo,
                pref,
                dispatcher,
            )
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Load success result when more data is present`() = runTest {
        val pageSize = 10
        val response = mockk<VolumesResponse>()
        val volumesList = List(pageSize) { mockk<VolumeInfo>() }
        val itemsList = volumesList.mapIndexed { i, info ->
            val volume = PagingWikiVolumeItem(
                index = i,
                info = info,
            )
            volume
        }
        val remoteKeysList = itemsList.map { volume ->
            WikiVolumeItemRemoteKeys(
                id = volume.index,
                prevOffset = null,
                nextOffset = pageSize,
            )
        }
        val pagingState = PagingState<Int, PagingWikiVolumeItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )
        val sort = PrefWikiVolumesSort.Alphabetical(
            direction = PrefSortDirection.Desc,
        )
        val filter = PrefWikiVolumesFilterBundle(
            name = PrefWikiVolumesFilter.Name.Contains("test"),
            dateAdded = PrefWikiVolumesFilter.DateAdded.Unknown,
            dateLastUpdated = PrefWikiVolumesFilter.DateLastUpdated.Unknown,
        )

        every { response.statusCode } returns StatusCode.OK
        every { response.results } returns volumesList
        every { response.numberOfPageResults } returns pageSize
        coEvery {
            volumesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = VolumesSort.Name(ComicVineSortDirection.Desc),
                filters = listOf(VolumesFilter.Name("test")),
            )
        } returns ComicVineResult.Success(response)
        coEvery {
            volumeItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        } returns ComicVinePagingRepository.Result.Success(Unit)
        itemsList.forEach { volume ->
            coEvery {
                volumeItemRepo.getRemoteKeysById(volume.index)
            } returns ComicVinePagingRepository.Result.Success(remoteKeysList[volume.index])
        }
        every { pref.wikiVolumesSort } returns flowOf(sort)
        every { pref.wikiVolumesFilters } returns flowOf(filter)

        val result = mediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        verifyAll {
            response.statusCode
            response.results
            response.numberOfPageResults
        }
        coVerify {
            volumesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = VolumesSort.Name(ComicVineSortDirection.Desc),
                filters = listOf(VolumesFilter.Name("test")),
            )
        }
        coVerify {
            volumeItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        }
        verify { pref.wikiVolumesSort }
        verify { pref.wikiVolumesFilters }
        confirmVerified(volumesRepo, volumeItemRepo, response)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Load success result and endOfPaginationReached when no more data`() = runTest {
        val pageSize = 10
        val response = mockk<VolumesResponse>()
        val volumesList = emptyList<VolumeInfo>()
        val itemsList = emptyList<PagingWikiVolumeItem>()
        val remoteKeysList = emptyList<WikiVolumeItemRemoteKeys>()
        val pagingState = PagingState<Int, PagingWikiVolumeItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )
        val sort = PrefWikiVolumesSort.Alphabetical(
            direction = PrefSortDirection.Desc,
        )
        val filter = PrefWikiVolumesFilterBundle(
            name = PrefWikiVolumesFilter.Name.Contains("test"),
            dateAdded = PrefWikiVolumesFilter.DateAdded.Unknown,
            dateLastUpdated = PrefWikiVolumesFilter.DateLastUpdated.Unknown,
        )

        every { response.statusCode } returns StatusCode.OK
        every { response.results } returns volumesList
        every { response.numberOfPageResults } returns 0
        coEvery {
            volumesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = VolumesSort.Name(ComicVineSortDirection.Desc),
                filters = listOf(VolumesFilter.Name("test")),
            )
        } returns ComicVineResult.Success(response)
        coEvery {
            volumeItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        } returns ComicVinePagingRepository.Result.Success(Unit)
        volumesList.forEach { volume ->
            coEvery {
                volumeItemRepo.getRemoteKeysById(volume.id)
            } returns ComicVinePagingRepository.Result.Success(null)
        }
        every { pref.wikiVolumesSort } returns flowOf(sort)
        every { pref.wikiVolumesFilters } returns flowOf(filter)

        val result = mediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        verifyAll {
            response.statusCode
            response.results
            response.numberOfPageResults
        }
        coVerify {
            volumesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = VolumesSort.Name(ComicVineSortDirection.Desc),
                filters = listOf(VolumesFilter.Name("test")),
            )
        }
        coVerify {
            volumeItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        }
        verify { pref.wikiVolumesSort }
        verify { pref.wikiVolumesFilters }
        confirmVerified(volumesRepo, volumeItemRepo, response)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Service error`() = runTest {
        val pageSize = 10
        val response = mockk<VolumesResponse>()
        val pagingState = PagingState<Int, PagingWikiVolumeItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )
        val sort = PrefWikiVolumesSort.Alphabetical(
            direction = PrefSortDirection.Desc,
        )
        val filter = PrefWikiVolumesFilterBundle(
            name = PrefWikiVolumesFilter.Name.Contains("test"),
            dateAdded = PrefWikiVolumesFilter.DateAdded.Unknown,
            dateLastUpdated = PrefWikiVolumesFilter.DateLastUpdated.Unknown,
        )

        every { response.statusCode } returns StatusCode.InvalidAPIKey
        every { response.error } returns "Invalid API Key"
        coEvery {
            volumesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = VolumesSort.Name(ComicVineSortDirection.Desc),
                filters = listOf(VolumesFilter.Name("test")),
            )
        } returns ComicVineResult.Success(response)

        every { pref.wikiVolumesSort } returns flowOf(sort)
        every { pref.wikiVolumesFilters } returns flowOf(filter)

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
        verify { pref.wikiVolumesSort }
        verify { pref.wikiVolumesFilters }
        coVerify {
            volumesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = VolumesSort.Name(ComicVineSortDirection.Desc),
                filters = listOf(VolumesFilter.Name("test")),
            )
        }
        confirmVerified(volumesRepo, response)
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
            VolumesRemoteMediator(
                endOfPaginationOffset = pageSize - 1,
                volumesRepo,
                volumeItemRepo,
                pref,
                dispatcher,
            )
        val response = mockk<VolumesResponse>()
        val volumesList = emptyList<VolumeInfo>()
        val itemsList = emptyList<PagingWikiVolumeItem>()
        val remoteKeysList = emptyList<WikiVolumeItemRemoteKeys>()
        val pagingState = PagingState<Int, PagingWikiVolumeItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )
        val sort = PrefWikiVolumesSort.Alphabetical(
            direction = PrefSortDirection.Desc,
        )
        val filter = PrefWikiVolumesFilterBundle(
            name = PrefWikiVolumesFilter.Name.Contains("test"),
            dateAdded = PrefWikiVolumesFilter.DateAdded.Unknown,
            dateLastUpdated = PrefWikiVolumesFilter.DateLastUpdated.Unknown,
        )

        every { response.statusCode } returns StatusCode.OK
        every { response.results } returns volumesList
        coEvery {
            volumesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = VolumesSort.Name(ComicVineSortDirection.Desc),
                filters = listOf(VolumesFilter.Name("test")),
            )
        } returns ComicVineResult.Success(response)
        coEvery {
            volumeItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        } returns ComicVinePagingRepository.Result.Success(Unit)
        volumesList.forEach { volume ->
            coEvery {
                volumeItemRepo.getRemoteKeysById(volume.id)
            } returns ComicVinePagingRepository.Result.Success(null)
        }
        every { pref.wikiVolumesSort } returns flowOf(sort)
        every { pref.wikiVolumesFilters } returns flowOf(filter)

        val result = mediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        verifyAll {
            response.statusCode
            response.results
        }
        coVerify {
            volumesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = VolumesSort.Name(ComicVineSortDirection.Desc),
                filters = listOf(VolumesFilter.Name("test")),
            )
        }
        coVerify {
            volumeItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        }
        verify { pref.wikiVolumesSort }
        verify { pref.wikiVolumesFilters }
        confirmVerified(volumesRepo, volumeItemRepo, response)
    }
}