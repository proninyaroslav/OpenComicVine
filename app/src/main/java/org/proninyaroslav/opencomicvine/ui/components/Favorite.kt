package org.proninyaroslav.opencomicvine.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.components.card.CardWithImage
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        AnimatedIcon(isFavorite = isFavorite)
    }
}

@Composable
fun FavoriteFilledTonalActionButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalActionButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        AnimatedIcon(isFavorite = isFavorite)
    }
}

@Composable
fun FavoriteFilledTonalButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalIconButton(
        onClick = onClick,
        modifier = modifier.size(32.dp),
    ) {
        AnimatedIcon(isFavorite = isFavorite)
    }
}

@Composable
@OptIn(ExperimentalAnimationApi::class)
private fun AnimatedIcon(
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
    favoriteTint: Color = MaterialTheme.colorScheme.primary,
) {
    AnimatedContent(targetState = isFavorite) { isFavoriteVal ->
        Icon(
            painterResource(
                if (isFavoriteVal) {
                    R.drawable.ic_favorite_filled_24
                } else {
                    R.drawable.ic_favorite_24
                }
            ),
            tint = if (isFavoriteVal) favoriteTint else LocalContentColor.current,
            contentDescription = stringResource(R.string.add_to_favorite),
            modifier = modifier.offset(y = 2.dp),
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FavoriteSwipeableBox(
    isFavorite: Boolean,
    icon: @Composable () -> Unit,
    actionLabel: String?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    RevealSwipe(
        directions = setOf(RevealDirection.StartToEnd),
        hiddenContentStart = {
            Box(
                contentAlignment = Alignment.TopStart,
                modifier = modifier.fillMaxSize(),
            ) {
                icon()
            }
        },
        backgroundCardColors = animateBackgroundCardColors(isFavorite),
        maxRevealDp = FavoriteBoxDefaults.maxRevealDp,
        closeOnContentClick = false,
        backgroundStartActionLabel = actionLabel,
        backgroundEndActionLabel = null,
        modifier = modifier.clipToBounds(),
        content = content,
    )
}

@Composable
fun FavoriteBox(
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    iconAlignment: Alignment = Alignment.TopEnd,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        content()
        Box(
            modifier = Modifier
                .align(iconAlignment)
                .padding(4.dp),
        ) {
            icon()
        }
    }
}

@Composable
fun animateBackgroundCardColors(isFavorite: Boolean): CardColors {
    val backgroundContainerColor by animateColorAsState(
        if (isFavorite) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            RevealSwipeDefaults.backgroundCardContainerColor
        }
    )
    val backgroundContentColor by animateColorAsState(
        if (isFavorite) {
            MaterialTheme.colorScheme.onSecondaryContainer
        } else {
            RevealSwipeDefaults.backgroundCardContentColor
        }
    )

    return CardDefaults.cardColors(
        containerColor = backgroundContainerColor,
        contentColor = backgroundContentColor,
    )
}

object FavoriteBoxDefaults {
    val maxRevealDp = 48.dp
}

@Preview
@Composable
fun PreviewFavoriteButton() {
    OpenComicVineTheme {
        var isFavorite by remember { mutableStateOf(false) }
        FavoriteButton(
            isFavorite = isFavorite,
            onClick = { isFavorite = !isFavorite },
        )
    }
}

@Preview
@Composable
fun PreviewFavoriteFilledTonalActionButton() {
    OpenComicVineTheme {
        var isFavorite by remember { mutableStateOf(false) }
        FavoriteFilledTonalActionButton(
            isFavorite = isFavorite,
            onClick = { isFavorite = !isFavorite },
        )
    }
}

@Preview
@Composable
fun PreviewFavoriteFilledTonalButton() {
    OpenComicVineTheme {
        var isFavorite by remember { mutableStateOf(false) }
        FavoriteFilledTonalButton(
            isFavorite = isFavorite,
            onClick = { isFavorite = !isFavorite },
        )
    }
}

@Preview
@Composable
fun PreviewFavoriteSwipeableBox() {
    val titleStyle = MaterialTheme.typography.titleMedium
    OpenComicVineTheme {
        var isFavorite by remember { mutableStateOf(false) }
        FavoriteSwipeableBox(
            isFavorite = isFavorite,
            icon = {
                FavoriteButton(
                    isFavorite = isFavorite,
                    onClick = { isFavorite = !isFavorite },
                )
            },
            actionLabel = stringResource(R.string.add_to_favorite),
        ) {
            CardWithImage(
                imageUrl = "https://dummyimage.com/320",
                imageDescription = "Dummy image",
                placeholder = R.drawable.placeholder_square,
                onClick = {},
            ) {
                Text(
                    "Title",
                    style = titleStyle,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}

@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewFavoriteSwipeableBox_Dark() {
    val titleStyle = MaterialTheme.typography.titleMedium
    OpenComicVineTheme {
        var isFavorite by remember { mutableStateOf(false) }
        FavoriteSwipeableBox(
            isFavorite = isFavorite,
            icon = {
                FavoriteButton(
                    isFavorite = isFavorite,
                    onClick = { isFavorite = !isFavorite },
                )
            },
            actionLabel = stringResource(R.string.add_to_favorite),
        ) {
            CardWithImage(
                imageUrl = "https://dummyimage.com/320",
                imageDescription = "Dummy image",
                placeholder = R.drawable.placeholder_square,
                onClick = {},
            ) {
                Text(
                    "Title",
                    style = titleStyle,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewFavoriteBox() {
    val titleStyle = MaterialTheme.typography.titleMedium
    OpenComicVineTheme {
        var isFavorite by remember { mutableStateOf(false) }
        FavoriteBox(
            icon = {
                FavoriteFilledTonalButton(
                    isFavorite = isFavorite,
                    onClick = { isFavorite = !isFavorite },
                )
            },
        ) {
            CardWithImage(
                imageUrl = "https://dummyimage.com/320",
                imageDescription = "Dummy image",
                placeholder = R.drawable.placeholder_square,
                onClick = {},
            ) {
                Text(
                    "Title",
                    style = titleStyle,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}

@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewFavoriteBox_Dark() {
    val titleStyle = MaterialTheme.typography.titleMedium
    OpenComicVineTheme {
        var isFavorite by remember { mutableStateOf(false) }
        FavoriteBox(
            icon = {
                FavoriteFilledTonalButton(
                    isFavorite = isFavorite,
                    onClick = { isFavorite = !isFavorite },
                )
            },
        ) {
            CardWithImage(
                imageUrl = "https://dummyimage.com/320",
                imageDescription = "Dummy image",
                placeholder = R.drawable.placeholder_square,
                onClick = {},
            ) {
                Text(
                    "Title",
                    style = titleStyle,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}