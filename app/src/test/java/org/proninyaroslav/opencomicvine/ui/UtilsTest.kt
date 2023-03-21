package org.proninyaroslav.opencomicvine.ui

import org.junit.Assert.*
import org.junit.Test

class UtilsTest {
    @Test
    fun isMultiWordString() {
        assertTrue("a b".isMultiWord())
        assertTrue("a  b".isMultiWord())
        assertTrue("a   b".isMultiWord())
        assertTrue("a\nb".isMultiWord())
        assertFalse("ab".isMultiWord())
    }
}
