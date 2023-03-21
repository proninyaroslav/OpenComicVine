package org.proninyaroslav.opencomicvine.data

data class ErrorReportInfo(
    val error: Throwable?,
    val comment: String? = null,
)