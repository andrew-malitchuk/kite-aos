package presentation.core.navigation.api.core.composition

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import presentation.core.navigation.api.source.destination.AppNavigator

/**
 * [CompositionLocal][androidx.compose.runtime.CompositionLocal] for accessing the [AppNavigator]
 * within the Compose tree.
 *
 * Consumers should read this local via `LocalAppNavigator.current` to obtain the active
 * navigator instance. A default error factory is provided so that any access outside a
 * [CompositionLocalProvider][androidx.compose.runtime.CompositionLocalProvider] immediately
 * surfaces the misconfiguration.
 *
 * @see AppNavigator
 * @see LocalBackAction
 * @see presentation.core.navigation.impl.source.host.NavigationHost
 * @since 0.0.1
 */
public val LocalAppNavigator: ProvidableCompositionLocal<AppNavigator?> =
    // Default factory throws to fail fast when no navigator is provided higher in the tree.
    compositionLocalOf {
        error("No App Navigator Provided")
    }

/**
 * [CompositionLocal][androidx.compose.runtime.CompositionLocal] for a generic back-navigation
 * action, typically bound to [AppNavigator.popBackStack].
 *
 * This allows leaf composables to trigger back navigation without depending on [AppNavigator]
 * directly, enabling simpler composable previews and reuse.
 *
 * @see AppNavigator.popBackStack
 * @see LocalAppNavigator
 * @see presentation.core.navigation.impl.source.host.NavigationHost
 * @since 0.0.1
 */
public val LocalBackAction: ProvidableCompositionLocal<() -> Unit> =
    // Default factory throws to fail fast when no back action is provided higher in the tree.
    compositionLocalOf {
        error("No Back Action Provided")
    }
