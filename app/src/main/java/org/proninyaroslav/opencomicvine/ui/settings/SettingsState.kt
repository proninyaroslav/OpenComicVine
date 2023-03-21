package org.proninyaroslav.opencomicvine.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.alorma.compose.settings.storage.base.SettingValueState

@Composable
fun <T> rememberSettingsState(
    value: T,
    defaultValue: T,
    onValueChanged: (T) -> Unit,
) = remember(value) {
    object : SettingValueState<T> {
        override var value: T
            get() = value
            set(value) {
                onValueChanged(value)
            }

        override fun reset() {
            onValueChanged(defaultValue)
        }
    }
}