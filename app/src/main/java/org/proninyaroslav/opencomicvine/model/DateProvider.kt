package org.proninyaroslav.opencomicvine.model

import java.util.*
import javax.inject.Inject

interface DateProvider {
    val now: Date
}

class DateProviderImpl @Inject constructor() : DateProvider {
    override val now: Date
        get() = Calendar.getInstance().time
}