package org.proninyaroslav.opencomicvine.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.data.ImageInfo
import org.proninyaroslav.opencomicvine.ui.components.list.ScrollUpButton
import org.proninyaroslav.opencomicvine.ui.components.list.ScrollUpButtonHeight
import org.proninyaroslav.opencomicvine.ui.components.rememberTopAppBarWithImageScrollBehavior
import org.proninyaroslav.opencomicvine.ui.removeBottomPadding
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun DetailsScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    headerContent: @Composable () -> Unit = {},
    secondaryContent: @Composable () -> Unit = {},
    isExpandedWidth: Boolean,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    mainContent: @Composable () -> Unit,
) {
    Scaffold(
        topBar = topBar,
        modifier = modifier,
    ) { scaffoldPadding ->
        val scrollState = rememberScrollState()
        val direction = LocalLayoutDirection.current
        val newContentPadding by remember(scaffoldPadding, contentPadding, isExpandedWidth) {
            derivedStateOf {
                scaffoldPadding.removeBottomPadding(
                    direction = direction,
                    extraPadding = contentPadding,
                )
            }
        }
        val coroutineScope = rememberCoroutineScope()

        BoxWithConstraints {
            val showScrollUpButton by remember(scrollState, maxHeight) {
                derivedStateOf { scrollState.value > maxHeight.value }
            }
            Box(modifier = Modifier.fillMaxSize()) {
                if (isExpandedWidth && this@BoxWithConstraints.maxWidth >= 960.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(newContentPadding),
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .weight(2f)
                                .fillMaxHeight(),
                        ) {
                            headerContent()
                            mainContent()
                            if (showScrollUpButton) {
                                ScrollUpButtonSpacer()
                            }
                        }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(start = 16.dp),
                        ) {
                            secondaryContent()
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(newContentPadding),
                    ) {
                        headerContent()
                        secondaryContent()
                        mainContent()
                        if (showScrollUpButton) {
                            ScrollUpButtonSpacer()
                        }
                    }
                }

                ScrollUpButton(
                    visible = showScrollUpButton,
                    onClick = {
                        coroutineScope.launch { scrollState.scrollTo(0) }
                    },
                    modifier = Modifier.align(Alignment.BottomEnd),
                )
            }
        }
    }
}

@Composable
fun ScrollUpButtonSpacer() {
    Spacer(modifier = Modifier.height(ScrollUpButtonHeight + 16.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewDetailsScaffold() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val isExpandedWidth = false
    val image = ImageInfo(
        iconUrl = "",
        mediumUrl = "https://comicvine.gamespot.com/a/uploads/scale_medium/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
        screenUrl = "",
        screenLargeUrl = "https://comicvine.gamespot.com/a/uploads/screen_kubrick/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
        smallUrl = "https://comicvine.gamespot.com/a/uploads/scale_small/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
        superUrl = "",
        thumbUrl = "",
        tinyUrl = "",
        originalUrl = "https://comicvine.gamespot.com/a/uploads/original/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
        imageTags = null,
    )
    OpenComicVineTheme {
        DetailsScaffold(
            topBar = {
                ComicVineBannerAppBar(
                    title = "Title",
                    image = image,
                    isExpandedWidth = isExpandedWidth,
                    onBackButtonClicked = {},
                )
            },
            headerContent = {
                Text("Header content")
            },
            secondaryContent = {
                Text("Secondary content 1")
                Text("Secondary content 2")
                Text("Secondary content 3")
            },
            isExpandedWidth = isExpandedWidth,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            Text("Content")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(device = "spec:shape=Normal,width=1280,height=800,unit=dp,dpi=480")
@Composable
private fun PreviewDetailsScaffold_ExpandedWidth() {
    val scrollBehavior = rememberTopAppBarWithImageScrollBehavior()
    val isExpandedWidth = true
    val image = ImageInfo(
        iconUrl = "",
        mediumUrl = "https://comicvine.gamespot.com/a/uploads/scale_medium/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
        screenUrl = "",
        screenLargeUrl = "https://comicvine.gamespot.com/a/uploads/screen_kubrick/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
        smallUrl = "https://comicvine.gamespot.com/a/uploads/scale_small/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
        superUrl = "",
        thumbUrl = "",
        tinyUrl = "",
        originalUrl = "https://comicvine.gamespot.com/a/uploads/original/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
        imageTags = null,
    )
    OpenComicVineTheme {
        DetailsScaffold(
            topBar = {
                ComicVineBannerAppBar(
                    title = "Title",
                    image = image,
                    isExpandedWidth = isExpandedWidth,
                    scrollBehavior = scrollBehavior,
                    onBackButtonClicked = {},
                )
            },
            headerContent = {
                Text("Header content")
            },
            secondaryContent = {
                Text("Secondary content 1")
                Text("Secondary content 2")
                Text("Secondary content 3")
            },
            isExpandedWidth = isExpandedWidth,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            Text("Content")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(device = "spec:shape=Normal,width=900,height=800,unit=dp,dpi=480")
@Composable
private fun PreviewDetailsScaffold_ExpandedWidthPortrait() {
    val scrollBehavior = rememberTopAppBarWithImageScrollBehavior()
    val isExpandedWidth = true
    val image = ImageInfo(
        iconUrl = "",
        mediumUrl = "https://comicvine.gamespot.com/a/uploads/scale_medium/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
        screenUrl = "",
        screenLargeUrl = "https://comicvine.gamespot.com/a/uploads/screen_kubrick/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
        smallUrl = "https://comicvine.gamespot.com/a/uploads/scale_small/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
        superUrl = "",
        thumbUrl = "",
        tinyUrl = "",
        originalUrl = "https://comicvine.gamespot.com/a/uploads/original/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
        imageTags = null,
    )
    OpenComicVineTheme {
        DetailsScaffold(
            topBar = {
                ComicVineBannerAppBar(
                    title = "Title",
                    image = image,
                    isExpandedWidth = isExpandedWidth,
                    scrollBehavior = scrollBehavior,
                    onBackButtonClicked = {},
                )
            },
            headerContent = {
                Text("Header content")
            },
            secondaryContent = {
                Text("Secondary content 1")
                Text("Secondary content 2")
                Text("Secondary content 3")
            },
            isExpandedWidth = isExpandedWidth,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            Text("Content")
        }
    }
}