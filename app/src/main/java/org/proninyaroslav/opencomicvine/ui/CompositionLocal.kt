/*
 * Copyright (C) 2023 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of OpenComicVine.
 *
 * OpenComicVine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenComicVine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenComicVine.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.proninyaroslav.opencomicvine.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope

val LocalActivity = staticCompositionLocalOf<AppCompatActivity> {
    noLocalProvidedFor("LocalActivity")
}

val LocalAppSnackbarState = staticCompositionLocalOf<SnackbarHostState> {
    noLocalProvidedFor("LocalAppSnackbarState")
}

val LocalAppCoroutineScope = staticCompositionLocalOf<CoroutineScope> {
    noLocalProvidedFor("LocalAppCoroutineScope")
}

private fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}

@Composable
fun AppCompositionLocalProvider(
    activity: AppCompatActivity,
    snackbarState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalActivity provides activity,
        LocalAppSnackbarState provides snackbarState,
        LocalAppCoroutineScope provides coroutineScope,
        content = content,
    )
}
