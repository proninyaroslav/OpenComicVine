package org.proninyaroslav.opencomicvine.ui.details

sealed interface DetailsPage {
    data class Character(val characterId: Int) : DetailsPage
    data class Issue(val issueId: Int) : DetailsPage
    data class Volume(val volumeId: Int) : DetailsPage
    data class Publisher(val publisherId: Int) : DetailsPage
    data class Creator(val creatorId: Int) : DetailsPage
    data class Movie(val movieId: Int) : DetailsPage
    data class StoryArc(val storyArcId: Int) : DetailsPage
    data class Team(val teamId: Int) : DetailsPage
    data class Person(val personId: Int) : DetailsPage
    data class Location(val locationId: Int) : DetailsPage
    data class Concept(val conceptId: Int) : DetailsPage
    data class Object(val objectId: Int) : DetailsPage
    data class ImageViewer(val url: String) : DetailsPage
}