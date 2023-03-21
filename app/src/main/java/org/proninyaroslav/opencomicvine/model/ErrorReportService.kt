package org.proninyaroslav.opencomicvine.model

import org.acra.ACRA
import org.acra.ReportField
import org.proninyaroslav.opencomicvine.data.ErrorReportInfo
import javax.inject.Inject

interface ErrorReportService {
    fun report(info: ErrorReportInfo)
}

class ErrorReportServiceImpl @Inject constructor() : ErrorReportService {
    private val reporter = ACRA.errorReporter

    override fun report(info: ErrorReportInfo) {
        info.comment?.let { reporter.putCustomData(ReportField.USER_COMMENT.toString(), it) }
        reporter.handleSilentException(info.error)
    }
}