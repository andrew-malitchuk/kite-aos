package presentation.core.platform.source.config

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.content.pm.PackageManager
import org.koin.core.annotation.Single

/**
 * Runtime form-factor configuration shared across the presentation layer.
 *
 * Feature libraries cannot read the application module's `BuildConfig`, so this
 * abstraction centralises "is this an Android TV device?" detection and is
 * injected wherever shared code must branch between the `mobile` and `tv`
 * form-factors at runtime (e.g. hiding the FAB, enabling D-pad key handling,
 * choosing a foreground-service type).
 *
 * @see AppConfigImpl
 * @since 1.2.0
 */
public interface AppConfig {

    /**
     * `true` when the app is running on an Android TV / leanback device.
     *
     * @since 1.2.0
     */
    public val isTv: Boolean

    public companion object {

        /**
         * Compile-time form-factor seed set by the application module from its
         * own `BuildConfig.IS_TV` before the DI graph is consumed.
         *
         * Runtime detection ([AppConfigImpl.isTv]) is authoritative; this seed is
         * an additional signal so that a `tv`-flavored build behaves as TV even on
         * a device that fails to report the leanback feature (e.g. a sideloaded
         * APK). `null` until the application seeds it.
         *
         * @since 1.2.0
         */
        @Volatile
        public var buildFlagIsTv: Boolean? = null
    }
}

/**
 * Default [AppConfig] backed by Android runtime capabilities.
 *
 * A device is classified as TV when the [UiModeManager] reports
 * [Configuration.UI_MODE_TYPE_TELEVISION], when the platform declares the
 * [PackageManager.FEATURE_LEANBACK] system feature, or when the application
 * seeded [AppConfig.buildFlagIsTv] as `true`. Mobile and TV form-factors never
 * coexist on one device, so the permissive OR is safe.
 *
 * @param context Application [Context] used to query system services.
 * @since 1.2.0
 */
@Single(binds = [AppConfig::class])
public class AppConfigImpl(
    private val context: Context,
) : AppConfig {

    override val isTv: Boolean by lazy { detectIsTv() }

    private fun detectIsTv(): Boolean {
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager
        val isTelevision = uiModeManager?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
        val hasLeanback = context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
        return isTelevision || hasLeanback || (AppConfig.buildFlagIsTv == true)
    }
}
