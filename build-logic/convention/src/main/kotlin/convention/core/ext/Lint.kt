package convention.core.ext

import com.android.build.api.dsl.Lint

/**
 * Disable a specific lint check by its rule identifier.
 *
 * Appends [ruleId] to the set of disabled checks so that subsequent lint
 * analysis will skip the corresponding rule entirely.
 *
 * @param ruleId The lint rule identifier to disable (e.g., `"UnusedResources"`).
 * @see com.android.build.api.dsl.Lint
 */
public fun Lint.removeFromCheck(ruleId: String) {
    disable.add(ruleId)
}
