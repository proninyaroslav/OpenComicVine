package org.proninyaroslav.opencomicvine.ui.components.error

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ErrorPageScaffold(
    vararg content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    bottomAction: (@Composable () -> Unit) = {},
    contentInCard: Boolean = false,
) {
    if (contentInCard) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
            ),
            modifier = modifier
                // Limit the width for large screens
                .wrapContentWidth(align = Alignment.Start)
                .widthIn(max = 550.dp)
        ) {
            Body(
                content = content,
                bottomAction = bottomAction,
                contentInCard = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    } else {
        Body(
            content = content,
            bottomAction = bottomAction,
            contentInCard = false,
            modifier = modifier,
        )
    }
}

@Composable
private fun Body(
    vararg content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    bottomAction: (@Composable () -> Unit) = {},
    contentInCard: Boolean,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = if (contentInCard) {
            Alignment.Start
        } else {
            Alignment.CenterHorizontally
        },
        modifier = modifier.padding(16.dp),
    ) {
        content.forEach { it() }
        Box(
            modifier = Modifier.align(
                if (contentInCard) {
                    Alignment.End
                } else {
                    Alignment.CenterHorizontally
                }
            )
        ) {
            bottomAction()
        }
    }
}