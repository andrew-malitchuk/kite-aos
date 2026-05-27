package convention.core.ext

import java.util.Locale

/**
 * Return a copy of this string with the first character converted to title case.
 *
 * Uses [Locale.US] for case conversion to guarantee deterministic behaviour
 * across build environments regardless of the host locale.
 *
 * @return A new string whose first character is upper-cased, or the original
 *         string if the first character is already upper-case.
 */
public fun String.capitalize(): String =
    replaceFirstChar {
        if (it.isLowerCase()) {
            it.titlecase(Locale.US)
        } else {
            it.toString()
        }
    }

/**
 * Check whether this string ends with the given file extension.
 *
 * The [extension] must be supplied without a leading dot; a dot is prepended
 * automatically before comparison.
 *
 * @param extension The file extension without a leading dot (e.g., `"apk"` not `".apk"`).
 * @return `true` if this string ends with `".<extension>"`.
 * @throws IllegalArgumentException if [extension] contains a dot character.
 */
public fun String.containsExtension(extension: String): Boolean {
    require(!extension.contains(".")) {
        "Please, use file extension without \".\" (e.g., \"apk\" not \".apk\")"
    }
    return this.endsWith(".$extension")
}
