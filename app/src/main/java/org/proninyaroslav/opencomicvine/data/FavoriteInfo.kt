package org.proninyaroslav.opencomicvine.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.proninyaroslav.opencomicvine.model.db.converter.DateConverter
import java.util.*

@Entity(indices = [Index(value = ["entityId", "entityType"], unique = true)])
@TypeConverters(
    DateConverter::class,
)
data class FavoriteInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val entityId: Int,

    val entityType: EntityType,

    val dateAdded: Date,
) {
    enum class EntityType {
        Character,
        Issue,
        Concept,
        Location,
        Movie,
        Object,
        Person,
        StoryArc,
        Team,
        Volume,
    }
}