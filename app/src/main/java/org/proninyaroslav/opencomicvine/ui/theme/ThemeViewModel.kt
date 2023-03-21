package org.proninyaroslav.opencomicvine.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.proninyaroslav.opencomicvine.data.preferences.PrefTheme
import org.proninyaroslav.opencomicvine.model.AppPreferences
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    pref: AppPreferences,
) : ViewModel() {

    val theme = pref.theme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PrefTheme.Unknown,
        )
}