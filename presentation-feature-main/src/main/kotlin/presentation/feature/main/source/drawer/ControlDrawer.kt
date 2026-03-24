package presentation.feature.main.source.drawer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import domain.core.source.model.ApplicationModel
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.button.icon.IconButton
import presentation.core.ui.source.kit.atom.button.icon.core.IconButtonDefault
import presentation.core.ui.source.kit.atom.icon.IcBack24
import presentation.core.ui.source.kit.atom.icon.IcForward24
import presentation.core.ui.source.kit.atom.icon.IcRefresh24
import presentation.core.ui.source.kit.atom.icon.IcSettings24
import presentation.core.ui.source.kit.molecule.item.SimpleApplicationListItem

/**
 * A drawer component providing navigation and control actions.
 *
 * It dynamically switches between a [Row] (horizontal) and a [Column] (vertical)
 * layout based on the [isBottom] parameter, reflecting the current dock position.
 *
 * @param modifier The modifier for the drawer layout.
 * @param isBottom Whether the drawer is positioned at the bottom of the screen.
 * @param applications The list of allowed applications to display.
 * @param canGoBack Whether the WebView can navigate backward.
 * @param canGoForward Whether the WebView can navigate forward.
 * @param onAction Callback for handling drawer actions.
 */
@Composable
public fun ControlDrawer(
    modifier: Modifier = Modifier,
    isBottom: Boolean,
    applications: List<ApplicationModel>,
    canGoBack: Boolean,
    canGoForward: Boolean,
    onAction: (ControlAction) -> Unit,
) {
    val scrollState = rememberScrollState()

    val content = @Composable {
        IconButton(
            modifier = Modifier,
            icon = IcSettings24,
            sizes = IconButtonDefault.buttonSizeSet().buttonSize64(),
            onClick = {
                onAction(ControlAction.OnSettingAction)
            },
        )
        IconButton(
            modifier = Modifier,
            icon = IcRefresh24,
            sizes = IconButtonDefault.buttonSizeSet().buttonSize64(),
            onClick = {
                onAction(ControlAction.OnReloadAction)
            },
        )
        // Back button
        IconButton(
            modifier = Modifier,
            icon = IcBack24, // Placeholder icon
            sizes = IconButtonDefault.buttonSizeSet().buttonSize64(),
            enabled = canGoBack,
            onClick = {
                onAction(ControlAction.OnBackAction)
            },
        )
        // Forward button
        IconButton(
            modifier = Modifier,
            icon = IcForward24, // Placeholder icon
            sizes = IconButtonDefault.buttonSizeSet().buttonSize64(),
            enabled = canGoForward,
            onClick = {
                onAction(ControlAction.OnForwardAction)
            },
        )

        when (isBottom) {
            true ->
                Box(
                    modifier =
                    Modifier
                        .size(
                            width = Theme.size.sizeXS,
                            height = Theme.size.size3XL,
                        )
                        .clip(CircleShape)
                        .background(
                            Theme.color.brandVariant,
                        ),
                )

            false ->
                Box(
                    modifier =
                    Modifier
                        .size(
                            width = Theme.size.size3XL,
                            height = Theme.size.sizeXS,
                        )
                        .clip(CircleShape)
                        .background(
                            Theme.color.brandVariant,
                        ),
                )
        }
        applications.forEach { app ->
            SimpleApplicationListItem(
                applicationModel = app,
                onClick = {
                    onAction(ControlAction.OnApplicationAction(app.packageName))
                },
            )
        }
    }

    AnimatedContent(
        targetState = isBottom,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith
                fadeOut(animationSpec = tween(300))
        },
        label = "LayoutSwitch",
    ) { targetIsBottom ->
        if (targetIsBottom) {
            Row(
                modifier =
                modifier
                    .fillMaxSize()
                    .background(Theme.color.canvas)
                    .horizontalScroll(scrollState)
                    .padding(Theme.spacing.sizeL),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.sizeM),
            ) {
                content()
            }
        } else {
            Column(
                modifier =
                modifier
                    .fillMaxSize()
                    .background(Theme.color.canvas)
                    .verticalScroll(scrollState)
                    .padding(Theme.spacing.sizeL),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeM),
            ) {
                content()
            }
        }
    }
}

/**
 * Represents the possible actions within the [ControlDrawer].
 */
public sealed interface ControlAction {
    /** Reload the current kiosk dashboard. */
    public data object OnReloadAction : ControlAction

    /** Navigate to the settings screen. */
    public data object OnSettingAction : ControlAction

    /** Navigate backward in the WebView history. */
    public data object OnBackAction : ControlAction

    /** Navigate forward in the WebView history. */
    public data object OnForwardAction : ControlAction

    /** Launch an external application. */
    public data class OnApplicationAction(val packageName: String) : ControlAction
}
