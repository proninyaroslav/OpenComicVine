package org.proninyaroslav.opencomicvine.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class UtilsTest {
    @Test
    fun isEven() {
        assertFalse(1.isEven())
        assertTrue(2.isEven())
    }

    @Test
    fun isOdd() {
        assertTrue(1.isOdd())
        assertFalse(2.isOdd())
    }

    @Test
    fun subListFrom() {
        val list = (1..10).toList()
        assertEquals(
            (2..4).toList(),
            list.subListFrom(offset = 1, maxLength = 3)
        )
        assertEquals(
            listOf(10),
            list.subListFrom(offset = 9, maxLength = 3)
        )
    }

    @Test
    fun `Issues count`() {
        assertEquals(3, issuesCount("1", "3"))

        assertEquals(1, issuesCount("1", null))

        assertEquals(1, issuesCount(null, "3"))

        assertNull(issuesCount(null, null))

        assertEquals(1, issuesCount("TPB", null))

        assertEquals(1, issuesCount(null, "TPB"))

        assertThrows(NumberFormatException::class.java) {
            issuesCount("1", "test")
        }

        assertEquals(6, issuesCount("1", "5 Suppl."))

        assertEquals(3, issuesCount("1 ", "3 \n"))

        assertEquals(5, issuesCount("I", "V"))
    }

    @Test
    fun isRomanNumeral() {
        val validRoman = "MCMXCIV"
        val invalidRoman = "MCMZ"

        assertTrue(validRoman.isRomanNumeral())

        assertFalse(invalidRoman.isRomanNumeral())

        assertFalse("".isRomanNumeral())

        assertFalse("ABC".isRomanNumeral())

        assertFalse("123".isRomanNumeral())
    }

    @Test
    fun romanToArabic() {
        assertEquals(1994, "MCMXCIV".romanToArabic())
        
        assertThrows(IllegalArgumentException::class.java) { "".romanToArabic() }

        assertThrows(IllegalArgumentException::class.java) { "ABC".romanToArabic() }

        assertThrows(IllegalArgumentException::class.java) { "123".romanToArabic() }
    }
}