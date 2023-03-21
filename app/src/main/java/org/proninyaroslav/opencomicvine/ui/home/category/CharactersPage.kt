package org.proninyaroslav.opencomicvine.ui.home.category

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.util.Pair
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.preferences.PrefRecentCharactersFilter
import org.proninyaroslav.opencomicvine.data.preferences.PrefRecentCharactersFilterBundle
import org.proninyaroslav.opencomicvine.ui.components.DateRangePickerDialog
import org.proninyaroslav.opencomicvine.ui.components.card.CharacterCard
import org.proninyaroslav.opencomicvine.ui.components.list.EmptyListPlaceholder
import org.proninyaroslav.opencomicvine.ui.home.HomePage
import org.proninyaroslav.opencomicvine.ui.home.category.filter.*
import org.proninyaroslav.opencomicvine.ui.viewmodel.*
import java.util.*

@Composable
fun CharactersPage(
    viewModel: RecentCategoryPageViewModel,
    networkConnection: NetworkConnectionViewModel,
    datePickerViewModel: DatePickerViewModel,
    filterViewModel: CharactersFilterViewModel,
    favoritesViewModel: FavoritesViewModel,
    onLoadPage: (HomePage) -> Unit,
    onBackButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val characters = viewModel.charactersList.collectAsLazyPagingItems()
    val filterState by filterViewModel.state.collectAsStateWithLifecycle()
    val showApplyButton by remember(filterState) {
        derivedStateOf {
            when (val s = filterState) {
                is CharactersFilterState.FiltersChanged -> s.isNeedApply
                else -> false
            }
        }
    }
    val datePickerState by datePickerViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(filterViewModel) {
        filterViewModel.effect.collect { effect ->
            when (effect) {
                CharactersFilterEffect.Applied -> characters.refresh()
            }
        }
    }

    DateRangePickerDialog(
        show = when (datePickerState) {
            DatePickerState.Hide,
            DatePickerState.Initial -> false
            is DatePickerState.Show<*> -> true
        },
        titleText = R.string.date_picker_select_dates,
        initRange = when (val s = datePickerState) {
            DatePickerState.Hide,
            DatePickerState.Initial -> null
            is DatePickerState.Show<*> -> s.range
        },
        onPositiveClicked = { selection ->
            when (val s = datePickerState) {
                DatePickerState.Hide -> {}
                DatePickerState.Initial -> {}
                is DatePickerState.Show<*> -> {
                    val filter = handleDatePickerResult(
                        filterState = filterState,
                        type = s.dialogType as CharactersDatePickerType,
                        selection = selection,
                    )
                    filter?.let {
                        filterViewModel.event(
                            CharactersFilterEvent.ChangeFilters(
                                filterBundle = filter,
                            )
                        )
                    }
                }
            }
        },
        onHide = { datePickerViewModel.event(DatePickerEvent.Hide) },
    )

    RecentCategoryPage(
        type = RecentCategoryPageType.Characters,
        title = { Text(stringResource(R.string.recent_characters)) },
        itemCard = { item ->
            CharacterCard(
                characterInfo = item.info,
                onClick = { onLoadPage(HomePage.Character(item.id)) }
            )
        },
        emptyListPlaceholder = {
            EmptyListPlaceholder(
                icon = R.drawable.ic_face_24,
                label = stringResource(R.string.no_characters),
            )
        },
        filterDrawerContent = {
            charactersFilter(
                filterBundle = filterState.filterBundle,
                onFiltersChanged = {
                    filterViewModel.event(
                        CharactersFilterEvent.ChangeFilters(filterBundle = it)
                    )
                },
                onDatePickerDialogShow = { type, date ->
                    datePickerViewModel.event(
                        DatePickerEvent.Show(
                            dialogType = type,
                            range = date.run { Pair(first.time, second.time) }
                        )
                    )
                },
            )
        },
        items = characters,
        errorMessageTemplates = RecentCategoryPage.ErrorMessageTemplates(
            fetchTemplate = R.string.fetch_characters_list_error_template,
            saveTemplate = R.string.cache_characters_list_error_template,
        ),
        showApplyButton = showApplyButton,
        onApplyFilter = { filterViewModel.event(CharactersFilterEvent.Apply) },
        viewModel = viewModel,
        networkConnection = networkConnection,
        favoritesViewModel = favoritesViewModel,
        onBackButtonClicked = onBackButtonClicked,
        modifier = modifier,
    )
}

private fun handleDatePickerResult(
    filterState: CharactersFilterState,
    type: CharactersDatePickerType,
    selection: Pair<Long, Long>,
): PrefRecentCharactersFilterBundle? =
    when (type) {
        CharactersDatePickerType.Unknown -> null
        CharactersDatePickerType.DateAdded -> filterState.filterBundle.copy(
            dateAdded = PrefRecentCharactersFilter.DateAdded.InRange(
                start = Date(selection.first),
                end = Date(selection.second),
            ),
        )
    }