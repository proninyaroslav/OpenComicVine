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