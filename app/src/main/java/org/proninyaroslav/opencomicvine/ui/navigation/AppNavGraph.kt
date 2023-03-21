package org.proninyaroslav.opencomicvine.ui.navigation

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.model.ComicVineUrlBuilder
import org.proninyaroslav.opencomicvine.ui.about.AboutDialogPage
import org.proninyaroslav.opencomicvine.ui.auth.AuthActivity
import org.proninyaroslav.opencomicvine.ui.auth.AuthGuardState
import org.proninyaroslav.opencomicvine.ui.auth.AuthGuardViewModel
import org.proninyaroslav.opencomicvine.ui.auth.AuthLoadingError
import org.proninyaroslav.opencomicvine.ui.details.DetailsPage
import org.proninyaroslav.opencomicvine.ui.details.category.character.CharacterPage
import org.proninyaroslav.opencomicvine.ui.details.category.issue.IssuePage
import org.proninyaroslav.opencomicvine.ui.details.category.volume.VolumePage
import org.proninyaroslav.opencomicvine.ui.favorites.FavoritesPage
import org.proninyaroslav.opencomicvine.ui.home.HomePage
import org.proninyaroslav.opencomicvine.ui.image_viewer.ImageViewerActivity
import org.proninyaroslav.opencomicvine.ui.search.SearchPage
import org.proninyaroslav.opencomicvine.ui.settings.SettingsPage
import org.proninyaroslav.opencomicvine.ui.wiki.WikiPage
import org.proninyaroslav.opencomicvine.ui.favorites.category.CharactersPage as FavoriteCharactersPage
import org.proninyaroslav.opencomicvine.ui.favorites.category.ConceptsPage as FavoriteConceptsPage
import org.proninyaroslav.opencomicvine.ui.favorites.category.IssuesPage as FavoriteIssuesPage
import org.proninyaroslav.opencomicvine.ui.favorites.category.LocationsPage as FavoriteLocationsPage
import org.proninyaroslav.opencomicvine.ui.favorites.category.MoviesPage as FavoriteMoviesPage
import org.proninyaroslav.opencomicvine.ui.favorites.category.ObjectsPage as FavoriteObjectsPage
import org.proninyaroslav.opencomicvine.ui.favorites.category.PeoplePage as FavoritePeoplePage
import org.proninyaroslav.opencomicvine.ui.favorites.category.StoryArcsPage as FavoriteStoryArcsPage
import org.proninyaroslav.opencomicvine.ui.favorites.category.TeamsPage as FavoriteTeamsPage
import org.proninyaroslav.opencomicvine.ui.favorites.category.VolumesPage as FavoriteVolumesPage
import org.proninyaroslav.opencomicvine.ui.home.category.CharactersPage as RecentCharactersPage
import org.proninyaroslav.opencomicvine.ui.home.category.IssuesPage as RecentIssuesPage
import org.proninyaroslav.opencomicvine.ui.home.category.VolumesPage as RecentVolumesPage
import org.proninyaroslav.opencomicvine.ui.wiki.category.CharactersPage as WikiCharactersPage
import org.proninyaroslav.opencomicvine.ui.wiki.category.IssuesPage as WikiIssuesPage
import org.proninyaroslav.opencomicvine.ui.wiki.category.VolumesPage as WikiVolumesPage

@Composable
fun AppNavGraph(
    navController: NavHostController,
    isExpandedWidth: Boolean,
    modifier: Modifier = Modifier,
) {
    val authGuardViewModel = hiltViewModel<AuthGuardViewModel>()
    val authGuardState by authGuardViewModel.state.collectAsState()
    val startDestination by remember {
        derivedStateOf {
            when (authGuardState) {
                AuthGuardState.NotAuthorized -> AppDestination.Auth.route
                else -> AppDestination.Home.route
            }
        }
    }
    when (val s = authGuardState) {
        AuthGuardState.Initial -> AuthLoading()
        is AuthGuardState.GetStatusError -> AuthLoadingError(error = s.error)
        else -> {
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = modifier,
            ) {
                homeGraph(navController, isExpandedWidth)
                searchGraph(navController, isExpandedWidth)
                wikiGraph(navController, isExpandedWidth)
                favoritesGraph(navController, isExpandedWidth)
                activity(
                    AppDestination.ImageViewer.route
                ) {
                    activityClass = ImageViewerActivity::class
                    argument(AppDestination.ImageViewer.argumentName!!) {
                        type = NavType.StringType
                    }
                }
                activity(
                    AppDestination.Auth.route
                ) {
                    activityClass = AuthActivity::class
                }
            }
        }
    }
}

@Composable
fun AuthLoading(
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize(),
    ) {
        CircularProgressIndicator()
    }
}

fun NavGraphBuilder.searchGraph(
    navController: NavHostController,
    isExpandedWidth: Boolean,
) {
    val onResolveDetailsDestination = { destination: DetailsPage ->
        when (destination) {
            is DetailsPage.Character -> HomeDestination.Character
            is DetailsPage.Volume -> HomeDestination.Volume
            is DetailsPage.Issue -> HomeDestination.Issue
            else -> throw IllegalArgumentException("Unknown destination: $destination")
        }
    }
    val onResolveSearchDestination = { context: Context, destination: SearchPage ->
        when (destination) {
            is SearchPage.Character -> {
                navController.navigateTo(
                    SearchDestination.Character,
                    argument = destination.characterId,
                )
            }
            is SearchPage.Issue -> {
                navController.navigateTo(
                    SearchDestination.Issue,
                    argument = destination.issueId,
                )
            }
            is SearchPage.Volume -> {
                navController.navigateTo(
                    SearchDestination.Volume,
                    argument = destination.volumeId,
                )
            }
            is SearchPage.Concept -> navController.navigateOrView(
                context = context,
                url = ComicVineUrlBuilder.concept(destination.conceptId).toUri(),
            )
            is SearchPage.Episode -> navController.navigateOrView(
                context = context,
                url = ComicVineUrlBuilder.episode(destination.episodeId).toUri(),
            )
            is SearchPage.Location -> navController.navigateOrView(
                context = context,
                url = ComicVineUrlBuilder.location(destination.locationId).toUri(),
            )
            is SearchPage.Movie -> navController.navigateOrView(
                context = context,
                url = ComicVineUrlBuilder.movie(destination.movieId).toUri(),
            )
            is SearchPage.Object -> navController.navigateOrView(
                context = context,
                url = ComicVineUrlBuilder.objectUrl(destination.objectId).toUri(),
            )
            is SearchPage.Person -> navController.navigateOrView(
                context = context,
                url = ComicVineUrlBuilder.person(destination.personId).toUri(),
            )
            is SearchPage.Series -> navController.navigateOrView(
                context = context,
                url = ComicVineUrlBuilder.series(destination.seriesId).toUri(),
            )
            is SearchPage.StoryArc -> navController.navigateOrView(
                context = context,
                url = ComicVineUrlBuilder.storyArc(destination.storyArcId).toUri(),
            )
            is SearchPage.Team -> navController.navigateOrView(
                context = context,
                url = ComicVineUrlBuilder.team(destination.teamId).toUri(),
            )
            is SearchPage.Video -> navController.navigateOrView(
                context = context,
                url = ComicVineUrlBuilder.video(destination.videoId).toUri(),
            )
        }
    }

    navigation(
        route = AppDestination.Search.route,
        startDestination = SearchDestination.Overview.route,
    ) {
        composable(SearchDestination.Overview.route) {
            val context = LocalContext.current
            SearchPage(
                viewModel = hiltViewModel(),
                favoritesViewModel = hiltViewModel(),
                networkConnection = hiltViewModel(),
                filterViewModel = hiltViewModel(),
                searchHistoryViewModel = hiltViewModel(),
                isExpandedWidth = isExpandedWidth,
                onLoadPage = { onResolveSearchDestination(context, it) },
            )
        }
        characterGraph(
            destination = SearchDestination.Character,
            navController = navController,
            isExpandedWidth = isExpandedWidth,
            onLoadPage = { context, page ->
                page.navigate(context, navController, onResolveDetailsDestination)
            }
        )
        issueGraph(
            destination = SearchDestination.Issue,
            navController = navController,
            isExpandedWidth = isExpandedWidth,
            onLoadPage = { context, page ->
                page.navigate(context, navController, onResolveDetailsDestination)
            }
        )
        volumeGraph(
            destination = SearchDestination.Volume,
            navController = navController,
            isExpandedWidth = isExpandedWidth,
            onLoadPage = { context, page ->
                page.navigate(context, navController, onResolveDetailsDestination)
            }
        )
    }
}

fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    isExpandedWidth: Boolean,
) {
    val onResolveDetailsDestination = { destination: DetailsPage ->
        when (destination) {
            is DetailsPage.Character -> HomeDestination.Character
            is DetailsPage.Volume -> HomeDestination.Volume
            is DetailsPage.Issue -> HomeDestination.Issue
            else -> throw IllegalArgumentException("Unknown destination: $destination")
        }
    }
    val onResolveHomeDestination = { destination: HomePage ->
        when (destination) {
            HomePage.RecentCharacters -> navController navigateTo HomeDestination.RecentCharacters
            HomePage.Settings -> navController navigateTo HomeDestination.Settings
            HomePage.RecentIssues -> navController navigateTo HomeDestination.RecentIssues
            HomePage.RecentVolumes -> navController navigateTo HomeDestination.RecentVolumes
            is HomePage.Character -> {
                navController.navigateTo(
                    HomeDestination.Character,
                    argument = destination.characterId,
                )
            }
            is HomePage.Issue -> {
                navController.navigateTo(
                    HomeDestination.Issue,
                    argument = destination.issueId,
                )
            }
            is HomePage.Volume -> {
                navController.navigateTo(
                    HomeDestination.Volume,
                    argument = destination.volumeId,
                )
            }
            HomePage.About -> navController navigateTo HomeDestination.About
        }
    }
    navigation(
        route = AppDestination.Home.route,
        startDestination = HomeDestination.Overview.route,
    ) {
        composable(HomeDestination.Overview.route) {
            HomePage(
                viewModel = hiltViewModel(),
                networkConnection = hiltViewModel(),
                isExpandedWidth = isExpandedWidth,
                onLoadPage = { onResolveHomeDestination(it) },
            )
        }
        composable(HomeDestination.Settings.route) {
            SettingsPage(
                onBackButtonClicked = navController::navigateUp,
                isExpandedWidth = isExpandedWidth,
                viewModel = hiltViewModel(),
            )
        }
        dialog(HomeDestination.About.route) {
            AboutDialogPage(
                viewModel = hiltViewModel(),
                isExpandedWidth = isExpandedWidth,
                onBackButtonClicked = navController::navigateUp,
            )
        }
        composable(HomeDestination.RecentCharacters.route) {
            RecentCharactersPage(
                viewModel = hiltViewModel(),
                networkConnection = hiltViewModel(),
                datePickerViewModel = hiltViewModel(),
                filterViewModel = hiltViewModel(),
                favoritesViewModel = hiltViewModel(),
                onLoadPage = { onResolveHomeDestination(it) },
                onBackButtonClicked = navController::navigateUp,
            )
        }
        composable(
            HomeDestination.RecentIssues.route,
            deepLinks = HomeDestination.RecentIssues.deepLinks.map {
                navDeepLink {
                    uriPattern = it.uriPattern
                    action = it.intentAction
                    mimeType = it.mimeType
                }
            },
        ) {
            RecentIssuesPage(
                viewModel = hiltViewModel(),
                networkConnection = hiltViewModel(),
                datePickerViewModel = hiltViewModel(),
                filterViewModel = hiltViewModel(),
                favoritesViewModel = hiltViewModel(),
                onLoadPage = { onResolveHomeDestination(it) },
                onBackButtonClicked = navController::navigateUp,
            )
        }
        composable(HomeDestination.RecentVolumes.route) {
            RecentVolumesPage(
                viewModel = hiltViewModel(),
                networkConnection = hiltViewModel(),
                datePickerViewModel = hiltViewModel(),
                filterViewModel = hiltViewModel(),
                favoritesViewModel = hiltViewModel(),
                onBackButtonClicked = navController::navigateUp,
                onLoadPage = { onResolveHomeDestination(it) },
            )
        }
        characterGraph(
            destination = HomeDestination.Character,
            navController = navController,
            isExpandedWidth = isExpandedWidth,
            onLoadPage = { context, page ->
                page.navigate(context, navController, onResolveDetailsDestination)
            }
        )
        issueGraph(
            destination = HomeDestination.Issue,
            navController = navController,
            isExpandedWidth = isExpandedWidth,
            onLoadPage = { context, page ->
                page.navigate(context, navController, onResolveDetailsDestination)
            }
        )
        volumeGraph(
            destination = HomeDestination.Volume,
            navController = navController,
            isExpandedWidth = isExpandedWidth,
            onLoadPage = { context, page ->
                page.navigate(context, navController, onResolveDetailsDestination)
            }
        )
    }
}

fun NavGraphBuilder.wikiGraph(
    navController: NavHostController,
    isExpandedWidth: Boolean,
) {
    val onResolveDetailsDestination = { destination: DetailsPage ->
        when (destination) {
            is DetailsPage.Character -> WikiDestination.Character
            is DetailsPage.Volume -> WikiDestination.Volume
            is DetailsPage.Issue -> WikiDestination.Issue
            else -> throw IllegalArgumentException("Unknown destination: $destination")
        }
    }
    val onResolveWikiDestination = { destination: WikiPage ->
        when (destination) {
            WikiPage.Characters -> navController navigateTo WikiDestination.Characters
            WikiPage.Issues -> navController navigateTo WikiDestination.Issues
            WikiPage.Volumes -> navController navigateTo WikiDestination.Volumes
            is WikiPage.Character -> {
                navController.navigateTo(
                    WikiDestination.Character,
                    argument = destination.characterId,
                )
            }
            is WikiPage.Issue -> {
                navController.navigateTo(
                    WikiDestination.Issue,
                    argument = destination.issueId,
                )
            }
            is WikiPage.Volume -> {
                navController.navigateTo(
                    WikiDestination.Volume,
                    argument = destination.volumeId,
                )
            }
        }
    }
    navigation(
        route = AppDestination.Wiki.route,
        startDestination = WikiDestination.Overview.route,
    ) {
        composable(WikiDestination.Overview.route) {
            WikiPage(
                viewModel = hiltViewModel(),
                networkConnection = hiltViewModel(),
                onLoadPage = { onResolveWikiDestination(it) },
                isExpandedWidth = isExpandedWidth,
            )
        }
        composable(
            WikiDestination.Characters.route,
            deepLinks = WikiDestination.Characters.deepLinks.map {
                navDeepLink {
                    uriPattern = it.uriPattern
                    action = it.intentAction
                    mimeType = it.mimeType
                }
            },
        ) {
            WikiCharactersPage(
                viewModel = hiltViewModel(),
                networkConnection = hiltViewModel(),
                datePickerViewModel = hiltViewModel(),
                filterViewModel = hiltViewModel(),
                favoritesViewModel = hiltViewModel(),
                onBackButtonClicked = navController::navigateUp,
                onLoadPage = { onResolveWikiDestination(it) },
            )
        }
        composable(
            WikiDestination.Issues.route,
            deepLinks = WikiDestination.Issues.deepLinks.map {
                navDeepLink {
                    uriPattern = it.uriPattern
                    action = it.intentAction
                    mimeType = it.mimeType
                }
            },
        ) {
            WikiIssuesPage(
                viewModel = hiltViewModel(),
                networkConnection = hiltViewModel(),
                datePickerViewModel = hiltViewModel(),
                filterViewModel = hiltViewModel(),
                favoritesViewModel = hiltViewModel(),
                onLoadPage = { onResolveWikiDestination(it) },
                onBackButtonClicked = navController::navigateUp,
            )
        }
        composable(
            WikiDestination.Volumes.route,
            deepLinks = WikiDestination.Volumes.deepLinks.map {
                navDeepLink {
                    uriPattern = it.uriPattern
                    action = it.intentAction
                    mimeType = it.mimeType
                }
            },
        ) {
            WikiVolumesPage(
                viewModel = hiltViewModel(),
                networkConnection = hiltViewModel(),
                datePickerViewModel = hiltViewModel(),
                filterViewModel = hiltViewModel(),
                favoritesViewModel = hiltViewModel(),
                onLoadPage = { onResolveWikiDestination(it) },
                onBackButtonClicked = navController::navigateUp,
            )
        }
        characterGraph(
            destination = WikiDestination.Character,
            navController = navController,
            isExpandedWidth = isExpandedWidth,
            onLoadPage = { context, page ->
                page.navigate(context, navController, onResolveDetailsDestination)
            }
        )
        issueGraph(
            destination = WikiDestination.Issue,
            navController = navController,
            isExpandedWidth = isExpandedWidth,
            onLoadPage = { context, page ->
                page.navigate(context, navController, onResolveDetailsDestination)
            }
        )
        volumeGraph(
            destination = WikiDestination.Volume,
            navController = navController,
            isExpandedWidth = isExpandedWidth,
            onLoadPage = { context, page ->
                page.navigate(context, navController, onResolveDetailsDestination)
            }
        )
    }
}

@SuppressLint("RestrictedApi")
fun NavGraphBuilder.characterGraph(
    destination: Destination,
    onLoadPage: (Context, DetailsPage) -> Unit,
    navController: NavHostController,
    isExpandedWidth: Boolean,
) {
    composable(
        destination.route,
        arguments = listOf(
            navArgument(destination.argumentName!!) {
                type = NavType.IntType
            }
        ),
        deepLinks = destination.deepLinks.map {
            navDeepLink {
                uriPattern = it.uriPattern
                action = it.intentAction
                mimeType = it.mimeType
            }
        },
    ) { backStackEntry ->
        val context = LocalContext.current
        val characterId = backStackEntry.arguments?.getInt(destination.argumentName)
        CharacterPage(
            characterId = characterId!!,
            viewModel = hiltViewModel(),
            networkConnection = hiltViewModel(),
            favoritesViewModel = hiltViewModel(),
            isExpandedWidth = isExpandedWidth,
            onBackPressed = navController::navigateUp,
            onLoadPage = { onLoadPage(context, it) },
            onShareLink = context::shareUrl,
            onOpenLink = {
                navController.navigateOrView(
                    context = context,
                    request = NavDeepLinkRequest(
                        uri = it,
                        action = destination.parent!!.value,
                        mimeType = null,
                    ),
                )
            },
        )
    }
}

@SuppressLint("RestrictedApi")
fun NavGraphBuilder.issueGraph(
    destination: Destination,
    onLoadPage: (Context, DetailsPage) -> Unit,
    navController: NavHostController,
    isExpandedWidth: Boolean,
) {
    composable(
        destination.route,
        arguments = listOf(
            navArgument(destination.argumentName!!) {
                type = NavType.IntType
            }
        ),
        deepLinks = destination.deepLinks.map {
            navDeepLink {
                uriPattern = it.uriPattern
                action = it.intentAction
                mimeType = it.mimeType
            }
        },
    ) { backStackEntry ->
        val context = LocalContext.current
        val issueId = backStackEntry.arguments?.getInt(destination.argumentName)
        IssuePage(
            issueId = issueId!!,
            viewModel = hiltViewModel(),
            networkConnection = hiltViewModel(),
            favoritesViewModel = hiltViewModel(),
            isExpandedWidth = isExpandedWidth,
            onBackPressed = navController::navigateUp,
            onLoadPage = { onLoadPage(context, it) },
            onShareLink = context::shareUrl,
            onOpenLink = {
                navController.navigateOrView(
                    context = context,
                    request = NavDeepLinkRequest(
                        uri = it,
                        action = destination.parent!!.value,
                        mimeType = null,
                    ),
                )
            },
        )
    }
}

@SuppressLint("RestrictedApi")
fun NavGraphBuilder.volumeGraph(
    destination: Destination,
    onLoadPage: (Context, DetailsPage) -> Unit,
    navController: NavHostController,
    isExpandedWidth: Boolean,
) {
    composable(
        destination.route,
        arguments = listOf(
            navArgument(destination.argumentName!!) {
                type = NavType.IntType
            }
        ),
        deepLinks = destination.deepLinks.map {
            navDeepLink {
                uriPattern = it.uriPattern
                action = it.intentAction
                mimeType = it.mimeType
            }
        },
    ) { backStackEntry ->
        val context = LocalContext.current
        val volumeId = backStackEntry.arguments?.getInt(destination.argumentName)
        VolumePage(
            volumeId = volumeId!!,
            viewModel = hiltViewModel(),
            filterViewModel = hiltViewModel(),
            networkConnection = hiltViewModel(),
            favoritesViewModel = hiltViewModel(),
            isExpandedWidth = isExpandedWidth,
            onBackPressed = navController::navigateUp,
            onLoadPage = { onLoadPage(context, it) },
            onShareLink = context::shareUrl,
            onOpenLink = {
                navController.navigateOrView(
                    context = context,
                    request = NavDeepLinkRequest(
                        uri = it,
                        action = destination.parent!!.value,
                        mimeType = null,
                    ),
                )
            },
        )
    }
}

fun NavGraphBuilder.favoritesGraph(
    navController: NavHostController,
    isExpandedWidth: Boolean,
) {
    val onResolveDetailsDestination = { destination: DetailsPage ->
        when (destination) {
            is DetailsPage.Character -> FavoritesDestination.Character
            else -> throw IllegalArgumentException("Unknown destination: $destination")
        }
    }
    val onResolveFavoritesDestination = { context: Context, destination: FavoritesPage ->
        when (destination) {
            is FavoritesPage.Character -> navController.navigateTo(
                FavoritesDestination.Character,
                argument = destination.characterId,
            )
            is FavoritesPage.Concept -> navController.navigateOrView(
                context = context,
                url = ComicVineUrlBuilder.concept(destination.conceptId).toUri(),
            )
            is FavoritesPage.Issue -> navController.navigateTo(
                FavoritesDestination.Issue,
                argument = destination.issueId,
            )
            is FavoritesPage.Location -> navController.navigateOrView(
                context = context,
                url = ComicVineUrlBuilder.location(destination.locationId).toUri(),
            )
            is FavoritesPage.Movie -> navController.navigateOrView(
                context = context,
                url = ComicVineUrlBuilder.movie(destination.movieId).toUri(),
            )
            is FavoritesPage.Object -> navController.navigateOrView(
                context = context,
                url = ComicVineUrlBuilder.objectUrl(destination.objectId).toUri(),
            )
            is FavoritesPage.Person -> navController.navigateOrView(
                context = context,
                url = ComicVineUrlBuilder.person(destination.personId).toUri(),
            )
            is FavoritesPage.Volume -> navController.navigateTo(
                FavoritesDestination.Volume,
                argument = destination.volumeId,
            )
            is FavoritesPage.StoryArc -> navController.navigateTo(
                FavoritesDestination.StoryArcs,
                argument = destination.storyArcId,
            )
            is FavoritesPage.Team -> navController.navigateTo(
                FavoritesDestination.Teams,
                argument = destination.teamId,
            )
            FavoritesPage.FavoriteCharacters ->
                navController navigateTo FavoritesDestination.Characters
            FavoritesPage.FavoriteConcepts ->
                navController navigateTo FavoritesDestination.Concepts
            FavoritesPage.FavoriteIssues ->
                navController navigateTo FavoritesDestination.Issues
            FavoritesPage.FavoriteLocations ->
                navController navigateTo FavoritesDestination.Locations
            FavoritesPage.FavoriteMovies ->
                navController navigateTo FavoritesDestination.Movies
            FavoritesPage.FavoriteObjects ->
                navController navigateTo FavoritesDestination.Objects
            FavoritesPage.FavoritePeople ->
                navController navigateTo FavoritesDestination.People
            FavoritesPage.FavoriteVolumes ->
                navController navigateTo FavoritesDestination.Volumes
            FavoritesPage.FavoriteStoryArcs ->
                navController navigateTo FavoritesDestination.StoryArcs
            FavoritesPage.FavoriteTeams ->
                navController navigateTo FavoritesDestination.Teams
        }
    }
    navigation(
        route = AppDestination.Favorites.route,
        startDestination = FavoritesDestination.Overview.route,
    ) {
        composable(FavoritesDestination.Overview.route) {
            val context = LocalContext.current
            FavoritesPage(
                viewModel = hiltViewModel(),
                favoritesViewModel = hiltViewModel(),
                networkConnection = hiltViewModel(),
                isExpandedWidth = isExpandedWidth,
                onLoadPage = { onResolveFavoritesDestination(context, it) },
            )
        }

        composable(FavoritesDestination.Characters.route) {
            val context = LocalContext.current
            FavoriteCharactersPage(
                viewModel = hiltViewModel(),
                filterViewModel = hiltViewModel(),
                networkConnection = hiltViewModel(),
                favoritesViewModel = hiltViewModel(),
                onLoadPage = { onResolveFavoritesDestination(context, it) },
                onBackButtonClicked = navController::navigateUp,
            )
        }

        composable(FavoritesDestination.Issues.route) {
            val context = LocalContext.current
            FavoriteIssuesPage(
                viewModel = hiltViewModel(),
                filterViewModel = hiltViewModel(),
                networkConnection = hiltViewModel(),
                favoritesViewModel = hiltViewModel(),
                onLoadPage = { onResolveFavoritesDestination(context, it) },
                onBackButtonClicked = navController::navigateUp,
            )
        }

        composable(FavoritesDestination.Volumes.route) {
            val context = LocalContext.current
            FavoriteVolumesPage(
                viewModel = hiltViewModel(),
                filterViewModel = hiltViewModel(),
                networkConnection = hiltViewModel(),
                favoritesViewModel = hiltViewModel(),
                onLoadPage = { onResolveFavoritesDestination(context, it) },
                onBackButtonClicked = navController::navigateUp,
            )
        }

        composable(FavoritesDestination.Concepts.route) {
            val context = LocalContext.current
            FavoriteConceptsPage(
                viewModel = hiltViewModel(),
                filterViewModel = hiltViewModel(),
                networkConnection = hiltViewModel(),
                favoritesViewModel = hiltViewModel(),
                onLoadPage = { onResolveFavoritesDestination(context, it) },
                onBackButtonClicked = navController::navigateUp,
            )
        }

        composable(FavoritesDestination.Locations.route) {
            val context = LocalContext.current
            FavoriteLocationsPage(
                viewModel = hiltViewModel(),
                filterViewModel = hiltViewModel(),
                networkConnection = hiltViewModel(),
                favoritesViewModel = hiltViewModel(),
                onLoadPage = { onResolveFavoritesDestination(context, it) },
                onBackButtonClicked = navController::navigateUp,
            )
        }

        composable(FavoritesDestination.Movies.route) {
            val context = LocalContext.current
            FavoriteMoviesPage(
                viewModel = hiltViewModel(),
                filterViewModel = hiltViewModel(),
                networkConnection = hiltViewModel(),
                favoritesViewModel = hiltViewModel(),
                onLoadPage = { onResolveFavoritesDestination(context, it) },
                onBackButtonClicked = navController::navigateUp,
            )
        }

        composable(FavoritesDestination.Objects.route) {
            val context = LocalContext.current
            FavoriteObjectsPage(
                viewModel = hiltViewModel(),
                filterViewModel = hiltViewModel(),
                networkConnection = hiltViewModel(),
                favoritesViewModel = hiltViewModel(),
                onLoadPage = { onResolveFavoritesDestination(context, it) },
                onBackButtonClicked = navController::navigateUp,
            )
        }

        composable(FavoritesDestination.People.route) {
            val context = LocalContext.current
            FavoritePeoplePage(
                viewModel = hiltViewModel(),
                filterViewModel = hiltViewModel(),
                networkConnection = hiltViewModel(),
                favoritesViewModel = hiltViewModel(),
                onLoadPage = { onResolveFavoritesDestination(context, it) },
                onBackButtonClicked = navController::navigateUp,
            )
        }

        composable(FavoritesDestination.StoryArcs.route) {
            val context = LocalContext.current
            FavoriteStoryArcsPage(
                viewModel = hiltViewModel(),
                filterViewModel = hiltViewModel(),
                networkConnection = hiltViewModel(),
                favoritesViewModel = hiltViewModel(),
                onLoadPage = { onResolveFavoritesDestination(context, it) },
                onBackButtonClicked = navController::navigateUp,
            )
        }

        composable(FavoritesDestination.Teams.route) {
            val context = LocalContext.current
            FavoriteTeamsPage(
                viewModel = hiltViewModel(),
                filterViewModel = hiltViewModel(),
                networkConnection = hiltViewModel(),
                favoritesViewModel = hiltViewModel(),
                onLoadPage = { onResolveFavoritesDestination(context, it) },
                onBackButtonClicked = navController::navigateUp,
            )
        }

        characterGraph(
            destination = FavoritesDestination.Character,
            navController = navController,
            isExpandedWidth = isExpandedWidth,
            onLoadPage = { context, page ->
                page.navigate(context, navController, onResolveDetailsDestination)
            }
        )

        issueGraph(
            destination = FavoritesDestination.Issue,
            navController = navController,
            isExpandedWidth = isExpandedWidth,
            onLoadPage = { context, page ->
                page.navigate(context, navController, onResolveDetailsDestination)
            }
        )

        volumeGraph(
            destination = FavoritesDestination.Volume,
            navController = navController,
            isExpandedWidth = isExpandedWidth,
            onLoadPage = { context, page ->
                page.navigate(context, navController, onResolveDetailsDestination)
            }
        )
    }
}

private fun Context.shareUrl(url: Uri) {
    ShareCompat.IntentBuilder(this)
        .setType("text/plain")
        .setChooserTitle(getString(R.string.share))
        .setText(url.toString())
        .startChooser()
}

private fun DetailsPage.navigate(
    context: Context,
    navController: NavHostController,
    onResolveDestination: (DetailsPage) -> Destination,
) {
    when (this) {
        is DetailsPage.Character -> navController.navigateTo(
            onResolveDestination(this),
            argument = characterId,
        )
        is DetailsPage.ImageViewer -> navController.navigateTo(
            AppDestination.ImageViewer,
            argument = Base64.encodeToString(url.toByteArray(), Base64.DEFAULT),
        )
        is DetailsPage.Issue -> navController.navigateTo(
            onResolveDestination(this),
            argument = issueId,
        )
        is DetailsPage.Volume -> navController.navigateTo(
            onResolveDestination(this),
            argument = volumeId,
        )
        is DetailsPage.Creator -> navController.navigateOrView(
            context = context,
            url = ComicVineUrlBuilder.creator(creatorId).toUri(),
        )
        is DetailsPage.Publisher -> navController.navigateOrView(
            context = context,
            url = ComicVineUrlBuilder.publisher(publisherId).toUri(),
        )
        is DetailsPage.Movie -> navController.navigateOrView(
            context = context,
            url = ComicVineUrlBuilder.movie(movieId).toUri(),
        )
        is DetailsPage.StoryArc -> navController.navigateOrView(
            context = context,
            url = ComicVineUrlBuilder.storyArc(storyArcId).toUri(),
        )
        is DetailsPage.Team -> navController.navigateOrView(
            context = context,
            url = ComicVineUrlBuilder.team(teamId).toUri(),
        )
        is DetailsPage.Person -> navController.navigateOrView(
            context = context,
            url = ComicVineUrlBuilder.person(personId).toUri(),
        )
        is DetailsPage.Concept -> navController.navigateOrView(
            context = context,
            url = ComicVineUrlBuilder.concept(conceptId).toUri(),
        )
        is DetailsPage.Location -> navController.navigateOrView(
            context = context,
            url = ComicVineUrlBuilder.location(locationId).toUri(),
        )
        is DetailsPage.Object -> navController.navigateOrView(
            context = context,
            url = ComicVineUrlBuilder.objectUrl(objectId).toUri(),
        )
    }
}