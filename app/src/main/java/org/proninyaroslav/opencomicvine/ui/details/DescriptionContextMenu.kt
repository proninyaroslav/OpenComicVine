package org.proninyaroslav.opencomicvine.ui.details

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

enum class DescriptionContextMenuAction {
    CopyLink,
    ShareLink,
}

@Composable
fun DescriptionContextMenu(
    title: String,
    onDismiss: () -> Unit,
    onAction: (DescriptionContextMenuAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        title = {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Item(
                    leadingIcon = R.drawable.ic_content_copy_24,
                    text = stringResource(R.string.copy_link),
                    onClick = { onAction(DescriptionContextMenuAction.CopyLink) },
                )
                Item(
                    leadingIcon = R.drawable.ic_share_24,
                    text = stringResource(R.string.share_link),
                    onClick = { onAction(DescriptionContextMenuAction.ShareLink) },
                )
            }
        },
        onDismissRequest = onDismiss,
        modifier = modifier,
    )
}

@Composable
private fun Item(
    @DrawableRes leadingIcon: Int,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DropdownMenuItem(
        leadingIcon = {
            Icon(
                painterResource(leadingIcon),
                contentDescription = null,
            )
        },
        text = {
            Text(
                text,
                style = MaterialTheme.typography.titleMedium,
            )
        },
        onClick = onClick,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun PreviewDescriptionContextMenu() {
    OpenComicVineTheme {
        DescriptionContextMenu(
            title = "https://example.org",
            onDismiss = {},
            onAction = {},
        )
    }
}