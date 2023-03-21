package org.proninyaroslav.opencomicvine.ui.details.category.character

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.LocalAppSnackbarState
import org.proninyaroslav.opencomicvine.ui.details.DetailsPage
import org.proninyaroslav.opencomicvine.ui.details.category.DetailsCategoryPage
import org.proninyaroslav.opencomicvine.ui.details.category.DetailsEffect
import org.proninyaroslav.opencomicvine.ui.details.category.DetailsEvent
import org.proninyaroslav.opencomicvine.ui.details.category.DetailsState
import org.proninyaroslav.opencomicvine.ui.viewmodel.FavoritesEvent
import org.proninyaroslav.opencomicvine.ui.viewmodel.FavoritesViewModel
import org.proninyaroslav.opencomicvine.ui.viewmodel.NetworkConnectionViewModel

private const val TAG = "CharacterPage"

@Composable
fun CharacterPage(
    characterId: Int,
    viewModel: CharacterViewModel,
    networkConnection: NetworkConnectionViewModel,
    favoritesViewModel: FavoritesViewModel,
    isExpandedWidth: Boolean,
    onBackPressed: () -> Unit,
    onLoadPage: (DetailsPage) -> Unit,
    onOpenLink: (Uri) -> Unit,
    onShareLink: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current
    val snackbarState = LocalAppSnackbarState.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(viewModel) {
        viewModel.event(DetailsEvent.Load(characterId))
        viewModel.effect.collect { effect ->
            when (effect) {
                is DetailsEffect.CacheLoadFailed -> {
                    Log.e(
                        TAG,
                        "Unable to load character info from cache, id = $characterId",
                        effect.exception
                    )
                }
            }
        }
    }

    val item = when (val s = state) {
        is DetailsState.CacheLoaded -> s.details
        is DetailsState.Loaded -> s.details
        is DetailsState.LoadFailed -> s.details
        else -> null
    }
    val otherInfoState by remember(state) {
        mutableStateOf(
            when (val s = state) {
                is DetailsState.Loaded -> s.relatedEntities.run {
                    CharacterOtherInfoState(
                        movies = movies,
                        issues = issues,
                        volumes = volumes,
                        storyArcs = storyArcs,
                        friends = friends,
                        enemies = enemies,
                        teams = teams,
                        teamEnemies = teamEnemies,
                        teamFriends = teamFriends,
                    )
                }
                else -> null
            }
        )
    }

    DetailsCategoryPage(
        type = DetailsCategoryPage.Type.Characters(characterId),
        title = item?.details?.name,
        image = item?.details?.image,
        fullDescription = item?.details?.description,
        state = state,
        errorMessageTemplates = DetailsCategoryPage.ErrorMessageTemplates(
            fetchTemplate = R.string.fetch_character_error_template,
        ),
        shortDescriptionHeader = { isFullLoaded ->
            CharacterDescriptionHeader(
                details = item?.details,
                isExpandedWidth = isExpandedWidth,
                isFullLoaded = isFullLoaded,
                onImageClick = { onLoadPage(DetailsPage.ImageViewer(it)) },
            )
        },
        generalInfo = {
            CharacterGeneralInfo(
                details = item?.details,
                onLoadPage = onLoadPage,
                onCopyToClipboard = {
                    clipboardManager.setText(AnnotatedString(it))
                    coroutineScope.launch {
                        snackbarState.showSnackbar(
                            context.getString(R.string.copied_to_clipboard)
                        )
                    }
                },
            )
        },
        otherInfo = {
            CharacterOtherInfo(
                state = otherInfoState,
                toSourceError = viewModel::toSourceError,
                onLoadPage = onLoadPage,
                onFavoriteClick = { entityId, entityType ->
                    favoritesViewModel.event(
                        FavoritesEvent.SwitchFavorite(
                            entityId = entityId,
                            entityType = entityType,
                        )
                    )
                },
                onReport = { viewModel.event(DetailsEvent.ErrorReport(it)) },
            )
        },
        networkConnection = networkConnection,
        favoritesViewModel = favoritesViewModel,
        isExpandedWidth = isExpandedWidth,
        onRefresh = { viewModel.event(DetailsEvent.Load(characterId)) },
        onBackPressed = onBackPressed,
        onOpenLink = onOpenLink,
        onShareLink = onShareLink,
        onReport = { viewModel.event(DetailsEvent.ErrorReport(it)) },
        modifier = modifier,
    )
}