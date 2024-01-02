/*
 * Copyright (C) 2023 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of OpenComicVine.
 *
 * OpenComicVine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenComicVine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenComicVine.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.proninyaroslav.opencomicvine.model

import java.util.Calendar
import java.util.Date


fun getDaysOfCurrentWeek(): Pair<Date, Date> {
    val calendar = Calendar.getInstance().apply {
        firstDayOfWeek = Calendar.MONDAY
        this[Calendar.DAY_OF_WEEK] = Calendar.MONDAY
        this[Calendar.AM_PM] = Calendar.AM
        this[Calendar.HOUR] = 0
        this[Calendar.MINUTE] = 0
        this[Calendar.SECOND] = 0
        this[Calendar.MILLISECOND] = 0
    }

    val start = calendar.time
    calendar.add(Calendar.DAY_OF_MONTH, 6)
    val end = calendar.time

    return start to end
}

fun getNextWeekFromCurrentDay(): Pair<Date, Date> {
    val calendar = Calendar.getInstance().apply {
        this[Calendar.AM_PM] = Calendar.AM
        this[Calendar.HOUR] = 0
        this[Calendar.MINUTE] = 0
        this[Calendar.SECOND] = 0
        this[Calendar.MILLISECOND] = 0
    }

    calendar.add(Calendar.DAY_OF_MONTH, 1)
    val start = calendar.time
    calendar.add(Calendar.DAY_OF_MONTH, 6)
    val end = calendar.time

    return start to end
}

fun Int.isEven() = this % 2 == 0

fun Int.isOdd() = this % 2 != 0

fun <T> List<T>.subListFrom(offset: Int, maxLength: Int): List<T> {
    val toIndex = offset + maxLength
    return subList(offset, if (toIndex > size) size else toIndex)
}

private val numberExtractRegex = "\\D".toRegex()
private fun isTPB(n: String?) = n == "TPB"
private fun isSupplement(n: String?) = n?.contains("Suppl.", ignoreCase = true) ?: false

fun issuesCount(firstIssueNumber: String?, lastIssueNumber: String?): Int? {
    if (isTPB(firstIssueNumber) || isTPB(lastIssueNumber)) {
        return 1
    }

    val firstIssue = if (isSupplement(firstIssueNumber)) {
        firstIssueNumber?.replace(numberExtractRegex, "")?.toInt()?.plus(1)
    } else {
        firstIssueNumber?.toInt()
    }

    val lastIssue = if (isSupplement(lastIssueNumber)) {
        lastIssueNumber?.replace(numberExtractRegex, "")?.toInt()?.plus(1)
    } else {
        lastIssueNumber?.toInt()
    }

    return if (firstIssue != null && lastIssue != null) {
        lastIssue - firstIssue + 1
    } else if (firstIssue == null && lastIssue == null) {
        null
    } else {
        // At least there are one non-null issue
        1
    }
}