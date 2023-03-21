package org.proninyaroslav.opencomicvine.ui.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.proninyaroslav.opencomicvine.ui.components.defaultPlaceholder
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun DetailsPlaceholderText(
    text: String?,
    visible: Boolean,
    isExpandedWidth: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .defaultPlaceholder(
                visible = visible,
            )
    ) {
        Text(
            text ?: "",
            modifier = modifier
                .then(
                    if (visible) {
                        Modifier.fillMaxWidth(if (isExpandedWidth) 0.25f else 1f)
                    } else {
                        Modifier
                    }
                ),
        )
    }
}

@Preview
@Composable
private fun PreviewDetailsPlaceholderText() {
    OpenComicVineTheme {
        DetailsPlaceholderText(
            text = "Text",
            visible = false,
            isExpandedWidth = false,
        )
    }
}

@Preview(name = "Loading")
@Composable
private fun PreviewDetailsPlaceholderText_Loading() {
    OpenComicVineTheme {
        DetailsPlaceholderText(
            text = "Text",
            visible = true,
            isExpandedWidth = false,
        )
    }
}

@Preview(
    name = "Expanded width",
    device = "spec:shape=Normal,width=1280,height=800,unit=dp,dpi=480"
)
@Composable
private fun PreviewDetailsPlaceholderText_ExpandedWidth() {
    OpenComicVineTheme {
        DetailsPlaceholderText(
            text = "Text",
            visible = false,
            isExpandedWidth = true,
        )
    }
}

@Preview(
    name = "Loading with expanded width",
    device = "spec:shape=Normal,width=1280,height=800,unit=dp,dpi=480"
)
@Composable
private fun PreviewDetailsPlaceholderText_LoadingWithExpandedWidth() {
    OpenComicVineTheme {
        Box(modifier = Modifier.fillMaxWidth()) {
            DetailsPlaceholderText(
                text = "Test",
                visible = true,
                isExpandedWidth = true,
            )
        }
    }
}