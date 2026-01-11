package dev.yahk.convention.core.ext

import java.util.Locale

/**
 * Capitalizes the first character of a string, converting it to title case.
 * If the first character is already uppercase, the string is returned unchanged.
 *
 * @return A new string with the first character capitalized.
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
 * Checks if the string ends with the specified file extension.
 *
 * @param extension The file extension to check, without the leading dot (e.g., "apk" not ".apk").
 * @return `true` if the string ends with the specified extension, `false` otherwise.
 * @throws IllegalArgumentException if the provided extension contains a dot.
 */
public fun String.containsExtension(extension: String): Boolean {
    require(!extension.contains(".")) {
        "Please, use file extension without \".\" (e.g., \"apk\" not \".apk\")"
    }
    return this.endsWith(extension)
}
