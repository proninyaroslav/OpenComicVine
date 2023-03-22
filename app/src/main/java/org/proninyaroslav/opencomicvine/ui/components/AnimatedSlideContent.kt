/*
 * Copyright (C) 2023 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of OpenComicVine.
 *
 * OpenComicVine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenComicVine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenComicVine.  If not, see <http://www.gnu.org/licenses/>.
 */

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
