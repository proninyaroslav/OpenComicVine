package org.proninyaroslav.opencomicvine.model.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object ComicVineDateConverter {
    private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
    private const val DATE_FORMAT_SHORT = "yyyy-MM-dd"
    private const val DATE_FORMAT_SHORT_WITH_MONTH = "MMM d, yyyy"

    private val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.US)
    private val dateFormatShort = SimpleDateFormat(DATE_FORMAT_SHORT, Locale.US)
    private val dateFormatShortWithMonth = SimpleDateFormat(
        DATE_FORMAT_SHORT_WITH_MONTH,
        Locale.US,
    )

    @FromJson
    fun fromJson(json: String): Date? = try {
        dateFormat.parse(json)
    } catch (e: ParseException) {
        parseShort(json)
    }

    private fun parseShort(json: String) = try {
        dateFormatShort.parse(json)
    } catch (e: ParseException) {
        dateFormatShortWithMonth.parse(json)
    }

    fun fromJsonShort(json: String): Date? = dateFormatShort.parse(json)

    @ToJson
    fun toJson(value: Date?): String? = value?.let { dateFormat.format(value) }

    fun toJsonShort(value: Date?): String? = value?.let { dateFormatShort.format(value) }
}
