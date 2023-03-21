package org.proninyaroslav.opencomicvine.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun CustomTabIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    height: Dp = 3.dp,
    edgePadding: Dp = 20.dp,
    bottomPadding: Dp = 1.dp,
) {
    Box(
        modifier
            .padding(
                start = edgePadding,
                end = edgePadding,
                bottom = bottomPadding,
            )
            .clip(
                RoundedCornerShape(
                    topStart = 2.dp,
                    topEnd = 2.dp,
                    bottomEnd = 0.dp,
                    bottomStart = 0.dp,
                )
            )
            .fillMaxWidth()
            .height(height + bottomPadding)
            .background(color),
    )
}

@Preview
@Composable
private fun PreviewCustomTabIndicator() {
    OpenComicVineTheme {
        TabRow(
            selectedTabIndex = 1,
            indicator = { tabPositions ->
                CustomTabIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[1]),
                )
            }
        ) {
            Tab(
                selected = false,
                onClick = {},
                text = { Text("Tab 1") }
            )
            Tab(
                selected = true,
                onClick = {},
                text = { Text("Tab 2") }
            )
            Tab(
                selected = false,
                onClick = {},
                text = { Text("Tab 3") }
            )
        }
    }
}