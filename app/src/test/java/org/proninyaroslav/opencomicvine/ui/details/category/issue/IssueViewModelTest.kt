package org.proninyaroslav.opencomicvine.ui.details.category.issue

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
import org.proninyaroslav.opencomicvine.data.*
import org.proninyaroslav.opencomicvine.data.item.*
import org.proninyaroslav.opencomicvine.data.paging.recent.PagingRecentIssueItem
import org.proninyaroslav.opencomicvine.data.paging.wiki.PagingWikiIssueItem
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoriteFetchResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.IssuesRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.ui.details.category.DetailsState
import java.io.IOException
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingIssueRepository as RecentIssueItemRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingIssueRepository as WikiIssueItemRepository

@OptIn(ExperimentalCoroutinesApi::class)
class IssueViewModelTest {
    lateinit var viewModel: IssueViewModel

    @MockK
    lateinit var issuesRepo: IssuesRepository

    @MockK
    lateinit var wikiIssueItemRepo: WikiIssueItemRepository

    @MockK
    lateinit var recentIssueItemRepo: RecentIssueItemRepository

    @MockK
    lateinit var pagingDataFactory: IssueViewModel.PagingDataFactory

    @MockK
    lateinit var issueDetails: IssueDetails

    @MockK
    lateinit var creators: Flow<PagingData<PersonItem>>

    @MockK
    lateinit var characters: Flow<PagingData<CharacterItem>>

    @MockK
    lateinit var characterDiedIn: Flow<PagingData<CharacterItem>>

    @MockK
    lateinit var teams: Flow<PagingData<TeamItem>>

    @MockK
    lateinit var disbandedTeams: Flow<PagingData<TeamItem>>

    @MockK
    lateinit var locations: Flow<PagingData<LocationItem>>

    @MockK
    lateinit var concepts: Flow<PagingData<ConceptItem>>

    @MockK
    lateinit var objects: Flow<PagingData<ObjectItem>>

    @MockK
    lateinit var storyArcs: Flow<PagingData<StoryArcItem>>

    @MockK
    lateinit var favoritesRepo: FavoritesRepository

    private lateinit var issueDetailsCached: IssueDetails

    private lateinit var issueInfo: IssueInfo

    val id = 1

    val isFavorite = flowOf(FavoriteFetchResult.Success(isFavorite = true))

    val dispatcher = StandardTestDispatcher()

    @MockK
    lateinit var errorReportService: ErrorReportService

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)

        issueInfo = IssueInfo(
            id = 1,
            name = "Name",
            issueNumber = "1",
            image = mockk(),
            dateAdded = mockk(),
            dateLastUpdated = mockk(),
            volume = IssueInfo.Volume(id = 1, name = "Name"),
            storeDate = mockk(),
            coverDate = mockk(),
        )

        issueDetailsCached = IssueDetails(
            id = issueInfo.id,
            name = issueInfo.name,
            image = issueInfo.image,
            dateAdded = issueInfo.dateAdded,
            dateLastUpdated = issueInfo.dateLastUpdated,
            descriptionShort = null,
            description = null,
            issueNumber = issueInfo.issueNumber,
            volume = issueInfo.volume.run { IssueDetails.Volume(id = id, name = name) },
            coverDate = issueInfo.coverDate,
            storeDate = issueInfo.storeDate,
            associatedImages = emptyList(),
            characterCredits = emptyList(),
            characterDiedIn = emptyList(),
            conceptCredits = emptyList(),
            locationCredits = emptyList(),
            objectCredits = emptyList(),
            personCredits = emptyList(),
            storyArcCredits = emptyList(),
            teamCredits = emptyList(),
            teamDisbandedIn = emptyList(),
        )

        viewModel = IssueViewModel(
            issuesRepo,
            wikiIssueItemRepo,
            recentIssueItemRepo,
            pagingDataFactory,
            favoritesRepo,
            dispatcher,
            errorReportService,
        )

        every {
            pagingDataFactory.buildCreators(
                details = issueDetails,
                coroutineScope = viewModel.viewModelScope,
            )
        } returns creators
        every {
            pagingDataFactory.buildCharacters(
                details = issueDetails,
                coroutineScope = viewModel.viewModelScope,
            )
        } returns characters
        every {
            pagingDataFactory.buildCharacterDiedIn(
                details = issueDetails,
                coroutineScope = viewModel.viewModelScope,
            )
        } returns characterDiedIn
        every {
            pagingDataFactory.buildTeams(
                details = issueDetails,
                coroutineScope = viewModel.viewModelScope,
            )
        } returns teams
        every {
            pagingDataFactory.buildDisbandedTeams(
                details = issueDetails,
                coroutineScope = viewModel.viewModelScope,
            )
        } returns disbandedTeams
        every {
            pagingDataFactory.buildLocations(
                details = issueDetails,
                coroutineScope = viewModel.viewModelScope,
            )
        } returns locations
        every {
            pagingDataFactory.buildConcepts(
                details = issueDetails,
                coroutineScope = viewModel.viewModelScope,
            )
        } returns concepts
        every {
            pagingDataFactory.buildObjects(
                details = issueDetails,
                coroutineScope = viewModel.viewModelScope,
            )
        } returns objects
        every {
            pagingDataFactory.buildStoryArcs(
                details = issueDetails,
                coroutineScope = viewModel.viewModelScope,
            )
        } returns storyArcs
        every {
            favoritesRepo.observe(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Issue,
            )
        } returns isFavorite
        every { issueDetails.id } returns id
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Load success (cache from wiki repo)`() = runTest {
        val response = IssueResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 1,
            offset = 0,
            numberOfPageResults = 1,
            numberOfTotalResults = 1,
            results = issueDetails,
        )
        val result = ComicVineResult.Success(response)

        val expectedStates = listOf(
            DetailsState.Initial,
            DetailsState.Loading,
            DetailsState.CacheLoaded(
                IssueDetailsItem(
                    details = issueDetailsCached,
                    isFavorite = isFavorite,
                )
            ),
            DetailsState.Loaded(
                details = IssueDetailsItem(
                    details = issueDetails,
                    isFavorite = isFavorite,
                ),
                relatedEntities = IssueViewModel.RelatedEntities(
                    creators = creators,
                    characters = characters,
                    characterDiedIn = characterDiedIn,
                    teams = teams,
                    disbandedTeams = disbandedTeams,
                    locations = locations,
                    concepts = concepts,
                    objects = objects,
                    storyArcs = storyArcs,
                ),
            ),
        )
        val actualStates =
            mutableListOf<DetailsState<IssueDetailsItem, IssueViewModel.RelatedEntities>>()

        coEvery { issuesRepo.getItemDetailsById(id) } returns result
        coEvery { wikiIssueItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Success(
                    PagingWikiIssueItem(0, issueInfo)
                )
        coEvery { recentIssueItemRepo.getItemById(id) } returns
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

        coVerify { issuesRepo.getItemDetailsById(id) }
        coVerify { wikiIssueItemRepo.getItemById(id) }
        confirmVerified(issuesRepo, wikiIssueItemRepo)
    }

    @Test
    fun `Load success (cache from recent repo)`() = runTest {
        val response = IssueResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 1,
            offset = 0,
            numberOfPageResults = 1,
            numberOfTotalResults = 1,
            results = issueDetails,
        )
        val result = ComicVineResult.Success(response)

        val expectedStates = listOf(
            DetailsState.Initial,
            DetailsState.Loading,
            DetailsState.CacheLoaded(
                IssueDetailsItem(
                    details = issueDetailsCached,
                    isFavorite = isFavorite,
                )
            ),
            DetailsState.Loaded(
                details = IssueDetailsItem(
                    details = issueDetails,
                    isFavorite = isFavorite,
                ),
                relatedEntities = IssueViewModel.RelatedEntities(
                    creators = creators,
                    characters = characters,
                    characterDiedIn = characterDiedIn,
                    teams = teams,
                    disbandedTeams = disbandedTeams,
                    locations = locations,
                    concepts = concepts,
                    objects = objects,
                    storyArcs = storyArcs,
                ),
            ),
        )
        val actualStates =
            mutableListOf<DetailsState<IssueDetailsItem, IssueViewModel.RelatedEntities>>()

        coEvery { issuesRepo.getItemDetailsById(id) } returns result
        coEvery { wikiIssueItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Success(null)
        coEvery { recentIssueItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Success(
                    PagingRecentIssueItem(0, issueInfo)
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

        coVerify { issuesRepo.getItemDetailsById(id) }
        coVerify { recentIssueItemRepo.getItemById(id) }
        confirmVerified(issuesRepo, recentIssueItemRepo)
    }

    @Test
    fun `Load failed`() = runTest {
        val result = ComicVineResult.Failed.HttpError(com.skydoves.sandwich.StatusCode.NotFound)

        val expectedStates = listOf(
            DetailsState.Initial,
            DetailsState.Loading,
            DetailsState.CacheLoaded(
                IssueDetailsItem(
                    details = issueDetailsCached,
                    isFavorite = isFavorite,
                )
            ),
            DetailsState.LoadFailed(
                details = IssueDetailsItem(
                    details = issueDetailsCached,
                    isFavorite = isFavorite,
                ),
                error = result,
            ),
        )
        val actualStates =
            mutableListOf<DetailsState<IssueDetailsItem, IssueViewModel.RelatedEntities>>()

        coEvery { issuesRepo.getItemDetailsById(id) } returns result
        coEvery { wikiIssueItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Success(null)
        coEvery { recentIssueItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Success(
                    PagingRecentIssueItem(0, issueInfo)
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

        coVerify { issuesRepo.getItemDetailsById(id) }
        confirmVerified(issuesRepo)
    }

    @Test
    fun `Load after failed`() = runTest {
        val response = IssueResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 1,
            offset = 0,
            numberOfPageResults = 1,
            numberOfTotalResults = 1,
            results = issueDetails,
        )
        val failedResult = ComicVineResult.Failed.HttpError(
            com.skydoves.sandwich.StatusCode.NotFound,
        )
        val successResult = ComicVineResult.Success(response)

        val expectedStates = listOf(
            DetailsState.Initial,
            DetailsState.Loading,
            DetailsState.CacheLoaded(
                IssueDetailsItem(
                    details = issueDetailsCached,
                    isFavorite = isFavorite,
                )
            ),
            DetailsState.LoadFailed(
                details = IssueDetailsItem(
                    details = issueDetailsCached,
                    isFavorite = isFavorite,
                ),
                error = failedResult,
            ),
            DetailsState.Loading,
            DetailsState.CacheLoaded(
                IssueDetailsItem(
                    details = issueDetailsCached,
                    isFavorite = isFavorite,
                )
            ),
            DetailsState.Loaded(
                details = IssueDetailsItem(
                    details = issueDetails,
                    isFavorite = isFavorite,
                ),
                relatedEntities = IssueViewModel.RelatedEntities(
                    creators = creators,
                    characters = characters,
                    characterDiedIn = characterDiedIn,
                    teams = teams,
                    disbandedTeams = disbandedTeams,
                    locations = locations,
                    concepts = concepts,
                    objects = objects,
                    storyArcs = storyArcs,
                ),
            )
        )
        val actualStates =
            mutableListOf<DetailsState<IssueDetailsItem, IssueViewModel.RelatedEntities>>()

        coEvery { wikiIssueItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Success(null)
        coEvery { recentIssueItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Success(
                    PagingRecentIssueItem(0, issueInfo)
                )

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        dispatcher.scheduler.apply {
            coEvery { issuesRepo.getItemDetailsById(id) } returns failedResult
            viewModel.load(id)
            runCurrent()
            coEvery { issuesRepo.getItemDetailsById(id) } returns successResult
            viewModel.load(id)
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        coVerify { issuesRepo.getItemDetailsById(id) }
        confirmVerified(issuesRepo)
    }

    @Test
    fun `Cache load failed`() = runTest {
        val response = IssueResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 1,
            offset = 0,
            numberOfPageResults = 1,
            numberOfTotalResults = 1,
            results = issueDetails,
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
                details = IssueDetailsItem(
                    details = issueDetails,
                    isFavorite = isFavorite,
                ),
                relatedEntities = IssueViewModel.RelatedEntities(
                    creators = creators,
                    characters = characters,
                    characterDiedIn = characterDiedIn,
                    teams = teams,
                    disbandedTeams = disbandedTeams,
                    locations = locations,
                    concepts = concepts,
                    objects = objects,
                    storyArcs = storyArcs,
                ),
            ),
        )
        val actualStates =
            mutableListOf<DetailsState<IssueDetailsItem, IssueViewModel.RelatedEntities>>()

        coEvery { issuesRepo.getItemDetailsById(id) } returns result
        coEvery { wikiIssueItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Failed.IO(exception)
        coEvery { recentIssueItemRepo.getItemById(id) } returns
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

        coVerify { issuesRepo.getItemDetailsById(id) }
        coVerify { wikiIssueItemRepo.getItemById(id) }
        coVerify { recentIssueItemRepo.getItemById(id) }
        confirmVerified(issuesRepo, wikiIssueItemRepo, recentIssueItemRepo)
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