package org.proninyaroslav.opencomicvine.ui.details.category.character

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.data.*
import org.proninyaroslav.opencomicvine.data.item.*
import org.proninyaroslav.opencomicvine.data.paging.recent.PagingRecentCharacterItem
import org.proninyaroslav.opencomicvine.data.paging.wiki.PagingWikiCharacterItem
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.repo.CharactersRepository
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.repo.FavoriteFetchResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import org.proninyaroslav.opencomicvine.ui.details.category.DetailsEffect
import org.proninyaroslav.opencomicvine.ui.details.category.DetailsEvent
import org.proninyaroslav.opencomicvine.ui.details.category.DetailsState
import java.io.IOException
import org.proninyaroslav.opencomicvine.model.repo.paging.recent.PagingCharacterRepository as RecentCharacterItemRepository
import org.proninyaroslav.opencomicvine.model.repo.paging.wiki.PagingCharacterRepository as WikiCharacterItemRepository

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterViewModelTest {
    lateinit var viewModel: CharacterViewModel

    @MockK
    lateinit var charactersRepo: CharactersRepository

    @MockK
    lateinit var wikiCharacterItemRepo: WikiCharacterItemRepository

    @MockK
    lateinit var recentCharacterItemRepo: RecentCharacterItemRepository

    @MockK
    lateinit var pagingDataFactory: CharacterViewModel.PagingDataFactory

    @MockK
    lateinit var characterDetails: CharacterDetails

    @MockK
    lateinit var movies: Flow<PagingData<MovieItem>>

    @MockK
    lateinit var issues: Flow<PagingData<IssueItem>>

    @MockK
    lateinit var volumes: Flow<PagingData<VolumeItem>>

    @MockK
    lateinit var storyArcs: Flow<PagingData<StoryArcItem>>

    @MockK
    lateinit var friends: Flow<PagingData<CharacterItem>>

    @MockK
    lateinit var enemies: Flow<PagingData<CharacterItem>>

    @MockK
    lateinit var teams: Flow<PagingData<TeamItem>>

    @MockK
    lateinit var teamEnemies: Flow<PagingData<TeamItem>>

    @MockK
    lateinit var teamFriends: Flow<PagingData<TeamItem>>

    @MockK
    lateinit var favoritesRepo: FavoritesRepository

    private lateinit var characterDetailsCached: CharacterDetails

    private lateinit var characterInfo: CharacterInfo

    val isFavorite = flowOf(FavoriteFetchResult.Success(isFavorite = true))

    val id = 1

    val dispatcher = StandardTestDispatcher()

    @MockK
    lateinit var errorReportService: ErrorReportService

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)

        characterInfo = CharacterInfo(
            id = 1,
            name = "Name",
            gender = Gender.Male,
            image = mockk(),
            dateAdded = mockk(),
            dateLastUpdated = mockk(),
        )

        characterDetailsCached = CharacterDetails(
            id = characterInfo.id,
            name = characterInfo.name,
            gender = characterInfo.gender,
            image = characterInfo.image,
            dateAdded = characterInfo.dateAdded,
            dateLastUpdated = characterInfo.dateLastUpdated,
            aliases = null,
            birth = null,
            enemies = emptyList(),
            friends = emptyList(),
            countOfIssueAppearances = 0,
            creators = emptyList(),
            descriptionShort = null,
            description = null,
            firstAppearedInIssue = null,
            issueCredits = emptyList(),
            issuesDiedIn = emptyList(),
            movies = emptyList(),
            origin = null,
            powers = emptyList(),
            publisher = null,
            realName = null,
            storyArcCredits = emptyList(),
            teamFriends = emptyList(),
            teamEnemies = emptyList(),
            teams = emptyList(),
            volumeCredits = emptyList(),
        )

        viewModel = CharacterViewModel(
            charactersRepo,
            wikiCharacterItemRepo,
            recentCharacterItemRepo,
            pagingDataFactory,
            favoritesRepo,
            dispatcher,
            errorReportService,
        )

        every {
            pagingDataFactory.buildMovies(
                details = characterDetails,
                coroutineScope = viewModel.viewModelScope,
            )
        } returns movies
        every {
            pagingDataFactory.buildIssues(
                details = characterDetails,
                coroutineScope = viewModel.viewModelScope,
            )
        } returns issues
        every {
            pagingDataFactory.buildVolumes(
                details = characterDetails,
                coroutineScope = viewModel.viewModelScope,
            )
        } returns volumes
        every {
            pagingDataFactory.buildStoryArcs(
                details = characterDetails,
                coroutineScope = viewModel.viewModelScope,
            )
        } returns storyArcs
        every {
            pagingDataFactory.buildFriends(
                details = characterDetails,
                coroutineScope = viewModel.viewModelScope,
            )
        } returns friends
        every {
            pagingDataFactory.buildEnemies(
                details = characterDetails,
                coroutineScope = viewModel.viewModelScope,
            )
        } returns enemies
        every {
            pagingDataFactory.buildTeams(
                details = characterDetails,
                coroutineScope = viewModel.viewModelScope,
            )
        } returns teams
        every {
            pagingDataFactory.buildTeamEnemies(
                details = characterDetails,
                coroutineScope = viewModel.viewModelScope,
            )
        } returns teamEnemies
        every {
            pagingDataFactory.buildTeamFriends(
                details = characterDetails,
                coroutineScope = viewModel.viewModelScope,
            )
        } returns teamFriends
        every {
            favoritesRepo.observe(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Character,
            )
        } returns isFavorite
        every { characterDetails.id } returns id
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Load success (cache from wiki repo)`() = runTest {
        val response = CharacterResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 1,
            offset = 0,
            numberOfPageResults = 1,
            numberOfTotalResults = 1,
            results = characterDetails,
        )
        val result = ComicVineResult.Success(response)

        val expectedStates = listOf(
            DetailsState.Initial,
            DetailsState.Loading,
            DetailsState.CacheLoaded(
                CharacterDetailsItem(
                    details = characterDetailsCached,
                    isFavorite = isFavorite,
                )
            ),
            DetailsState.Loaded(
                details = CharacterDetailsItem(
                    details = characterDetails,
                    isFavorite = isFavorite,
                ),
                relatedEntities = CharacterViewModel.RelatedEntities(
                    movies = movies,
                    issues = issues,
                    volumes = volumes,
                    storyArcs = storyArcs,
                    friends = friends,
                    enemies = enemies,
                    teams = teams,
                    teamEnemies = teamEnemies,
                    teamFriends = teamFriends,
                ),
            ),
        )
        val actualStates =
            mutableListOf<DetailsState<CharacterDetailsItem, CharacterViewModel.RelatedEntities>>()

        coEvery { charactersRepo.getItemDetailsById(id) } returns result
        coEvery { wikiCharacterItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Success(
                    PagingWikiCharacterItem(0, characterInfo)
                )
        coEvery { recentCharacterItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Success(null)

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        dispatcher.scheduler.apply {
            viewModel.event(DetailsEvent.Load(id))
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        coVerify { charactersRepo.getItemDetailsById(id) }
        coVerify { wikiCharacterItemRepo.getItemById(id) }
        verify {
            favoritesRepo.observe(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Character,
            )
        }
        confirmVerified(charactersRepo, wikiCharacterItemRepo, favoritesRepo)
    }

    @Test
    fun `Load success (cache from recent repo)`() = runTest {
        val response = CharacterResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 1,
            offset = 0,
            numberOfPageResults = 1,
            numberOfTotalResults = 1,
            results = characterDetails,
        )
        val result = ComicVineResult.Success(response)

        val expectedStates = listOf(
            DetailsState.Initial,
            DetailsState.Loading,
            DetailsState.CacheLoaded(
                CharacterDetailsItem(
                    details = characterDetailsCached,
                    isFavorite = isFavorite,
                )
            ),
            DetailsState.Loaded(
                details = CharacterDetailsItem(
                    details = characterDetails,
                    isFavorite = isFavorite,
                ),
                relatedEntities = CharacterViewModel.RelatedEntities(
                    movies = movies,
                    issues = issues,
                    volumes = volumes,
                    storyArcs = storyArcs,
                    friends = friends,
                    enemies = enemies,
                    teams = teams,
                    teamEnemies = teamEnemies,
                    teamFriends = teamFriends,
                ),
            ),
        )
        val actualStates =
            mutableListOf<DetailsState<CharacterDetailsItem, CharacterViewModel.RelatedEntities>>()

        coEvery { charactersRepo.getItemDetailsById(id) } returns result
        coEvery { wikiCharacterItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Success(null)
        coEvery { recentCharacterItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Success(
                    PagingRecentCharacterItem(0, characterInfo)
                )

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        dispatcher.scheduler.apply {
            viewModel.event(DetailsEvent.Load(id))
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        coVerify { charactersRepo.getItemDetailsById(id) }
        coVerify { recentCharacterItemRepo.getItemById(id) }
        verify {
            favoritesRepo.observe(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Character,
            )
        }
        confirmVerified(charactersRepo, recentCharacterItemRepo, favoritesRepo)
    }

    @Test
    fun `Load failed`() = runTest {
        val result = ComicVineResult.Failed.HttpError(com.skydoves.sandwich.StatusCode.NotFound)

        val expectedStates = listOf(
            DetailsState.Initial,
            DetailsState.Loading,
            DetailsState.CacheLoaded(
                CharacterDetailsItem(
                    details = characterDetailsCached,
                    isFavorite = isFavorite,
                )
            ),
            DetailsState.LoadFailed(
                details = CharacterDetailsItem(
                    details = characterDetailsCached,
                    isFavorite = isFavorite,
                ),
                error = result,
            ),
        )
        val actualStates =
            mutableListOf<DetailsState<CharacterDetailsItem, CharacterViewModel.RelatedEntities>>()

        coEvery { charactersRepo.getItemDetailsById(id) } returns result
        coEvery { wikiCharacterItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Success(null)
        coEvery { recentCharacterItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Success(
                    PagingRecentCharacterItem(0, characterInfo)
                )

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        dispatcher.scheduler.apply {
            viewModel.event(DetailsEvent.Load(id))
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        coVerify { charactersRepo.getItemDetailsById(id) }
        verify {
            favoritesRepo.observe(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Character,
            )
        }
        confirmVerified(charactersRepo, favoritesRepo)
    }

    @Test
    fun `Load after failed`() = runTest {
        val response = CharacterResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 1,
            offset = 0,
            numberOfPageResults = 1,
            numberOfTotalResults = 1,
            results = characterDetails,
        )
        val failedResult = ComicVineResult.Failed.HttpError(
            com.skydoves.sandwich.StatusCode.NotFound,
        )
        val successResult = ComicVineResult.Success(response)

        val expectedStates = listOf(
            DetailsState.Initial,
            DetailsState.Loading,
            DetailsState.CacheLoaded(
                CharacterDetailsItem(
                    details = characterDetailsCached,
                    isFavorite = isFavorite,
                )
            ),
            DetailsState.LoadFailed(
                details = CharacterDetailsItem(
                    details = characterDetailsCached,
                    isFavorite = isFavorite,
                ),
                error = failedResult,
            ),
            DetailsState.Loading,
            DetailsState.CacheLoaded(
                CharacterDetailsItem(
                    details = characterDetailsCached,
                    isFavorite = isFavorite,
                )
            ),
            DetailsState.Loaded(
                details = CharacterDetailsItem(
                    details = characterDetails,
                    isFavorite = isFavorite,
                ),
                relatedEntities = CharacterViewModel.RelatedEntities(
                    movies = movies,
                    issues = issues,
                    volumes = volumes,
                    storyArcs = storyArcs,
                    friends = friends,
                    enemies = enemies,
                    teams = teams,
                    teamEnemies = teamEnemies,
                    teamFriends = teamFriends,
                ),
            ),
        )
        val actualStates =
            mutableListOf<DetailsState<CharacterDetailsItem, CharacterViewModel.RelatedEntities>>()

        coEvery { wikiCharacterItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Success(null)
        coEvery { recentCharacterItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Success(
                    PagingRecentCharacterItem(0, characterInfo)
                )

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }

        dispatcher.scheduler.apply {
            coEvery { charactersRepo.getItemDetailsById(id) } returns failedResult
            viewModel.event(DetailsEvent.Load(id))
            runCurrent()
            coEvery { charactersRepo.getItemDetailsById(id) } returns successResult
            viewModel.event(DetailsEvent.Load(id))
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        stateJob.cancel()

        coVerify { charactersRepo.getItemDetailsById(id) }
        verify {
            favoritesRepo.observe(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Character,
            )
        }
        confirmVerified(charactersRepo, favoritesRepo)
    }

    @Test
    fun `Cache load failed`() = runTest {
        val response = CharacterResponse(
            statusCode = StatusCode.OK,
            error = "OK",
            limit = 1,
            offset = 0,
            numberOfPageResults = 1,
            numberOfTotalResults = 1,
            results = characterDetails,
        )
        val result = ComicVineResult.Success(response)
        val exception = IOException()

        val expectedStates = listOf(
            DetailsState.Initial,
            DetailsState.Loading,
            DetailsState.Loaded(
                details = CharacterDetailsItem(
                    details = characterDetails,
                    isFavorite = isFavorite,
                ),
                relatedEntities = CharacterViewModel.RelatedEntities(
                    movies = movies,
                    issues = issues,
                    volumes = volumes,
                    storyArcs = storyArcs,
                    friends = friends,
                    enemies = enemies,
                    teams = teams,
                    teamEnemies = teamEnemies,
                    teamFriends = teamFriends,
                ),
            ),
        )
        val expectedEffects = listOf(
            DetailsEffect.CacheLoadFailed(exception),
        )
        val actualStates =
            mutableListOf<DetailsState<CharacterDetailsItem, CharacterViewModel.RelatedEntities>>()
        val actualEffects = mutableListOf<DetailsEffect>()

        coEvery { charactersRepo.getItemDetailsById(id) } returns result
        coEvery { wikiCharacterItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Failed.IO(exception)
        coEvery { recentCharacterItemRepo.getItemById(id) } returns
                ComicVinePagingRepository.Result.Failed.IO(exception)

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }
        val effectJob = launch {
            viewModel.effect.toList(actualEffects)
        }

        dispatcher.scheduler.apply {
            viewModel.event(DetailsEvent.Load(id))
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        assertEquals(expectedEffects, actualEffects)
        stateJob.cancel()
        effectJob.cancel()

        coVerify { charactersRepo.getItemDetailsById(id) }
        coVerify { wikiCharacterItemRepo.getItemById(id) }
        coVerify { recentCharacterItemRepo.getItemById(id) }
        verify {
            favoritesRepo.observe(
                entityId = id,
                entityType = FavoriteInfo.EntityType.Character,
            )
        }
        confirmVerified(
            charactersRepo,
            wikiCharacterItemRepo,
            recentCharacterItemRepo,
            favoritesRepo,
        )
    }

    @Test
    fun `Error report`() {
        val info = ErrorReportInfo(
            error = IOException(),
            comment = "comment",
        )

        every { errorReportService.report(info) } just runs

        viewModel.event(DetailsEvent.ErrorReport(info))
        dispatcher.scheduler.runCurrent()

        verify { errorReportService.report(info) }
        confirmVerified(errorReportService)
    }
}