package org.proninyaroslav.opencomicvine.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class Aliases(private val list: List<String>) : List<String> by list