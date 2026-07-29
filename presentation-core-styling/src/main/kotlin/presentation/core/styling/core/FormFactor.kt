package presentation.core.styling.core

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * The physical form-factor the UI is rendering for.
 *
 * Distinct from [WindowSizeClass]: form-factor is about the *input model and
 * viewing distance* (a TV is remote-driven and viewed from across the room),
 * whereas a size class is about available space. Both feed [Theme.is10Foot].
 *
 * @since 1.2.0
 */
public enum class FormFactor {
    /** A phone or tablet driven by touch. */
    MOBILE,

    /** An Android TV / leanback device driven by a D-pad remote. */
    TV,
}

/**
 * CompositionLocal carrying the active [FormFactor].
 *
 * Provided at the host level (above `AppTheme`) from `AppConfig.isTv`. Defaults
 * to [FormFactor.MOBILE] so Composable previews and un-wrapped test trees don't
 * crash.
 *
 * @since 1.2.0
 */
public val LocalFormFactor: ProvidableCompositionLocal<FormFactor> =
    staticCompositionLocalOf { FormFactor.MOBILE }

/**
 * CompositionLocal carrying the current [WindowSizeClass].
 *
 * Provided at the host level from `calculateWindowSizeClass(activity)`. Nullable
 * with a `null` default because a [WindowSizeClass] cannot be derived without an
 * Activity; consumers treat `null` as "unknown / compact". [Theme.is10Foot]
 * handles the null case.
 *
 * @since 1.2.0
 */
public val LocalWindowSizeClass: ProvidableCompositionLocal<WindowSizeClass?> =
    staticCompositionLocalOf { null }
