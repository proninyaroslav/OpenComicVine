package org.proninyaroslav.opencomicvine.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.applyTonalElevation
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun FilledTonalActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = IconButtonDefaults.filledShape,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    FilledTonalIconButton(
        onClick = onClick,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.applyTonalElevation(
                backgroundColor = MaterialTheme.colorScheme.surface,
                elevation = 3.0.dp,
            ),
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        modifier = modifier.padding(horizontal = 2.dp),
        enabled = enabled,
        shape = shape,
        interactionSource = interactionSource,
        content = content,
    )
}

@Preview
@Composable
private fun PreviewFilledTonalActionButton() {
    OpenComicVineTheme {
        FilledTonalActionButton(
            onClick = {},
        ) {
            Icon(
                painterResource(R.drawable.ic_home_24),
                contentDescription = null,
            )
        }
    }
}