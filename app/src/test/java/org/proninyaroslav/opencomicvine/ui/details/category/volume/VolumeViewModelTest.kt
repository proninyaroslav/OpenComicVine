package org.proninyaroslav.opencomicvine.ui.details.category.volume

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.types.*
import org.proninyaroslav.opencomicvine.types.item.*
import org.proninyaroslav.opencomicvine.types.item.volume.*
import org.proninyaroslav.opencomicvine.types.paging.recent.PagingRecentVolumeItem
import org.proninyaroslav.opencomicvine.types.paging.wiki.PagingWikiVolumeItem
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoriteFetchResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.VolumesRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.ui.details.category.DetailsState
import java.io.IOException
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingVolumeRepository as RecentVolumeItemRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingVolumeRepository as WikiVolumeItemRepository

@OptIn(ExperimentalCoroutinesApi::class)
class VolumeViewModelTest {
    lateinit var viewModel: VolumeViewModel

    @MockK
    lateinit var volumesRepo: VolumesRepository

    @MockK
    lateinit var wikiVolumeItemRepo: WikiVolumeItemRepository

    @MockK
    lateinit var recentVolumeItemRepo: RecentVolumeItemRepository

    @MockK
    lateinit var volumeDetails: VolumeDetails

    @MockK
    lateinit var pagingDataFactory: VolumeViewModel.PagingDataFactory

    @MockK
    lateinit var issues: Flow<PagingData<IssueItem>>

    @MockK
    lateinit var characters: Flow<PagingData<VolumeCharacterItem>>

    @MockK
    lateinit var creators: Flow<PagingData<VolumePersonItem>>

    @MockK
    lateinit var locations: Flow<PagingData<VolumeLocationItem>>

    @MockK
    lateinit var concepts: Flow<PagingData<VolumeConceptItem>>

    @MockK
    lateinit var objects: Flow<PagingData<VolumeObjectItem>>

    @MockK
    lateinit var favoritesRepo: FavoritesRepository

    private lateinit var volumeDetailsCached: VolumeDetails

    private lateinit var volumeInfo: VolumeInfo

    val id = 1

    val isFavorite = flowOf(FavoriteFetchResult.Success(isFavorite = true))

    val dispatcher = StandardTestDispatcher()

    @MockK
    lateinit var errorReportService: ErrorReportService

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)

        volumeInfo = VolumeInfo(
            id = 1,
            name = "Name",
            startYear = "2022",
            image = mockk(),
            dateAdded = mockk(),
            dateLastUpdated = mockk(),
            _countOfIssues = 1,
            firstIssue = VolumeInfo.Issue(id = 1, name = "Name", issueNumber = "1"),
            lastIssue = VolumeInfo.Issue(id = 1, name = "Name", issueNumber = "1"),
            publisher = VolumeInfo.Publisher(id = 1, name = "Name"),
        )

        volumeDetailsCached = VolumeDetails(
            id = volumeInfo.id,
            name = volumeInfo.name,
            image = volumeInfo.image,
            dateAdded = volumeInfo.dateAdded,
            dateLastUpdated = volumeInfo.dateLastUpdated,
            startYear = volumeInfo.startYear,
            publisher = volumeInfo.publisher?.run {
                VolumeDetails.Publisher(
                    id = id,
                    name = name,
                )
            },
            firstIssue = volumeInfo.firstIssue?.run {
                VolumeDetails.Issue(
                    id = id,
                    name = name,
                    issueNumber = issueNumber,
                )
            },
            lastIssue = volumeInfo.lastIssue?.run {
                VolumeDetails.Issue(
                    id = id,
                    name = name,
                    issueNumber = issueNumber,
                )
            },
            _countOfIssues = volumeInfo._countOfIssues,
            descriptionShort = null,
            description = null,
            characters = emptyList(),
            concepts = emptyList(),
            issues = emptyList(),
            locations = emptyList(),
            objects = emptyList(),
            people = emptyList(),
        )

        viewModel = VolumeViewModel(
            volumesRepo,
            wikiVolumeItemRepo,
            recentVolumeItemRepo,
            pagingDataFactory,
            favoritesRepo,
            dispatcher,
            errorReportService,
        )

        every {
            pagingDataFactory.buildIssues(
                details = volumeDetails,
                coroutineScope = viewModel.viewModelScope,
            )
        } returns issues
        every {
            pagingDataFactory.buildCharacters(
                details = volumeDetails,
                coroutineScope = viewModel.viewModelScope,
            )
        } returns characters
        every {
            pagingDataFactory.buildPeople(
                details = volumeDetails,
                coroutineScope = viewModel.viewModelScope,
            )
        } returns creators
        every {
            pagingDataFactory.buildLocations(
                details = volumeDetails,
                coroutineScope = viewModel.viewModelScope,
            )
        } returns locations
        every {
            pagingDataFactory.buildConcepts(
                details = volumeDetails,
                coroutineScope = viewModel.viewModelScope,
            )
        } returns concepts
        every {
            pagingDataFactory.buildObjects(
                details = volumeDetails,
                coroutineScope = viewModel.viewModelScope,
            )
        } returns objects
        every {
            favoritesRepo.observe(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Volume,
            )
        } returns isFavorite
        every { volumeDetails.id } returns id
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Load success (cache from wiki repo)`() = runTest {
        val response = VolumeResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 1,
            offset = 0,
            numberOfPageResults = 1,
            numberOfTotalResults = 1,
            results = volumeDetails,
        )
        val result = ComicVineResult.Success(response)

        val expectedStates = listOf(
            DetailsState.Initial,
            DetailsState.Loading,
            DetailsState.CacheLoaded(
                VolumeDetailsItem(
                    details = volumeDetailsCached,
                    isFavorite = isFavorite,
                )
            ),
            DetailsState.Loaded(
                details = VolumeDetailsItem(
                    details = volumeDetails,
                    isFavorite = isFavorite,
                ),
                relatedEntities = VolumeViewModel.RelatedEntities(
                    issues = issues,
                    characters = characters,
                    creators = creators,
                    locations = locations,
                    concepts = concepts,
                    objects = objects,
                ),
            ),
        )
        val actualStates =
            mutableListOf<DetailsState<VolumeDetailsItem, VolumeViewModel.RelatedEntities>>()

        coEvery { volumesRepo.getItemDetailsById(id) } returns result
        coEvery { wikiVolumeItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Success(
                    PagingWikiVolumeItem(0, volumeInfo)
                )
        coEvery { recentVolumeItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Success(null)

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        dispatcher.scheduler.apply {
            viewModel.load(id)
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        coVerify { volumesRepo.getItemDetailsById(id) }
        coVerify { wikiVolumeItemRepo.getItemById(id) }
        confirmVerified(volumesRepo, wikiVolumeItemRepo)
    }

    @Test
    fun `Load success (cache from recent repo)`() = runTest {
        val response = VolumeResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 1,
            offset = 0,
            numberOfPageResults = 1,
            numberOfTotalResults = 1,
            results = volumeDetails,
        )
        val result = ComicVineResult.Success(response)

        val expectedStates = listOf(
            DetailsState.Initial,
            DetailsState.Loading,
            DetailsState.CacheLoaded(
                VolumeDetailsItem(
                    details = volumeDetailsCached,
                    isFavorite = isFavorite,
                )
            ),
            DetailsState.Loaded(
                details = VolumeDetailsItem(
                    details = volumeDetails,
                    isFavorite = isFavorite,
                ),
                relatedEntities = VolumeViewModel.RelatedEntities(
                    issues = issues,
                    characters = characters,
                    creators = creators,
                    locations = locations,
                    concepts = concepts,
                    objects = objects,
                ),
            ),
        )
        val actualStates =
            mutableListOf<DetailsState<VolumeDetailsItem, VolumeViewModel.RelatedEntities>>()

        coEvery { volumesRepo.getItemDetailsById(id) } returns result
        coEvery { wikiVolumeItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Success(null)
        coEvery { recentVolumeItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Success(
                    PagingRecentVolumeItem(0, volumeInfo)
                )

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        dispatcher.scheduler.apply {
            viewModel.load(id)
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        coVerify { volumesRepo.getItemDetailsById(id) }
        coVerify { recentVolumeItemRepo.getItemById(id) }
        confirmVerified(volumesRepo, recentVolumeItemRepo)
    }

    @Test
    fun `Load failed`() = runTest {
        val result = ComicVineResult.Failed.HttpError(com.skydoves.sandwich.StatusCode.NotFound)

        val expectedStates = listOf(
            DetailsState.Initial,
            DetailsState.Loading,
            DetailsState.CacheLoaded(
                VolumeDetailsItem(
                    details = volumeDetailsCached,
                    isFavorite = isFavorite,
                )
            ),
            DetailsState.LoadFailed(
                details = VolumeDetailsItem(
                    details = volumeDetailsCached,
                    isFavorite = isFavorite,
                ),
                error = result,
            ),
        )
        val actualStates =
            mutableListOf<DetailsState<VolumeDetailsItem, VolumeViewModel.RelatedEntities>>()

        coEvery { volumesRepo.getItemDetailsById(id) } returns result
        coEvery { wikiVolumeItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Success(null)
        coEvery { recentVolumeItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Success(
                    PagingRecentVolumeItem(0, volumeInfo)
                )

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        dispatcher.scheduler.apply {
            viewModel.load(id)
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        coVerify { volumesRepo.getItemDetailsById(id) }
        confirmVerified(volumesRepo)
    }

    @Test
    fun `Load after failed`() = runTest {
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
        val failedResult = ComicVineResult.Failed.HttpError(
            com.skydoves.sandwich.StatusCode.NotFound,
        )
        val successResult = ComicVineResult.Success(response)

        val expectedStates = listOf(
            DetailsState.Initial,
            DetailsState.Loading,
            DetailsState.CacheLoaded(
                VolumeDetailsItem(
                    details = volumeDetailsCached,
                    isFavorite = isFavorite,
                )
            ),
            DetailsState.LoadFailed(
                details = VolumeDetailsItem(
                    details = volumeDetailsCached,
                    isFavorite = isFavorite,
                ),
                error = failedResult,
            ),
            DetailsState.Loading,
            DetailsState.CacheLoaded(
                VolumeDetailsItem(
                    details = volumeDetailsCached,
                    isFavorite = isFavorite,
                )
            ),
            DetailsState.Loaded(
                details = VolumeDetailsItem(
                    details = volumeDetails,
                    isFavorite = isFavorite,
                ),
                relatedEntities = VolumeViewModel.RelatedEntities(
                    issues = issues,
                    characters = characters,
                    creators = creators,
                    locations = locations,
                    concepts = concepts,
                    objects = objects,
                ),
            ),
        )
        val actualStates =
            mutableListOf<DetailsState<VolumeDetailsItem, VolumeViewModel.RelatedEntities>>()

        coEvery { wikiVolumeItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Success(null)
        coEvery { recentVolumeItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Success(
                    PagingRecentVolumeItem(0, volumeInfo)
                )

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        dispatcher.scheduler.apply {
            coEvery { volumesRepo.getItemDetailsById(id) } returns failedResult
            viewModel.load(id)
            runCurrent()
            coEvery { volumesRepo.getItemDetailsById(id) } returns successResult
            viewModel.load(id)
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        coVerify { volumesRepo.getItemDetailsById(id) }
        confirmVerified(volumesRepo)
    }

    @Test
    fun `Cache load failed`() = runTest {
        val response = VolumeResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 1,
            offset = 0,
            numberOfPageResults = 1,
            numberOfTotalResults = 1,
            results = volumeDetails,
        )
        val result = ComicVineResult.Success(response)
        val exception = IOException()

        val expectedStates = listOf(
            DetailsState.Initial,
            DetailsState.Loading,
            DetailsState.CacheLoadFailed(
                details = null,
                exception = exception
            ),
            DetailsState.Loaded(
                details = VolumeDetailsItem(
                    details = volumeDetails,
                    isFavorite = isFavorite,
                ),
                relatedEntities = VolumeViewModel.RelatedEntities(
                    issues = issues,
                    characters = characters,
                    creators = creators,
                    locations = locations,
                    concepts = concepts,
                    objects = objects,
                ),
            ),
        )
        val actualStates =
            mutableListOf<DetailsState<VolumeDetailsItem, VolumeViewModel.RelatedEntities>>()

        coEvery { volumesRepo.getItemDetailsById(id) } returns result
        coEvery { wikiVolumeItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Failed.IO(exception)
        coEvery { recentVolumeItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Failed.IO(exception)

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        dispatcher.scheduler.apply {
            viewModel.load(id)
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        coVerify { volumesRepo.getItemDetailsById(id) }
        coVerify { wikiVolumeItemRepo.getItemById(id) }
        coVerify { recentVolumeItemRepo.getItemById(id) }
        confirmVerified(volumesRepo, wikiVolumeItemRepo, recentVolumeItemRepo)
    }

    @Test
    fun `Error report`() {
        val info = ErrorReportInfo(
            error = IOException(),
            comment = "comment",
        )

        every { errorReportService.report(info) } just runs

        viewModel.errorReport(info)
        dispatcher.scheduler.runCurrent()

        verify { errorReportService.report(info) }
        confirmVerified(errorReportService)
    }
}