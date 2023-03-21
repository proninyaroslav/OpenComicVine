package org.proninyaroslav.opencomicvine.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.placeholder

fun Modifier.defaultPlaceholder(
    visible: Boolean,
    shape: Shape? = null,
): Modifier = composed {
    Modifier.placeholder(
        visible = visible,
        color = material3Colors(),
        shape = shape ?: MaterialTheme.shapes.small,
        highlight = PlaceholderHighlight.fade(),
    )
}

@Composable
private fun material3Colors(
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    contentAlpha: Float = 0.1f,
): Color = contentColor.copy(contentAlpha).compositeOver(backgroundColor)
