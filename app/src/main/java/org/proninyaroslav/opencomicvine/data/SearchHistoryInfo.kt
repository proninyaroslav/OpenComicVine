package org.proninyaroslav.opencomicvine.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.proninyaroslav.opencomicvine.model.db.converter.DateConverter
import java.util.*

@Entity
@TypeConverters(
    DateConverter::class,
)
data class SearchHistoryInfo(
    @PrimaryKey
    val query: String,
    val date: Date,
)