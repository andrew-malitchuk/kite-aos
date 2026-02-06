package presentation.core.platform.core.extension

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings


/**
 * Opens the application-specific language settings in the Android system.
 * 
 * On Android 13 (API 33) and above, this opens the per-app language settings screen.
 * On older versions, this feature is not directly supported and the function returns false.
 *
 * @return true if the settings screen was successfully opened, false otherwise.
 */
public fun Context.openAppLanguageSettings(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        startActivity(Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        })
        true // System settings opened for per-app language
    } else {
        false // System doesn't support per-app language directly
    }
}