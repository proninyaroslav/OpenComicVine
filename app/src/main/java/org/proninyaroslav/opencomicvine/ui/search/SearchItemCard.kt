package org.proninyaroslav.opencomicvine.ui.search

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.SearchInfo
import org.proninyaroslav.opencomicvine.ui.components.FavoriteButton
import org.proninyaroslav.opencomicvine.ui.components.FavoriteSwipeableBox
import org.proninyaroslav.opencomicvine.ui.components.card.ImageCard
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import java.text.DateFormat

@Composable
fun SearchItemCard(
    characterInfo: SearchInfo.Character,
    isFavorite: Boolean?,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SearchItemCardImpl(
        title = characterInfo.name,
        imageUrl = characterInfo.image.squareMedium,
        fallbackImageUrl = characterInfo.image.originalUrl,
        description = characterInfo.descriptionShort,
        isFavorite = isFavorite,
        tags = {
            InfoTag(
                label = { Text(stringResource(R.string.character)) },
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_face_24),
                        contentDescription = null,
                    )
                }
            )
            InfoTag(
                label = {
                    Text(
                        pluralStringResource(
                            R.plurals.details_volume_count_of_issues_template,
                            characterInfo.countOfIssueAppearances,
                            characterInfo.countOfIssueAppearances,
                        )
                    )
                },
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_menu_book_24),
                        contentDescription = null,
                    )
                }
            )
            characterInfo.publisher?.run {
                InfoTag(
                    label = { Text(name) },
                    icon = {
                        Icon(
                            painterResource(R.drawable.ic_groups_24),
                            contentDescription = null,
                        )
                    }
                )
            }
        },
        onClick = onClick,
        onFavoriteClick = onFavoriteClick,
        modifier = modifier,
    )
}

@Composable
fun SearchItemCard(
    conceptInfo: SearchInfo.Concept,
    isFavorite: Boolean?,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SearchItemCardImpl(
        title = conceptInfo.name,
        imageUrl = conceptInfo.image.squareMedium,
        fallbackImageUrl = conceptInfo.image.originalUrl,
        description = conceptInfo.descriptionShort,
        isFavorite = isFavorite,
        tags = {
            InfoTag(
                label = { Text(stringResource(R.string.concept)) },
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_menu_book_24),
                        contentDescription = null,
                    )
                }
            )
        },
        onClick = onClick,
        onFavoriteClick = onFavoriteClick,
        modifier = modifier,
    )
}

@Composable
fun SearchItemCard(
    episodeInfo: SearchInfo.Episode,
    isFavorite: Boolean?,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateFormat = DateFormat.getDateInstance(DateFormat.SHORT)
    val title = episodeInfo.run {
        if (name == null) {
            stringResource(R.string.episode_title_template_without_name, series.name, episodeNumber)
        } else {
            stringResource(R.string.episode_title_template, series.name, episodeNumber, name)
        }
    }
    SearchItemCardImpl(
        title = title,
        imageUrl = episodeInfo.image.squareMedium,
        fallbackImageUrl = episodeInfo.image.originalUrl,
        description = episodeInfo.descriptionShort,
        isFavorite = isFavorite,
        tags = {
            InfoTag(
                label = { Text(stringResource(R.string.episode)) },
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_menu_book_24),
                        contentDescription = null,
                    )
                }
            )
            episodeInfo.airDate?.let {
                InfoTag(
                    label = { Text(dateFormat.format(it)) },
                    icon = {
                        Icon(
                            painterResource(R.drawable.ic_calendar_month_24),
                            contentDescription = null,
                        )
                    }
                )
            }
        },
        onClick = onClick,
        onFavoriteClick = onFavoriteClick,
        modifier = modifier,
    )
}

@Composable
fun SearchItemCard(
    issueInfo: SearchInfo.Issue,
    isFavorite: Boolean?,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateFormat = DateFormat.getDateInstance(DateFormat.SHORT)
    val title = issueInfo.run {
        if (name == null) {
            stringResource(R.string.issue_title_template_without_name, volume.name, issueNumber)
        } else {
            stringResource(R.string.issue_title_template, volume.name, issueNumber, name)
        }
    }
    SearchItemCardImpl(
        title = title,
        imageUrl = issueInfo.image.squareMedium,
        fallbackImageUrl = issueInfo.image.originalUrl,
        description = issueInfo.descriptionShort,
        isFavorite = isFavorite,
        tags = {
            InfoTag(
                label = { Text(stringResource(R.string.issue)) },
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_menu_book_24),
                        contentDescription = null,
                    )
                }
            )
            issueInfo.coverDate?.let {
                InfoTag(
                    label = { Text(dateFormat.format(it)) },
                    icon = {
                        Icon(
                            painterResource(R.drawable.ic_calendar_month_24),
                            contentDescription = null,
                        )
                    }
                )
            }
        },
        onClick = onClick,
        onFavoriteClick = onFavoriteClick,
        modifier = modifier,
    )
}

@Composable
fun SearchItemCard(
    locationInfo: SearchInfo.Location,
    isFavorite: Boolean?,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SearchItemCardImpl(
        title = locationInfo.name,
        imageUrl = locationInfo.image.squareMedium,
        fallbackImageUrl = locationInfo.image.originalUrl,
        description = locationInfo.descriptionShort,
        isFavorite = isFavorite,
        tags = {
            InfoTag(
                label = { Text(stringResource(R.string.location)) },
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_public_24),
                        contentDescription = null,
                    )
                }
            )
        },
        onClick = onClick,
        onFavoriteClick = onFavoriteClick,
        modifier = modifier,
    )
}

@Composable
fun SearchItemCard(
    objectInfo: SearchInfo.Object,
    isFavorite: Boolean?,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SearchItemCardImpl(
        title = objectInfo.name,
        imageUrl = objectInfo.image.squareMedium,
        fallbackImageUrl = objectInfo.image.originalUrl,
        description = objectInfo.descriptionShort,
        isFavorite = isFavorite,
        tags = {
            InfoTag(
                label = { Text(stringResource(R.string.things)) },
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_cube_outline_24),
                        contentDescription = null,
                    )
                }
            )
        },
        onClick = onClick,
        onFavoriteClick = onFavoriteClick,
        modifier = modifier,
    )
}

@Composable
fun SearchItemCard(
    personInfo: SearchInfo.Person,
    isFavorite: Boolean?,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SearchItemCardImpl(
        title = personInfo.name,
        imageUrl = personInfo.image.squareMedium,
        fallbackImageUrl = personInfo.image.originalUrl,
        description = personInfo.descriptionShort,
        isFavorite = isFavorite,
        tags = {
            InfoTag(
                label = { Text(stringResource(R.string.person)) },
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_person_24),
                        contentDescription = null,
                    )
                }
            )
        },
        onClick = onClick,
        onFavoriteClick = onFavoriteClick,
        modifier = modifier,
    )
}

@Composable
fun SearchItemCard(
    seriesInfo: SearchInfo.Series,
    isFavorite: Boolean?,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SearchItemCardImpl(
        title = seriesInfo.name,
        imageUrl = seriesInfo.image.squareMedium,
        fallbackImageUrl = seriesInfo.image.originalUrl,
        description = seriesInfo.descriptionShort,
        isFavorite = isFavorite,
        tags = {
            InfoTag(
                label = { Text(stringResource(R.string.series)) },
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_library_books_24),
                        contentDescription = null,
                    )
                }
            )
            seriesInfo.startYear?.let {
                InfoTag(
                    label = { Text(it) },
                    icon = {
                        Icon(
                            painterResource(R.drawable.ic_calendar_month_24),
                            contentDescription = null,
                        )
                    }
                )
            }
            InfoTag(
                label = {
                    Text(
                        pluralStringResource(
                            R.plurals.series_count_of_episodes_template,
                            seriesInfo.countOfEpisodes,
                            seriesInfo.countOfEpisodes,
                        )
                    )
                },
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_menu_book_24),
                        contentDescription = null,
                    )
                }
            )
            seriesInfo.publisher?.run {
                InfoTag(
                    label = { Text(name) },
                    icon = {
                        Icon(
                            painterResource(R.drawable.ic_groups_24),
                            contentDescription = null,
                        )
                    }
                )
            }
        },
        onClick = onClick,
        onFavoriteClick = onFavoriteClick,
        modifier = modifier,
    )
}

@Composable
fun SearchItemCard(
    storyArcInfo: SearchInfo.StoryArc,
    isFavorite: Boolean?,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SearchItemCardImpl(
        title = storyArcInfo.name,
        imageUrl = storyArcInfo.image.squareMedium,
        fallbackImageUrl = storyArcInfo.image.originalUrl,
        description = storyArcInfo.descriptionShort,
        isFavorite = isFavorite,
        tags = {
            InfoTag(
                label = { Text(stringResource(R.string.story_arc)) },
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_story_arc_24),
                        contentDescription = null,
                    )
                }
            )
            storyArcInfo.publisher?.run {
                InfoTag(
                    label = { Text(name) },
                    icon = {
                        Icon(
                            painterResource(R.drawable.ic_groups_24),
                            contentDescription = null,
                        )
                    }
                )
            }
        },
        onClick = onClick,
        onFavoriteClick = onFavoriteClick,
        modifier = modifier,
    )
}

@Composable
fun SearchItemCard(
    teamInfo: SearchInfo.Team,
    isFavorite: Boolean?,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SearchItemCardImpl(
        title = teamInfo.name,
        imageUrl = teamInfo.image.squareMedium,
        fallbackImageUrl = teamInfo.image.originalUrl,
        description = teamInfo.descriptionShort,
        isFavorite = isFavorite,
        tags = {
            InfoTag(
                label = { Text(stringResource(R.string.team)) },
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_groups_24),
                        contentDescription = null,
                    )
                }
            )
        },
        onClick = onClick,
        onFavoriteClick = onFavoriteClick,
        modifier = modifier,
    )
}

@Composable
fun SearchItemCard(
    videoInfo: SearchInfo.Video,
    isFavorite: Boolean?,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SearchItemCardImpl(
        title = videoInfo.name,
        imageUrl = videoInfo.image.squareMedium,
        fallbackImageUrl = videoInfo.image.originalUrl,
        description = videoInfo.descriptionShort,
        isFavorite = isFavorite,
        tags = {
            InfoTag(
                label = { Text(stringResource(R.string.video)) },
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_play_circle_24),
                        contentDescription = null,
                    )
                }
            )
        },
        onClick = onClick,
        onFavoriteClick = onFavoriteClick,
        modifier = modifier,
    )
}

@Composable
fun SearchItemCard(
    volumeInfo: SearchInfo.Volume,
    isFavorite: Boolean?,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SearchItemCardImpl(
        title = volumeInfo.name,
        imageUrl = volumeInfo.image.squareMedium,
        fallbackImageUrl = volumeInfo.image.originalUrl,
        description = volumeInfo.descriptionShort,
        isFavorite = isFavorite,
        tags = {
            InfoTag(
                label = { Text(stringResource(R.string.volume)) },
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_library_books_24),
                        contentDescription = null,
                    )
                }
            )
            volumeInfo.startYear?.let {
                InfoTag(
                    label = { Text(it) },
                    icon = {
                        Icon(
                            painterResource(R.drawable.ic_calendar_month_24),
                            contentDescription = null,
                        )
                    }
                )
            }
            InfoTag(
                label = {
                    Text(
                        pluralStringResource(
                            R.plurals.series_count_of_episodes_template,
                            volumeInfo.countOfIssues,
                            volumeInfo.countOfIssues,
                        )
                    )
                },
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_menu_book_24),
                        contentDescription = null,
                    )
                }
            )
            volumeInfo.publisher?.run {
                InfoTag(
                    label = { Text(name) },
                    icon = {
                        Icon(
                            painterResource(R.drawable.ic_groups_24),
                            contentDescription = null,
                        )
                    }
                )
            }
        },
        onClick = onClick,
        onFavoriteClick = onFavoriteClick,
        modifier = modifier,
    )
}

@Composable
private fun InfoTag(
    label: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = AbsoluteRoundedCornerShape(20.dp),
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Box(modifier = Modifier.size(14.dp)) {
                icon()
            }
            Spacer(modifier = Modifier.width(4.dp))
            ProvideTextStyle(MaterialTheme.typography.labelSmall) {
                label()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchItemCardImpl(
    modifier: Modifier = Modifier,
    title: String,
    imageUrl: String?,
    fallbackImageUrl: String?,
    description: String?,
    isFavorite: Boolean?,
    tags: @Composable () -> Unit = {},
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
) {
    var imageWidth by remember { mutableStateOf(120.dp) }

    FavoriteSwipeableBox(
        isFavorite = isFavorite ?: false,
        icon = {
            isFavorite?.let {
                FavoriteButton(
                    isFavorite = isFavorite,
                    onClick = onFavoriteClick,
                )
            }
        },
        actionLabel = stringResource(R.string.add_to_favorite)
    ) {
        Card(
            onClick = onClick,
            modifier = modifier,
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                ImageCard(
                    imageUrl = imageUrl,
                    fallbackImageUrl = fallbackImageUrl,
                    imageWidth = imageWidth,
                    onImageWidthChanged = { imageWidth = it },
                    imageDescription = title,
                    placeholder = R.drawable.placeholder_square,
                    imageAspectRatio = 1f, // Square,
                    modifier = Modifier
                        .requiredWidth(imageWidth)
                        .padding(end = 16.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    TitleText(title)
                    FlowRow(
                        mainAxisSpacing = 4.dp,
                        crossAxisSpacing = 4.dp,
                        modifier = Modifier.padding(vertical = 8.dp),
                        content = tags,
                    )
                    description?.let { DescriptionText(description) }
                }
            }
        }
    }
}

@Composable
private fun TitleText(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        title,
        maxLines = 2,
        style = MaterialTheme.typography.titleMedium,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

@Composable
private fun DescriptionText(
    description: String,
    modifier: Modifier = Modifier,
) {
    Text(
        description,
        style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}

@Preview
@Composable
fun PreviewInfoTag() {
    OpenComicVineTheme {
        InfoTag(
            label = { Text("Character") },
            icon = {
                Icon(
                    painterResource(R.drawable.ic_face_24),
                    contentDescription = null
                )
            }
        )
    }
}

@Preview
@Composable
private fun PreviewSearchItemCardImpl() {
    OpenComicVineTheme {
        var isFavorite by remember { mutableStateOf(false) }
        LazyColumn {
            item {
                SearchItemCardImpl(
                    title = "Title",
                    imageUrl = "https://dummyimage.com/320",
                    fallbackImageUrl = "https://dummyimage.com/320",
                    description = "Description",
                    isFavorite = isFavorite,
                    tags = {
                        InfoTag(
                            label = { Text("Character") },
                            icon = {
                                Icon(
                                    painterResource(R.drawable.ic_face_24),
                                    contentDescription = null
                                )
                            }
                        )
                        InfoTag(
                            label = { Text("DC Comics") },
                            icon = {
                                Icon(
                                    painterResource(R.drawable.ic_groups_24),
                                    contentDescription = null
                                )
                            }
                        )
                    },
                    onClick = {},
                    onFavoriteClick = { isFavorite = !isFavorite },
                )
            }
        }
    }
}

@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewSearchItemCardImpl_Dark() {
    OpenComicVineTheme {
        var isFavorite by remember { mutableStateOf(false) }
        LazyColumn {
            item {
                SearchItemCardImpl(
                    title = "Title",
                    imageUrl = "https://dummyimage.com/320",
                    fallbackImageUrl = "https://dummyimage.com/320",
                    description = "Description",
                    isFavorite = isFavorite,
                    onClick = {},
                    onFavoriteClick = { isFavorite = !isFavorite },
                )
            }
        }
    }
}

@Preview(name = "No description")
@Composable
private fun PreviewSearchItemCardImpl_NoDescription() {
    OpenComicVineTheme {
        var isFavorite by remember { mutableStateOf(false) }
        LazyColumn {
            item {
                SearchItemCardImpl(
                    title = "Title",
                    imageUrl = "https://dummyimage.com/320",
                    fallbackImageUrl = "https://dummyimage.com/320",
                    description = null,
                    isFavorite = isFavorite,
                    onClick = {},
                    onFavoriteClick = { isFavorite = !isFavorite },
                )
            }
        }
    }
}