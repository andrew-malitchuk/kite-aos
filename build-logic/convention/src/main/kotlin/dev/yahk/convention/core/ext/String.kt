package dev.yahk.convention.core.ext

import java.util.Locale

/**
 * Capitalizes the first character of a string, converting it to title case.
 * If the first character is already uppercase, the string is returned unchanged.
 *
 * This is a replacement for the deprecated [kotlin.text.capitalize] function,
 * using [Locale.US] for consistent behavior across platforms.
 *
 * @return A new string with the first character capitalized.
 * @since 0.0.1
 */
public fun String.capitalize(): String =
    replaceFirstChar {
        if (it.isLowerCase()) {
            // Convert to title case using US locale for consistent cross-platform behavior
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
 * @since 0.0.1
 */
public fun String.containsExtension(extension: String): Boolean {
    // Guard against incorrect usage where a dot is included in the extension
    require(!extension.contains(".")) {
        "Please, use file extension without \".\" (e.g., \"apk\" not \".apk\")"
    }
    return this.endsWith(extension)
}
