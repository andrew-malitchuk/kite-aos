package presentation.core.localisation.source.delegate

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/**
 * Changes the application's locale dynamically at runtime.
 *
 * This function utilises the AndroidX [AppCompatDelegate] API to set the application's
 * locales. It is the recommended approach for modern Android versions (API 33+) because it
 * automatically handles per-app language settings and persists the user's choice across
 * process restarts.
 *
 * Calling this function triggers a configuration change that recreates all active activities
 * so the new locale takes effect immediately.
 *
 * @param languageCode The ISO 639-1 language code to apply (e.g., `"uk"`, `"en"`).
 * @return Unit
 * @see AppCompatDelegate.setApplicationLocales
 * @see LocaleListCompat.forLanguageTags
 * @since 0.0.1
 */
public fun changeAppLanguage(languageCode: String) {
    // Build a LocaleListCompat from the provided BCP-47 / ISO 639-1 language tag.
    val appLocales = LocaleListCompat.forLanguageTags(languageCode)

    // Apply the selected language globally. This triggers Activity recreation so that all
    // resource qualifiers are re-evaluated against the new locale.
    AppCompatDelegate.setApplicationLocales(appLocales)
}
