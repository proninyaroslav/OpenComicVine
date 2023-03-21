package org.proninyaroslav.opencomicvine.data.preferences

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import dev.zacsweers.moshix.sealed.annotations.NestedSealed
import dev.zacsweers.moshix.sealed.annotations.TypeLabel
import org.proninyaroslav.opencomicvine.data.ComicVineSearchResourceType
import java.util.*

@JsonClass(generateAdapter = true, generator = "sealed:type")
sealed interface PrefSearchFilter {
    @NestedSealed
    sealed interface Resources : PrefSearchFilter {
        @TypeLabel("resources_unknown")
        object Unknown : Resources

        @TypeLabel("resources_all")
        object All : Resources

        @TypeLabel("resources_selected")
        @JsonClass(generateAdapter = true)
        data class Selected(val resourceTypes: Set<PrefSearchResourceType>) : Resources {
            init {
                check(resourceTypes.isNotEmpty()) { "Resource types set must be not empty" }
            }
        }
    }
}

@JsonClass(generateAdapter = true)
data class PrefSearchFilterBundle(
    val resources: PrefSearchFilter.Resources,
)

@JsonClass(generateAdapter = false)
enum class PrefSearchResourceType {
    @Json(name = "character")
    Character,

    @Json(name = "concept")
    Concept,

    @Json(name = "object")
    Object,

    @Json(name = "location")
    Location,

    @Json(name = "issue")
    Issue,

    @Json(name = "story_arc")
    StoryArc,

    @Json(name = "volume")
    Volume,

    @Json(name = "person")
    Person,

    @Json(name = "team")
    Team,

    @Json(name = "video")
    Video,

    @Json(name = "series")
    Series,

    @Json(name = "episode")
    Episode,
}

fun PrefSearchFilter.Resources.toComicVineResourceType(): Set<ComicVineSearchResourceType> =
    when (this) {
        PrefSearchFilter.Resources.Unknown -> emptySet()
        PrefSearchFilter.Resources.All -> ComicVineSearchResourceType.values().toSet()
        is PrefSearchFilter.Resources.Selected -> resourceTypes.map {
            when (it) {
                PrefSearchResourceType.Character -> ComicVineSearchResourceType.Character
                PrefSearchResourceType.Concept -> ComicVineSearchResourceType.Concept
                PrefSearchResourceType.Object -> ComicVineSearchResourceType.Object
                PrefSearchResourceType.Location -> ComicVineSearchResourceType.Location
                PrefSearchResourceType.Issue -> ComicVineSearchResourceType.Issue
                PrefSearchResourceType.StoryArc -> ComicVineSearchResourceType.StoryArc
                PrefSearchResourceType.Volume -> ComicVineSearchResourceType.Volume
                PrefSearchResourceType.Person -> ComicVineSearchResourceType.Person
                PrefSearchResourceType.Team -> ComicVineSearchResourceType.Team
                PrefSearchResourceType.Video -> ComicVineSearchResourceType.Video
                PrefSearchResourceType.Series -> ComicVineSearchResourceType.Series
                PrefSearchResourceType.Episode -> ComicVineSearchResourceType.Episode
            }
        }.toSet()
    }