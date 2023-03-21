// Copyright 2021 Alexander Karkossa
// Copyright 2022 Yaroslav Pronin
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

@file:OptIn(ExperimentalMaterialApi::class)

package org.proninyaroslav.opencomicvine.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.ResistanceConfig
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.SwipeableState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

typealias RevealState = SwipeableState<RevealValue>

/**
 * @param onContentClick called on click
 * @param closeOnContentClick if true, returns to unrevealed state on content click
 */
@Composable
fun RevealSwipe(
    modifier: Modifier = Modifier,
    enableSwipe: Boolean = true,
    onContentClick: (() -> Unit)? = null,
    backgroundStartActionLabel: String?,
    onBackgroundStartClick: () -> Boolean = { true },
    backgroundEndActionLabel: String?,
    onBackgroundEndClick: () -> Boolean = { true },
    closeOnContentClick: Boolean = true,
    closeOnBackgroundClick: Boolean = true,
    shape: CornerBasedShape = RevealSwipeDefaults.shape,
    maxRevealDp: Dp = RevealSwipeDefaults.maxRevealDp,
    maxAmountOfOverflow: Dp = RevealSwipeDefaults.maxAmountOfOverflow,
    directions: Set<RevealDirection> = setOf(
        RevealDirection.StartToEnd,
        RevealDirection.EndToStart
    ),
    backgroundCardModifier: Modifier = modifier,
    backgroundCardElevation: CardElevation = RevealSwipeDefaults.backgroundCardElevation,
    backgroundCardColors: CardColors = RevealSwipeDefaults.backgroundCardColors,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    state: RevealState = rememberRevealState(),
    hiddenContentEnd: @Composable RowScope.() -> Unit = {},
    hiddenContentStart: @Composable RowScope.() -> Unit = {},
    content: @Composable () -> Unit,
) {
    val closeOnContentClickHandler = remember(coroutineScope, state) {
        {
            if (state.targetValue != RevealValue.Default) {
                coroutineScope.launch {
                    state.reset()
                }
            }
        }
    }

    val backgroundStartClick = remember(coroutineScope, state, onBackgroundStartClick) {
        {
            if (state.targetValue == RevealValue.FullyRevealedEnd && closeOnBackgroundClick) {
                coroutineScope.launch {
                    state.reset()
                }
            }
            onBackgroundStartClick()
        }
    }

    val backgroundEndClick = remember(coroutineScope, state, onBackgroundEndClick) {
        {
            if (state.targetValue == RevealValue.FullyRevealedStart && closeOnBackgroundClick) {
                coroutineScope.launch {
                    state.reset()
                }
            }
            onBackgroundEndClick()
        }
    }

    Box(modifier = Modifier.wrapContentSize()) {
        val maxRevealPx = with(LocalDensity.current) { maxRevealDp.toPx() }

        // non swipable with hidden content
        Card(
            colors = backgroundCardColors,
            modifier = backgroundCardModifier.matchParentSize(),
            shape = shape,
            elevation = backgroundCardElevation,
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .fillMaxHeight()
                        .clickable(
                            onClick = {
                                backgroundStartClick()
                            }
                        ),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    content = hiddenContentStart,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight()
                        .clickable(
                            onClick = {
                                backgroundEndClick()
                            }
                        ),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    content = hiddenContentEnd,
                )
            }
        }

        SwipeableContent(
            enableSwipe = enableSwipe,
            isOpen = state.targetValue != RevealValue.Default,
            backgroundStartActionLabel = backgroundStartActionLabel,
            onBackgroundStartClick = onBackgroundStartClick,
            backgroundEndActionLabel = backgroundEndActionLabel,
            onBackgroundEndClick = onBackgroundEndClick,
            onContentClick = onContentClick,
            closeOnContentClick = closeOnContentClick,
            closeOnContentClickHandler = closeOnContentClickHandler,
            modifier = modifier.then(
                if (enableSwipe) {
                    Modifier
                        .offset {
                            IntOffset(
                                state.offset.value.roundToInt(),
                                0
                            )
                        }
                        .revealSwipeable(
                            state = state,
                            maxRevealPx = maxRevealPx,
                            maxAmountOfOverflow = maxAmountOfOverflow,
                            directions = directions
                        )
                } else {
                    Modifier
                }
            ),
            content = content,
        )
    }
}

@Composable
private fun SwipeableContent(
    modifier: Modifier = Modifier,
    enableSwipe: Boolean,
    isOpen: Boolean,
    backgroundStartActionLabel: String?,
    onBackgroundStartClick: () -> Boolean,
    backgroundEndActionLabel: String?,
    onBackgroundEndClick: () -> Boolean,
    onContentClick: (() -> Unit)?,
    closeOnContentClick: Boolean,
    closeOnContentClickHandler: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .then(
                if (enableSwipe) {
                    Modifier
                        .semantics {
                            customActions = buildList {
                                backgroundStartActionLabel?.let {
                                    add(
                                        CustomAccessibilityAction(
                                            it,
                                            onBackgroundStartClick
                                        )
                                    )
                                }
                                backgroundEndActionLabel?.let {
                                    add(
                                        CustomAccessibilityAction(
                                            it,
                                            onBackgroundEndClick
                                        )
                                    )
                                }
                            }
                        }
                } else {
                    Modifier
                }
            )
            .then(
                if (onContentClick != null && !closeOnContentClick) {
                    Modifier.clickable(
                        onClick = onContentClick
                    )
                } else if (onContentClick == null && closeOnContentClick) {
                    // if no onContentClick handler passed, add click handler with no indication to enable close on content click
                    Modifier.clickable(
                        onClick = closeOnContentClickHandler,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
                } else if (onContentClick != null) {
                    // decide based on state:
                    // 1. if open, just close without indication
                    // 2. if closed, call click handler
                    Modifier.clickable(
                        onClick =
                        {
                            // if open, just close. No click event.
                            if (isOpen) {
                                closeOnContentClickHandler()
                            } else {
                                onContentClick()
                            }
                        },
                        // no indication if just closing
                        indication = if (isOpen) null else LocalIndication.current,
                        interactionSource = remember { MutableInteractionSource() }
                    )
                } else Modifier
            )
    ) {
        content()
    }
}

private fun Modifier.revealSwipeable(
    maxRevealPx: Float,
    maxAmountOfOverflow: Dp,
    directions: Set<RevealDirection>,
    state: RevealState,
) = composed {

    val maxAmountOfOverflowPx = with(LocalDensity.current) { maxAmountOfOverflow.toPx() }

    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val anchors = mutableMapOf(0f to RevealValue.Default)

    if (RevealDirection.StartToEnd in directions) anchors += maxRevealPx to RevealValue.FullyRevealedEnd
    if (RevealDirection.EndToStart in directions) anchors += -maxRevealPx to RevealValue.FullyRevealedStart

    val thresholds = { _: RevealValue, _: RevealValue ->
        FractionalThreshold(0.5f)
    }

    val minFactor =
        if (RevealDirection.EndToStart in directions) SwipeableDefaults.StandardResistanceFactor else SwipeableDefaults.StiffResistanceFactor
    val maxFactor =
        if (RevealDirection.StartToEnd in directions) SwipeableDefaults.StandardResistanceFactor else SwipeableDefaults.StiffResistanceFactor

    Modifier.swipeable(
        state = state,
        anchors = anchors,
        thresholds = thresholds,
        orientation = Orientation.Horizontal,
        enabled = true, // state.value == RevealValue.Default,
        reverseDirection = isRtl,
        resistance = ResistanceConfig(
            basis = maxAmountOfOverflowPx,
            factorAtMin = minFactor,
            factorAtMax = maxFactor
        )
    )
}

enum class RevealDirection {
    /**
     * Can be dismissed by swiping in the reading direction.
     */
    StartToEnd,

    /**
     * Can be dismissed by swiping in the reverse of the reading direction.
     */
    EndToStart,
}

/**
 * Possible values of [RevealState].
 */
enum class RevealValue {
    /**
     * Indicates the component has not been revealed yet.
     */
    Default,

    /**
     * Fully revealed to end
     */
    FullyRevealedEnd,

    /**
     * Fully revealed to start
     */
    FullyRevealedStart,
}

object RevealSwipeDefaults {
    val maxRevealDp = 75.dp

    val shape
        @Composable get() = MaterialTheme.shapes.medium

    val maxAmountOfOverflow = 250.dp

    val backgroundCardElevation
        @Composable get() = CardDefaults.cardElevation()

    val backgroundCardContainerColor
        @Composable get() = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)

    val backgroundCardContentColor
        @Composable get() = MaterialTheme.colorScheme.onSurface

    val backgroundCardColors
        @Composable get() = CardDefaults.cardColors(
            containerColor = backgroundCardContainerColor,
            contentColor = backgroundCardContentColor
        )
}

/**
 * Create and [remember] a [RevealState] with the default animation clock.
 *
 * @param initialValue The initial value of the state.
 * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
 */
@Composable
fun rememberRevealState(
    initialValue: RevealValue = RevealValue.Default,
    confirmStateChange: (RevealValue) -> Boolean = { true },
): RevealState {
    return rememberSwipeableState(
        initialValue = initialValue,
        confirmStateChange = confirmStateChange
    )
}

/**
 * Reset the component to the default position, with an animation.
 */
suspend fun RevealState.reset() {
    animateTo(
        targetValue = RevealValue.Default,
    )
}

@Preview
@Composable
private fun RevealSwipegPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.height(600.dp)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(10.dp)
            ) {
                item {
                    RevealSwipe(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        directions = setOf(
                            RevealDirection.StartToEnd,
                            RevealDirection.EndToStart
                        ),
                        backgroundStartActionLabel = "Delete entry",
                        backgroundEndActionLabel = "Mark as favorite",
                        hiddenContentStart = {
                            Icon(
                                modifier = Modifier.padding(horizontal = 25.dp),
                                imageVector = Icons.Outlined.Star,
                                contentDescription = null,
                            )
                        },
                        hiddenContentEnd = {
                            Icon(
                                modifier = Modifier.padding(horizontal = 25.dp),
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null
                            )
                        }
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .requiredHeight(80.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF505160),
                            ),
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    modifier = Modifier.padding(start = 20.dp),
                                    text = "Both directions"
                                )
                            }
                        }
                    }
                }
                item {
                    RevealSwipe(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        closeOnContentClick = false,
                        closeOnBackgroundClick = false,
                        backgroundStartActionLabel = null,
                        backgroundEndActionLabel = null,
                        directions = setOf(
                            RevealDirection.StartToEnd,
                            RevealDirection.EndToStart
                        ),
                        hiddenContentStart = {
                            Icon(
                                modifier = Modifier.padding(horizontal = 25.dp),
                                imageVector = Icons.Outlined.Star,
                                contentDescription = null,
                            )
                        },
                        hiddenContentEnd = {
                            Icon(
                                modifier = Modifier.padding(horizontal = 25.dp),
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null
                            )
                        }
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .requiredHeight(80.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF68829E),
                            ),
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    modifier = Modifier.padding(start = 20.dp),
                                    text = "Both directions.\ncloseOnClick = false"
                                )
                            }
                        }
                    }
                }
                item {
                    RevealSwipe(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        directions = setOf(
                            RevealDirection.StartToEnd,
                        ),
                        backgroundStartActionLabel = null,
                        backgroundEndActionLabel = null,
                        hiddenContentStart = {
                            Icon(
                                modifier = Modifier.padding(horizontal = 25.dp),
                                imageVector = Icons.Outlined.Star,
                                contentDescription = null,
                            )
                        },
                        hiddenContentEnd = {
                            Icon(
                                modifier = Modifier.padding(horizontal = 25.dp),
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null
                            )
                        }
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .requiredHeight(80.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFAEBD38),
                            ),
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    modifier = Modifier.padding(start = 20.dp),
                                    text = "StartToEnd"
                                )
                            }
                        }
                    }
                }
                item {
                    RevealSwipe(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        directions = setOf(
                            RevealDirection.EndToStart,
                        ),
                        backgroundStartActionLabel = null,
                        backgroundEndActionLabel = null,
                        hiddenContentStart = {
                            Icon(
                                modifier = Modifier.padding(horizontal = 25.dp),
                                imageVector = Icons.Outlined.Star,
                                contentDescription = null,
                            )
                        },
                        hiddenContentEnd = {
                            Icon(
                                modifier = Modifier.padding(horizontal = 25.dp),
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null
                            )
                        }
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .requiredHeight(80.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF598234),
                            ),
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    modifier = Modifier.padding(start = 20.dp),
                                    text = "EndToStart"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
