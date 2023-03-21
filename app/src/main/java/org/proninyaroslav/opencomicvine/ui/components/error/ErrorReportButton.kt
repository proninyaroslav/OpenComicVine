package org.proninyaroslav.opencomicvine.ui.components.error

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun ErrorReportButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalButton(
        onClick = onClick,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
        ),
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painterResource(R.drawable.ic_report_24),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(18.dp)
            )
            Text(stringResource(R.string.report))
        }
    }
}

@Composable
fun ErrorReportOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tint = MaterialTheme.colorScheme.onErrorContainer
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = tint
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(tint)),
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painterResource(R.drawable.ic_report_24),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(18.dp)
            )
            Text(stringResource(R.string.report))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewErrorReportButton() {
    OpenComicVineTheme {
        ErrorReportButton(onClick = {})
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PreviewErrorReportButtonDark() {
    OpenComicVineTheme {
        Surface {
            ErrorReportButton(onClick = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewErrorReportOutlinedButton() {
    OpenComicVineTheme {
        ErrorReportOutlinedButton(onClick = {})
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PreviewErrorReportOutlinedButtonDark() {
    OpenComicVineTheme {
        Surface {
            ErrorReportOutlinedButton(onClick = {})
        }
    }
}