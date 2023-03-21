package org.proninyaroslav.opencomicvine.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

val DefaultCustomBadgeSize = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBadge(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Badge(
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        modifier = modifier,
        content = content,
    )
}

@Preview
@Composable
private fun PreviewCustomBadge() {
    OpenComicVineTheme {
        CustomBadge {
            Text("100")
        }
    }
}

@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewCustomBadge_Dark() {
    OpenComicVineTheme {
        CustomBadge {
            Text("100")
        }
    }
}