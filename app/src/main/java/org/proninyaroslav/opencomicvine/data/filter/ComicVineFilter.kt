package org.proninyaroslav.opencomicvine.data.filter

open class ComicVineFilter(
    val field: String,
    val value: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ComicVineFilter

        if (field != other.field) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = field.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

    override fun toString(): String {
        return "ComicVineFilter(field='$field', value='$value')"
    }
}
