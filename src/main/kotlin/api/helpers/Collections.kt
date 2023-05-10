package api.helpers

fun <E> Collection<E?>.anyNull() = any { it == null }
