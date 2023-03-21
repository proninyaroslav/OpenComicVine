package org.proninyaroslav.opencomicvine.ui.components.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun LazyCardColumn(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyListState = rememberLazyListState(),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content,
        contentPadding = contentPadding,
        state = state,
        modifier = modifier,
    )
}

@Preview(showBackground = true, widthDp = 840)
@Composable
private fun PreviewLazyCardColumn() {
    OpenComicVineTheme {
        LazyCardColumn {
            items((0..10).toList()) {
                Card(modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()) {}
            }
        }
    }
}