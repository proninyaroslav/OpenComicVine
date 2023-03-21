package org.proninyaroslav.opencomicvine.model.state

class FilterStateCache<S, F>(initialState: State<S, F>) {
    private val appliedStatesStack = ArrayDeque(listOf(initialState))

    val current
        get() = appliedStatesStack.first()

    fun save(state: State<S, F>) {
        appliedStatesStack.removeFirstOrNull()
        appliedStatesStack.addLast(state)
    }

    data class State<S, F>(
        val sort: S? = null,
        val filter: F? = null,
    )
}