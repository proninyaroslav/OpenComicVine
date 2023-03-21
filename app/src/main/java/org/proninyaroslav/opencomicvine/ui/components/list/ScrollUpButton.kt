package org.proninyaroslav.opencomicvine.ui.components.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

val ScrollUpButtonHeight = 40.0.dp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ScrollUpButton(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(),
        exit = scaleOut(),
        modifier = modifier
    ) {
        SmallFloatingActionButton(
            onClick = onClick,
            modifier = Modifier.padding(16.dp),
        ) {
            Icon(
                painterResource(R.drawable.ic_keyboard_arrow_up_24),
                contentDescription = stringResource(R.string.scroll_up),
            )
        }
    }
}

@Preview
@Composable
fun PreviewScrollUpButton() {
    OpenComicVineTheme {
        ScrollUpButton(
            visible = true,
            onClick = {},
        )
    }
}