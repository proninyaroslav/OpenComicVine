package org.proninyaroslav.opencomicvine.model.paging.recent

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
import org.proninyaroslav.opencomicvine.model.repo.VolumesRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingVolumeRepository
import org.proninyaroslav.opencomicvine.types.StatusCode
import org.proninyaroslav.opencomicvine.types.VolumeInfo
import org.proninyaroslav.opencomicvine.types.VolumesResponse
import org.proninyaroslav.opencomicvine.types.paging.recent.PagingRecentVolumeItem
import org.proninyaroslav.opencomicvine.types.paging.recent.RecentVolumeItemRemoteKeys
import org.proninyaroslav.opencomicvine.types.preferences.PrefRecentVolumesFilter
import org.proninyaroslav.opencomicvine.types.preferences.PrefRecentVolumesFilterBundle
import org.proninyaroslav.opencomicvine.types.sort.ComicVineSortDirection
import org.proninyaroslav.opencomicvine.types.sort.VolumesSort

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
            val volume = PagingRecentVolumeItem(
                index = i,
                info = info,
            )
            volume
        }
        val remoteKeysList = itemsList.map { volume ->
            RecentVolumeItemRemoteKeys(
                id = volume.index,
                prevOffset = null,
                nextOffset = pageSize,
            )
        }
        val pagingState = PagingState<Int, PagingRecentVolumeItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )
        val filter = PrefRecentVolumesFilterBundle(
            dateAdded = PrefRecentVolumesFilter.DateAdded.Unknown
        )

        every { response.statusCode } returns StatusCode.OK
        every { response.results } returns volumesList
        every { response.numberOfPageResults } returns pageSize
        coEvery {
            volumesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = VolumesSort.DateAdded(ComicVineSortDirection.Desc),
                filters = emptyList(),
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
        every { pref.recentVolumesFilters } returns flowOf(filter)

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
                sort = VolumesSort.DateAdded(ComicVineSortDirection.Desc),
                filters = emptyList(),
            )
        }
        coVerify {
            volumeItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        }
        verify { pref.recentVolumesFilters }
        confirmVerified(volumesRepo, volumeItemRepo, response)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Load success result and endOfPaginationReached when no more data`() = runTest {
        val pageSize = 10
        val response = mockk<VolumesResponse>()
        val volumesList = emptyList<VolumeInfo>()
        val itemsList = emptyList<PagingRecentVolumeItem>()
        val remoteKeysList = emptyList<RecentVolumeItemRemoteKeys>()
        val pagingState = PagingState<Int, PagingRecentVolumeItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )
        val filter = PrefRecentVolumesFilterBundle(
            dateAdded = PrefRecentVolumesFilter.DateAdded.Unknown
        )

        every { response.statusCode } returns StatusCode.OK
        every { response.results } returns volumesList
        every { response.numberOfPageResults } returns 0
        coEvery {
            volumesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = VolumesSort.DateAdded(ComicVineSortDirection.Desc),
                filters = emptyList(),
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
        every { pref.recentVolumesFilters } returns flowOf(filter)

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
                sort = VolumesSort.DateAdded(ComicVineSortDirection.Desc),
                filters = emptyList(),
            )
        }
        coVerify {
            volumeItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        }
        verify { pref.recentVolumesFilters }
        confirmVerified(volumesRepo, volumeItemRepo, response)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `Service error`() = runTest {
        val pageSize = 10
        val response = mockk<VolumesResponse>()
        val pagingState = PagingState<Int, PagingRecentVolumeItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )
        val filter = PrefRecentVolumesFilterBundle(
            dateAdded = PrefRecentVolumesFilter.DateAdded.Unknown
        )

        every { response.statusCode } returns StatusCode.InvalidAPIKey
        every { response.error } returns "Invalid API Key"
        coEvery {
            volumesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = VolumesSort.DateAdded(ComicVineSortDirection.Desc),
                filters = emptyList(),
            )
        } returns ComicVineResult.Success(response)

        every { pref.recentVolumesFilters } returns flowOf(filter)

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
        verify { pref.recentVolumesFilters }
        coVerify {
            volumesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = VolumesSort.DateAdded(ComicVineSortDirection.Desc),
                filters = emptyList(),
            )
        }
        confirmVerified(volumesRepo, response)
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
            VolumesRemoteMediator(
                endOfPaginationOffset = pageSize - 1,
                volumesRepo,
                volumeItemRepo,
                pref,
                dispatcher,
            )
        val response = mockk<VolumesResponse>()
        val volumesList = emptyList<VolumeInfo>()
        val itemsList = emptyList<PagingRecentVolumeItem>()
        val remoteKeysList = emptyList<RecentVolumeItemRemoteKeys>()
        val pagingState = PagingState<Int, PagingRecentVolumeItem>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = pageSize
            ),
            leadingPlaceholderCount = pageSize,
        )
        val filter = PrefRecentVolumesFilterBundle(
            dateAdded = PrefRecentVolumesFilter.DateAdded.Unknown
        )

        every { response.statusCode } returns StatusCode.OK
        every { response.results } returns volumesList
        coEvery {
            volumesRepo.getItems(
                offset = 0,
                limit = pageSize,
                sort = VolumesSort.DateAdded(ComicVineSortDirection.Desc),
                filters = emptyList(),
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
        every { pref.recentVolumesFilters } returns flowOf(filter)

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
                sort = VolumesSort.DateAdded(ComicVineSortDirection.Desc),
                filters = emptyList(),
            )
        }
        coVerify {
            volumeItemRepo.saveItems(
                items = itemsList,
                remoteKeys = remoteKeysList,
                clearBeforeSave = true,
            )
        }
        verify { pref.recentVolumesFilters }
        confirmVerified(volumesRepo, volumeItemRepo, response)
    }
}