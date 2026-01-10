package dev.yahk.convention.core.ext

import com.android.build.api.dsl.Lint

/**
 * Adds a lint rule ID to the list of disabled lint checks.
 *
 * This extension provides a convenient way to remove specific lint checks
 * from the Android lint analysis.
 *
 * Usage:
 *
 * ```kotlin
 * lint {
 *     removeFromCheck("UnusedResources")
 * }
 * ```
 *
 * @param ruleId The ID of the lint rule to disable.
 */
public fun Lint.removeFromCheck(ruleId: String) {
    disable.add(ruleId)
}
