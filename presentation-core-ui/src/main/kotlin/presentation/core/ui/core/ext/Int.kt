package presentation.core.ui.core.ext

/**
 * Checks whether the given [bit] flag is set in this integer using a bitwise AND operation.
 *
 * @param bit the bitmask to test against this integer.
 * @return `true` if the bitwise AND of this integer and [bit] is non-zero, `false` otherwise.
 *
 * @since 0.0.1
 */
public infix fun Int.has(bit: Int): Boolean = this.and(bit) != 0
