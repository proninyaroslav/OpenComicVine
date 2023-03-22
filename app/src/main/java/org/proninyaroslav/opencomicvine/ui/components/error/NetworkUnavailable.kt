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

package org.proninyaroslav.opencomicvine.ui.components.error

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.components.AnimatedSlideContent
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun NetworkNotAvailable(
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    if (compact) {
        ElevatedCard(
            modifier = modifier
                .defaultMinSize(minHeight = 50.dp)
                // Limit the width for large screens
                .wrapContentWidth(align = Alignment.Start)
                .widthIn(max = 550.dp),
        ) {
            Body(
                compact = true,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            )
        }
    } else {
        Body(
            compact = false,
            modifier = modifier
        )
    }
}

@Composable
fun NetworkNotAvailableSlider(
    modifier: Modifier = Modifier,
    targetState: Boolean,
    alignment: Alignment.Vertical = Alignment.Top,
    compact: Boolean = false
) {
    AnimatedSlideContent(
        targetState = targetState,
        alignment = alignment,
    ) { show ->
        if (show) {
            NetworkNotAvailable(
                compact = compact,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun Body(
    compact: Boolean,
    modifier: Modifier = Modifier,
) {
    val textStyle = MaterialTheme.typography.run {
        if (compact) titleMedium else headlineSmall
    }
    Row(
        modifier = modifier,
    ) {
        Icon(
            painterResource(R.drawable.ic_cloud_off_24),
            contentDescription = "",
            modifier = Modifier
                .padding(end = 8.dp)
                .size(if (compact) 28.dp else 36.dp)
        )
        Text(
            stringResource(R.string.network_unavailable),
            style = textStyle,
            modifier = Modifier.padding(
                top = if (compact) 3.dp else 0.dp
            ),
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewNetworkNotAvailable() {
    OpenComicVineTheme {
        Box(contentAlignment = Alignment.Center) {
            NetworkNotAvailable()
        }
    }
}

@Preview("Compact", showSystemUi = true)
@Composable
fun PreviewNetworkNotAvailable_Compact() {
    OpenComicVineTheme {
        Box(contentAlignment = Alignment.Center) {
            NetworkNotAvailable(
                compact = true,
                modifier = Modifier.width(200.dp),
            )
        }
    }
}

@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewNetworkNotAvailableDark() {
    OpenComicVineTheme {
        Surface {
            Box(contentAlignment = Alignment.Center) {
                NetworkNotAvailable()
            }
        }
    }
}

@Preview("Compact Dark", showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewNetworkNotAvailable_CompactDark() {
    OpenComicVineTheme {
        Box(contentAlignment = Alignment.Center) {
            NetworkNotAvailable(
                compact = true,
                modifier = Modifier.width(200.dp),
            )
        }
    }
}
