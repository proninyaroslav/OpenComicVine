package org.proninyaroslav.opencomicvine.ui.details

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.ui.components.ExpandableCard

@Composable
fun DetailsExpandableCard(
    title: String,
    expanded: Boolean,
    onHeaderClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(),
    content: @Composable () -> Unit,
) {
    ExpandableCard(
        title = {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp),
            )
        },
        colors = colors,
        expanded = expanded,
        onHeaderClick = onHeaderClick,
        modifier = modifier,
        content = content,
    )
}