package org.proninyaroslav.opencomicvine.ui.components.categories

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun CategoryHeader(
    @DrawableRes icon: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.padding(horizontal = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
                .padding(4.dp),
        ) {
            Icon(
                painterResource(icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(30.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(end = 16.dp, bottom = 2.dp),
            )
            Icon(
                painterResource(
                    if (LocalLayoutDirection.current == LayoutDirection.Rtl) {
                        R.drawable.ic_chevron_left_24
                    } else {
                        R.drawable.ic_chevron_right_24
                    }
                ),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, top = 1.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCategoryHeader() {
    OpenComicVineTheme {
        CategoryHeader(
            icon = R.drawable.ic_face_24,
            label = stringResource(R.string.characters),
            onClick = {},
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewCategoryHeaderDark() {
    OpenComicVineTheme {
        Surface {
            CategoryHeader(
                icon = R.drawable.ic_face_24,
                label = stringResource(R.string.characters),
                onClick = {},
            )
        }
    }
}