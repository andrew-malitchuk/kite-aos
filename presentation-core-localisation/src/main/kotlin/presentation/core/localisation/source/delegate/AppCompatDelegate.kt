package presentation.core.localisation.source.delegate

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/**
 * Changes the application's locale dynamically at runtime.
 *
 * This function utilizes the AndroidX [AppCompatDelegate] to set the application's
 * locales. This is the recommended approach for modern Android versions as it
 * automatically handles per-app language settings and persists the choice.
 *
 * @param languageCode The ISO 639-1 language code (e.g., "uk", "en").
 */
public fun changeAppLanguage(languageCode: String) {
    // Create a locale list from the language tag
    val appLocales = LocaleListCompat.forLanguageTags(languageCode)

    // Apply the selected language to the app. This will trigger a configuration change
    // and recreate activities to reflect the new locale.
    AppCompatDelegate.setApplicationLocales(appLocales)
}
