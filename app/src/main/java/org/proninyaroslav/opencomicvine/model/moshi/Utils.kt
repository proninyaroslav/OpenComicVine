package org.proninyaroslav.opencomicvine.model.moshi

fun stripEnclosingQuotes(json: String) = json.run {
    if (startsWith("\"") && endsWith("\"")) {
        /* Strip enclosing quotes for json String types */
        substring(1, length - 1)
    } else {
        this
    }
}