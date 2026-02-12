package presentation.core.ui.core.ext

public infix fun Int.has(bit: Int): Boolean = this.and(bit) != 0