package org.proninyaroslav.opencomicvine.ui.components.list

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed interface CardCellSize {
    sealed class Adaptive(val minSize: Dp) : CardCellSize {
        object Small : Adaptive(96.dp)
        object Medium : Adaptive(128.dp)
        object Large : Adaptive(160.dp)
    }

    data class Fixed(val count: Int) : CardCellSize
}