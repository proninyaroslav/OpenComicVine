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

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun AppNavRail(
    selectedItem: NavBarItemInfo?,
    onSelected: (NavBarItemInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationRail(modifier = modifier) {
        Spacer(Modifier.weight(1f))
        NavBarItemInfo.values().forEach { item ->
            val selected = selectedItem == item
            NavigationRailItem(
                icon = { Icon(painterResource(item.icon), contentDescription = "") },
                label = if (selected) {
                    { Text(stringResource(item.label)) }
                } else {
                    null
                },
                selected = selected,
                onClick = { onSelected(item) },
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        Spacer(Modifier.weight(1f))
    }
}

@Preview
@Composable
fun PreviewAppNavRail() {
    var selectedItem by remember {
        mutableStateOf(NavBarItemInfo.Home)
    }
    OpenComicVineTheme {
        AppNavRail(
            selectedItem = selectedItem,
            onSelected = { selectedItem = it }
        )
    }
}
