package org.proninyaroslav.opencomicvine.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun AppLogo(
    size: Dp,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.size(size)) {
        Image(
            painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier.requiredSize(size * 2),
        )
    }
}

@Preview
@Composable
fun PreviewAppLogo() {
    OpenComicVineTheme {
        AppLogo(size = 64.dp)
    }
}