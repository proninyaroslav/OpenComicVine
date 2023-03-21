package org.proninyaroslav.opencomicvine.model.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.proninyaroslav.opencomicvine.data.Aliases

object AliasesConverter {
    @FromJson
    fun fromJson(json: String?): Aliases? = json?.split('\n')?.let { Aliases(it) }

    @ToJson
    fun toJson(list: Aliases?): String? = list?.joinToString("\n")
}