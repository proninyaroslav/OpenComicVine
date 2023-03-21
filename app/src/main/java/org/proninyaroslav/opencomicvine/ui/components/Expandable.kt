package org.proninyaroslav.opencomicvine.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

private const val EXPANSION_TRANSITION_DURATION = 200

@Composable
fun ExpandableCard(
    title: @Composable () -> Unit,
    expanded: Boolean,
    onHeaderClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(),
    content: @Composable () -> Unit,
) {
    Card(
        colors = colors,
        modifier = modifier,
    ) {
        ExpandableContainer(
            title = title,
            content = content,
            expanded = expanded,
            onHeaderClick = onHeaderClick,
        )
    }
}

@Composable
fun ExpandableOutlinedCard(
    title: @Composable () -> Unit,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    onHeaderClick: () -> Unit,
    colors: CardColors = CardDefaults.outlinedCardColors(),
    border: BorderStroke = CardDefaults.outlinedCardBorder(),
    content: @Composable () -> Unit,
) {
    OutlinedCard(
        colors = colors,
        border = border,
        modifier = modifier,
    ) {
        ExpandableContainer(
            title = title,
            content = content,
            expanded = expanded,
            onHeaderClick = onHeaderClick,
        )
    }
}

@Composable
fun ExpandableContainer(
    title: @Composable () -> Unit,
    expanded: Boolean,
    onHeaderClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val transition = updateTransition(expanded, label = "transition")
    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = EXPANSION_TRANSITION_DURATION)
    }, label = "rotationDegreeTransition") {
        if (it) 0f else 180f
    }

    Column(
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onHeaderClick)
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f),
            ) {
                ProvideTextStyle(MaterialTheme.typography.titleMedium) {
                    title()
                }
            }
            ExpandArrow(
                degrees = arrowRotationDegree,
                onClick = onHeaderClick,
            )
        }
        ExpandableContent(
            content = content,
            visible = expanded,
        )
    }
}

@Composable
private fun ExpandArrow(
    degrees: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_expand_less_24),
            contentDescription = stringResource(R.string.expandable_arrow),
            modifier = Modifier.rotate(degrees),
        )
    }
}

@Composable
private fun ExpandableContent(
    content: @Composable () -> Unit,
    visible: Boolean = true,
) {
    val enterTransition = remember {
        expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(EXPANSION_TRANSITION_DURATION)
        ) + fadeIn(
            initialAlpha = 0.3f,
            animationSpec = tween(EXPANSION_TRANSITION_DURATION)
        )
    }
    val exitTransition = remember {
        shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(EXPANSION_TRANSITION_DURATION)
        ) + fadeOut(
            animationSpec = tween(EXPANSION_TRANSITION_DURATION)
        )
    }

    AnimatedVisibility(
        visible = visible,
        enter = enterTransition,
        exit = exitTransition,
    ) {
        content()
    }
}

@Preview
@Composable
fun PreviewExpandableCard() {
    OpenComicVineTheme {
        var expanded by remember { mutableStateOf(false) }
        ExpandableCard(
            title = { Text("Title") },
            content = {
                Text(
                    "Content",
                    modifier = Modifier.size(200.dp),
                )
            },
            onHeaderClick = { expanded = !expanded },
            expanded = expanded,
        )
    }
}

@Preview
@Composable
fun PreviewExpandableCard_Expanded() {
    OpenComicVineTheme {
        ExpandableCard(
            title = { Text("Title") },
            content = {
                Text(
                    "Content",
                    modifier = Modifier.size(200.dp),
                )
            },
            onHeaderClick = {},
            expanded = true,
        )
    }
}

@Preview
@Composable
fun PreviewExpandableOutlinedCard_Expanded() {
    OpenComicVineTheme {
        ExpandableOutlinedCard(
            title = { Text("Title") },
            content = {
                Text(
                    "Content",
                    modifier = Modifier.size(200.dp),
                )
            },
            onHeaderClick = {},
            expanded = true,
        )
    }
}