package org.proninyaroslav.opencomicvine.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.components.card.CardSummaryText
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun DetailsShortInfo(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        val textStyle = MaterialTheme.typography.bodyMedium
        ProvideTextStyle(value = textStyle) {
            content()
        }
    }
}

@Preview
@Composable
private fun PreviewDetailsShortInfo() {
    OpenComicVineTheme {
        DetailsShortInfo {
            CardSummaryText(
                text = "Date added",
                icon = R.drawable.ic_calendar_month_24,
            )
            CardSummaryText(
                text = "Date last updated",
                icon = R.drawable.ic_calendar_month_24,
            )
        }
    }
}