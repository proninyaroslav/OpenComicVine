package org.proninyaroslav.opencomicvine.ui.components

import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <S> AnimatedSlideContent(
    targetState: S,
    alignment: Alignment.Vertical,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.(targetState: S) -> Unit,
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            ContentTransform(
                targetContentEnter = slideInVertically { fullHeight ->
                    if (alignment == Alignment.Bottom) fullHeight else -fullHeight
                } + fadeIn(
                    initialAlpha = 0.3f
                ),
                initialContentExit = slideOutVertically { fullHeight ->
                    if (alignment == Alignment.Bottom) -fullHeight else fullHeight
                } + fadeOut(),
            )
        },
        modifier = modifier,
        content = content,
    )
}