package org.proninyaroslav.opencomicvine.ui.components.chip

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.inverse
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun OpenContentChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val direction = LocalLayoutDirection.current
    CompositionLocalProvider(LocalLayoutDirection provides direction.inverse()) {
        SuggestionChip(
            label = {
                CompositionLocalProvider(LocalLayoutDirection provides direction) {
                    Text(label, modifier = Modifier.padding(vertical = 4.dp))
                }
            },
            icon = {
                Icon(
                    painterResource(
                        if (LocalLayoutDirection.current == LayoutDirection.Ltr) {
                            R.drawable.ic_chevron_left_24
                        } else {
                            R.drawable.ic_chevron_right_24
                        }
                    ),
                    contentDescription = stringResource(R.string.open),
                )
            },
            onClick = onClick,
            modifier = modifier,
        )
    }
}

@Preview
@Composable
fun PreviewOpenContentChip() {
    OpenComicVineTheme {
        OpenContentChip(
            label = "Label",
            onClick = {},
        )
    }
}

@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewOpenContentChip_Dark() {
    OpenComicVineTheme {
        Surface {
            OpenContentChip(
                label = "Label",
                onClick = {},
            )
        }
    }
}