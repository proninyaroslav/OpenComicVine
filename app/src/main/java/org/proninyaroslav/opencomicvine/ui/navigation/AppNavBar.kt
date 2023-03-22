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

package org.proninyaroslav.opencomicvine.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun AppNavBar(
    selectedItem: NavBarItemInfo?,
    onSelected: (NavBarItemInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
        NavBarItemInfo.values().forEach { item ->
            NavigationBarItem(
                icon = { Icon(painterResource(item.icon), contentDescription = "") },
                label = { Text(stringResource(item.label)) },
                selected = selectedItem == item,
                onClick = { onSelected(item) }
            )
        }
    }
}

@Preview
@Composable
fun PreviewAppNavBar() {
    var selectedItem by remember {
        mutableStateOf(NavBarItemInfo.Home)
    }
    OpenComicVineTheme {
        AppNavBar(
            selectedItem = selectedItem,
            onSelected = { selectedItem = it }
        )
    }
}
