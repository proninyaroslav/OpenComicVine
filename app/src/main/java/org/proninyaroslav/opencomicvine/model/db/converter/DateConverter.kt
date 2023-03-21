package org.proninyaroslav.opencomicvine.model.db.converter

import androidx.room.TypeConverter
import java.util.*

class DateConverter {
    @TypeConverter
    fun fromDate(date: Date): Long = date.time

    @TypeConverter
    fun toDate(timeInMillis: Long): Date = Date(timeInMillis)
}