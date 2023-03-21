package org.proninyaroslav.opencomicvine.ui.navigation

class NavDeepLinkUrl {
    companion object {
        val characters = listOf("https://comicvine.gamespot.com/characters/")

        val issues = listOf("https://comicvine.gamespot.com/issues/")

        val volumes = listOf("https://comicvine.gamespot.com/volumes/")

        val recentIssues = listOf("https://comicvine.gamespot.com/new-comics/")

        fun character(idArgName: String) = listOf(
            "https://comicvine.gamespot.com/{_nameIdStub}/4005-{$idArgName}/",
            "https://comicvine.gamespot.com/{_nameIdStub}/29-{$idArgName}/",
        )

        fun issue(idArgName: String) =
            listOf("https://comicvine.gamespot.com/{_nameIdStub}/4000-{$idArgName}/")

        fun volume(idArgName: String) =
            listOf("https://comicvine.gamespot.com/{_nameIdStub}/4050-{$idArgName}/")
    }
}