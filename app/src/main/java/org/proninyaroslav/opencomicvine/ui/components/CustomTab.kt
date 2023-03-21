package org.proninyaroslav.opencomicvine.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun CustomTab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    selectedContentColor: Color = MaterialTheme.colorScheme.primary,
    unselectedContentColor: Color = LocalContentColor.current.copy(alpha = 0.87f),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    Tab(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        text = text?.let {
            {
                Box(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    text()
                }
            }
        },
        icon = icon,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        interactionSource = interactionSource,
    )
}

@Preview(name = "Selected")
@Composable
fun PreviewCustomTab_Selected() {
    OpenComicVineTheme {
        CustomTab(
            selected = true,
            text = { Text("Tab") },
            onClick = {},
        )
    }
}

@Preview(name = "Unselected")
@Composable
fun PreviewCustomTab_Unselected() {
    OpenComicVineTheme {
        CustomTab(
            selected = false,
            text = { Text("Tab") },
            onClick = {},
        )
    }
}