package presentation.feature.main.source.main

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import domain.core.source.model.DockPositionModel
import presentation.core.platform.source.service.MotionService
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.container.SafeContainer
import presentation.core.ui.source.kit.atom.icon.IcLogo48
import presentation.core.ui.source.kit.atom.icon.IcOpen24
import presentation.core.ui.source.kit.atom.shape.SquircleShape
import presentation.core.ui.source.kit.atom.shimmer.ShimmerImage
import presentation.core.ui.source.kit.atom.snackbar.StackedSnakbarHostState
import presentation.core.ui.source.kit.atom.snackbar.rememberStackedSnackbarHostState
import presentation.feature.main.core.components.SideBar
import presentation.feature.main.source.drawer.ControlAction
import presentation.feature.main.source.drawer.ControlDrawer
import presentation.feature.main.source.webview.KioskWebView
import presentation.feature.main.source.webview.rememberKioskWebViewState
import kotlin.math.roundToInt

/**
 * The internal layout implementation for the Main screen.
 *
 * It orchestrates the [KioskWebView], the motion-activated FAB, and the [SideBar] drawer.
 * It also handles immersive mode management and motion detection service lifecycle.
 *
 * @param state The current [MainState].
 * @param onIntent Callback to dispatch [MainIntent] to the ViewModel. Supported actions include:
 *   [MainIntent.OnLoadIntent] for initial data loading,
 *   [MainIntent.OnSettingsClickAction] for navigating to settings,
 *   and [MainIntent.OnOpenApplicationIntent] for launching an external application.
 * @param snackbarHostState State for the Design System's snackbar.
 * @see MainScreen
 * @see MainViewModel
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
internal fun MainContent(
    state: MainState,
    onIntent: (MainIntent) -> Unit = {},
    snackbarHostState: StackedSnakbarHostState = rememberStackedSnackbarHostState(),
) {
    val context = LocalContext.current

    var isOpened by remember { mutableStateOf(false) }

    val isBottom = state.dockPosition?.position == DockPositionModel.Position.Up

    val webViewState = rememberKioskWebViewState()

    LaunchedEffect(Unit) {
        onIntent(MainIntent.OnLoadIntent)
    }

    LaunchedEffect(state.isMoveDetectorEnabled) {
        if (state.isMoveDetectorEnabled) {
            startSelectedService(context)
        } else {
            stopSelectedService(context)
        }
    }

    LaunchedEffect(state.dashboardUrls) {
        state.dashboardUrls?.let {
            webViewState.url = it.dashboardUrl
            webViewState.whitelist = listOf(it.whitelistUrl)
        }
    }

    val activity = LocalActivity.current
    val window = (activity)?.window

    if (window != null) {
        // Manage system UI visibility for an immersive kiosk experience.
        val controller =
            remember(window) {
                WindowCompat.getInsetsController(window, window.decorView)
            }

        LaunchedEffect(Unit) {
            // Hide both status bar and navigation bar to maximize screen real estate.
            controller.hide(WindowInsetsCompat.Type.systemBars())
            // Configure the system to show bars temporarily when the user swipes from edges.
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    SafeContainer(
        modifier =
        Modifier
            .fillMaxSize(),
        snackbarHostState = snackbarHostState,
    ) {
        SideBar(
            modifier =
            Modifier
                .fillMaxSize(),
            isBottom = isBottom,
            isDrawerOpen = isOpened,
            onDismiss = {
                isOpened = false
            },
            drawer = {
                val sideModifier =
                    Modifier.then(
                        when (isBottom) {
                            true -> Modifier.fillMaxWidth()
                            false -> Modifier.fillMaxSize()
                        },
                    )

                Box(
                    modifier =
                    Modifier
                        .then(sideModifier),
                ) {
                    ControlDrawer(
                        isBottom = isBottom,
                        applications = state.chosenApps,
                        canGoBack = webViewState.canGoBack,
                        canGoForward = webViewState.canGoForward,
                    ) { action ->
                        when (action) {
                            ControlAction.OnReloadAction -> {
                                isOpened = false
                                webViewState.reload()
                            }

                            ControlAction.OnSettingAction -> onIntent(MainIntent.OnSettingsClickAction)
                            ControlAction.OnBackAction -> {
                                isOpened = false
                                webViewState.goBack()
                            }

                            ControlAction.OnForwardAction -> {
                                isOpened = false
                                webViewState.goForward()
                            }

                            is ControlAction.OnApplicationAction -> {
                                isOpened = false
                                onIntent(MainIntent.OnOpenApplicationIntent(action.packageName))
                            }
                        }
                    }
                }
            },
            content = {
                Box(
                    modifier =
                    Modifier
                        .fillMaxSize(),
                ) {
                    Box(
                        modifier =
                        Modifier
                            .fillMaxSize()
                            .alpha(if (webViewState.isLoading) 0f else 1f),
                    ) {
                        KioskWebView(
                            modifier =
                            Modifier
                                .fillMaxSize(),
                            state = webViewState,
                        )

                        // Track the drag offset of the FAB to allow user repositioning.
                        var fabOffset by rememberSaveable(stateSaver = IntOffsetSaver) {
                            mutableStateOf(
                                IntOffset.Zero,
                            )
                        }

                        AnimatedVisibility(
                            visible = state.isFabVisible,
                            enter = fadeIn(),
                            exit = fadeOut(),
                            modifier =
                            Modifier
                                .align(
                                    when (isBottom) {
                                        true -> Alignment.BottomEnd
                                        false -> Alignment.CenterStart
                                    },
                                )
                                .offset { fabOffset }
                                .padding(Theme.spacing.sizeL),
                        ) {
                            FloatingActionButton(
                                shape = SquircleShape(Theme.size.sizeXL),
                                onClick = {
                                    isOpened = true
                                },
                                containerColor = Theme.color.canvas,
                                modifier =
                                Modifier
                                    .pointerInput(Unit) {
                                        // Handle drag gestures to move the FAB across the screen.
                                        detectDragGestures { change, dragAmount ->
                                            change.consume()
                                            fabOffset +=
                                                IntOffset(
                                                    dragAmount.x.roundToInt(),
                                                    dragAmount.y.roundToInt(),
                                                )
                                        }
                                    },
                            ) {
                                Icon(
                                    imageVector = IcOpen24,
                                    contentDescription = "main_fab",
                                    tint = Theme.color.inkMain,
                                )
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = webViewState.isLoading,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier.align(Alignment.Center),
                    ) {
                        ShimmerImage(
                            modifier = Modifier.size(256.dp),
                            imageVector = IcLogo48,
                        )
                    }
                }
            },
        )
    }
}

/**
 * Starts the [MotionService] as a foreground service for camera-based motion detection.
 *
 * @param context The Android [Context] used to start the service.
 * @since 0.0.1
 */
private fun startSelectedService(context: Context) {
    val intent = Intent(context, MotionService::class.java)
    try {
        ContextCompat.startForegroundService(context, intent)
    } catch (e: Exception) {
        Toast.makeText(context, "SWW", Toast.LENGTH_LONG).show()
    }
}

/**
 * Stops the [MotionService] foreground service.
 *
 * @param context The Android [Context] used to stop the service.
 * @since 0.0.1
 */
private fun stopSelectedService(context: Context) {
    val intent = Intent(context, MotionService::class.java)
    context.stopService(intent)
}

/**
 * Custom [Saver] for [IntOffset] to persist the FAB drag position across configuration changes.
 */
private val IntOffsetSaver =
    Saver<IntOffset, Pair<Int, Int>>(
        save = { it.x to it.y },
        restore = { IntOffset(it.first, it.second) },
    )
