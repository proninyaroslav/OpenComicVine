package org.proninyaroslav.opencomicvine.data.preferences

import com.squareup.moshi.JsonClass
import dev.zacsweers.moshix.sealed.annotations.TypeLabel

@JsonClass(generateAdapter = true, generator = "sealed:type")
sealed class PrefTheme(val id: Int) {
    @TypeLabel("unknown")
    object Unknown : PrefTheme(-1)

    @TypeLabel("system")
    object System : PrefTheme(0)

    @TypeLabel("dark")
    object Dark : PrefTheme(1)

    @TypeLabel("light")
    object Light : PrefTheme(2)

    companion object {
        fun fromId(id: Int): PrefTheme = when (id) {
            0 -> System
            1 -> Dark
            2 -> Light
            else -> Unknown
        }
    }
}