package org.proninyaroslav.opencomicvine.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.applyTonalElevation
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            painterResource(R.drawable.ic_arrow_back_24),
            contentDescription = stringResource(R.string.back),
        )
    }
}

@Composable
fun FilledTonalBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalIconButton(
        onClick = onClick,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.applyTonalElevation(
                backgroundColor = MaterialTheme.colorScheme.surface,
                elevation = 3.0.dp,
            ),
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        modifier = modifier,
    ) {
        Icon(
            painterResource(R.drawable.ic_arrow_back_24),
            contentDescription = stringResource(R.string.back),
        )
    }
}

@Preview
@Composable
private fun PreviewBackButton() {
    OpenComicVineTheme {
        BackButton(onClick = {})
    }
}

@Preview(name = "Filled Tonal")
@Composable
private fun PreviewBackButton_FilledTonal() {
    OpenComicVineTheme {
        FilledTonalBackButton(onClick = {})
    }
}