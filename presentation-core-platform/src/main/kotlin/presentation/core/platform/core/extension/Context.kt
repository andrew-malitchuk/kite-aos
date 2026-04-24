package presentation.core.platform.core.extension

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

/**
 * Opens the application-specific language settings in the Android system.
 *
 * On Android 13 (API 33, Tiramisu) and above, this opens the per-app language settings screen
 * via [Settings.ACTION_APP_LOCALE_SETTINGS]. On older versions, per-app language selection is not
 * directly supported by the system, so the function returns `false`.
 *
 * @return `true` if the settings screen was successfully opened, `false` if the platform does not
 * support per-app language settings.
 * @see Settings.ACTION_APP_LOCALE_SETTINGS
 * @since 0.0.1
 */
public fun Context.openAppLanguageSettings(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        startActivity(
            Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
                // Build a package-scheme URI so the system opens settings for this app specifically.
                data = Uri.fromParts("package", packageName, null)
            },
        )
        true // System settings opened for per-app language
    } else {
        false // System doesn't support per-app language directly
    }
}
