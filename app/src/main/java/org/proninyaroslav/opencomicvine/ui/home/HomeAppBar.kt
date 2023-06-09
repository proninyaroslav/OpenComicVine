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

package org.proninyaroslav.opencomicvine.ui.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.components.AppLogo
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(
    modifier: Modifier = Modifier,
    onLoadPage: (HomePage) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        CenterAlignedTopAppBar(
            title = { AppLogo(size = 55.dp) },
            actions = {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        painterResource(R.drawable.ic_more_vert_24),
                        contentDescription = stringResource(R.string.menu)
                    )
                }
            },
            scrollBehavior = scrollBehavior,
        )
        Menu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            onLoadPage = {
                showMenu = false
                onLoadPage(it)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp),
        )
    }
}

@Composable
private fun Menu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onLoadPage: (HomePage) -> Unit,
    modifier: Modifier
) {
    Box(modifier = modifier) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = Modifier.widthIn(min = 200.dp),
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.settings)) },
                trailingIcon = {
                    Icon(
                        painterResource(R.drawable.ic_settings_24),
                        contentDescription = null,
                    )
                },
                onClick = { onLoadPage(HomePage.Settings) },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.about_app)) },
                trailingIcon = {
                    Icon(
                        painterResource(R.drawable.ic_info_outline_24),
                        contentDescription = null,
                    )
                },
                onClick = { onLoadPage(HomePage.About) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewHomeAppBar() {
    OpenComicVineTheme {
        HomeAppBar(
            onLoadPage = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewHomeAppBarDark() {
    OpenComicVineTheme {
        HomeAppBar(
            onLoadPage = {},
        )
    }
}
