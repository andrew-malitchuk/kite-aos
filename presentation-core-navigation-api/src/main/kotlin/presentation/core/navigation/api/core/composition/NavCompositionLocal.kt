package presentation.core.navigation.api.core.composition

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import presentation.core.navigation.api.source.destination.AppNavigator

/**
 * CompositionLocal for accessing the [AppNavigator] within the Compose tree.
 */
public val LocalAppNavigator: ProvidableCompositionLocal<AppNavigator?> =
    compositionLocalOf {
        error("No App Navigator Provided")
    }

/**
 * CompositionLocal for a generic back action, typically bound to [AppNavigator.popBackStack].
 */
public val LocalBackAction: ProvidableCompositionLocal<() -> Unit> =
    compositionLocalOf {
        error("No Back Action Provided")
    }
