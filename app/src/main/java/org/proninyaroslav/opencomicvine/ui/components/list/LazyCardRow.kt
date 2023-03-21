package org.proninyaroslav.opencomicvine.ui.components.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun LazyCardRow(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyListState = rememberLazyListState(),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: LazyListScope.() -> Unit,
) {
    LazyRow(
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        content = content,
        contentPadding = contentPadding,
        state = state,
        modifier = modifier,
    )
}

@Preview(showBackground = true, widthDp = 840)
@Composable
fun PreviewLazyCardRow() {
    OpenComicVineTheme {
        LazyCardRow {
            items((0..10).toList()) {
                Card(modifier = Modifier.sizeIn(150.dp, 200.dp)) {}
            }
        }
    }
}