package presentation.feature.onboarding.source.onboarding

import android.content.pm.PackageManager
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import presentation.core.localisation.R
import presentation.core.styling.core.FormFactor
import presentation.core.styling.core.LocalFormFactor
import presentation.core.styling.core.Theme
import presentation.core.ui.core.ext.fillMaxSquare
import presentation.core.ui.source.kit.atom.container.SafeContainer
import presentation.core.ui.source.kit.atom.gradient.backgroundGradient
import presentation.core.ui.source.kit.atom.icon.IcAdmin24
import presentation.core.ui.source.kit.atom.icon.IcCamera24
import presentation.core.ui.source.kit.atom.icon.IcLogo48
import presentation.core.ui.source.kit.atom.icon.IcNotification24
import presentation.core.ui.source.kit.atom.icon.IcOverlay24
import presentation.core.ui.source.kit.atom.icon.IcSensor24
import presentation.core.ui.source.kit.atom.icon.IcSuccess24
import presentation.core.ui.source.kit.atom.icon.IcSystemSettings24
import presentation.core.ui.source.kit.atom.icon.IcWeb24
import presentation.core.ui.source.kit.atom.icon.IcWebProtected24
import presentation.core.ui.source.kit.atom.snackbar.StackedSnakbarHostState
import presentation.core.ui.source.kit.atom.snackbar.rememberStackedSnackbarHostState
import presentation.core.ui.source.kit.molecule.item.TextInputListItem
import presentation.core.ui.source.kit.molecule.item.ToggleListItem
import presentation.core.ui.source.kit.organism.animatedsequence.AnimatedItem
import presentation.core.ui.source.kit.organism.animatedsequence.AnimationSequenceHost
import presentation.feature.onboarding.core.composable.pager.WizardPageData
import presentation.feature.onboarding.core.composable.pager.WizardPager
import presentation.feature.onboarding.core.composable.pager.WizardPagerAction
import presentation.feature.onboarding.core.composable.shape.AnimatedCookieShape

/**
 * The main UI content for the Onboarding screen.
 *
 * This Composable orchestrates the multi-step wizard, including permission requests,
 * URL configuration fields, and a final confirmation slide. It manages immersive mode
 * and delegates navigation logic to the [WizardPager].
 *
 * @param state The current [OnboardingState] providing permission statuses and dashboard URLs.
 * @param onIntent Callback for dispatching user intents back to the ViewModel. Supported
 *   actions include all [OnboardingIntent] subtypes for permission requests and finish actions.
 * @param snackbarHostState State for the Design System snackbar host.
 * @see OnboardingScreen
 * @see OnboardingViewModel
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
internal fun OnboardingContent(
    state: OnboardingState,
    onIntent: (OnboardingIntent) -> Unit,
    snackbarHostState: StackedSnakbarHostState = rememberStackedSnackbarHostState(),
) {
    val activity = LocalActivity.current
    val window = (activity)?.window

    if (window != null) {
        val controller =
            remember(window, Unit) {
                WindowCompat.getInsetsController(window, window.decorView)
            }

        LaunchedEffect(Unit) {
            // Hide both status bar and navigation bar
            controller.hide(WindowInsetsCompat.Type.systemBars())
            // Set behavior to show bars on swipe
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val pagerState = rememberPagerState(pageCount = { 4 })

    // Force hide keyboard when moving to the last slide (index 3 = final confirmation page)
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage == 3) {
            keyboardController?.hide()
            focusManager.clearFocus(force = true)
        }
    }

    // Logic extraction
    val urlRegex = remember { Regex("^https?://.+") }
    var dashboardUrl by remember { mutableStateOf("http://") }
    var whitelistUrl by remember { mutableStateOf("http://") }

    LaunchedEffect(state.dashboardUrls) {
        state.dashboardUrls?.let {
            dashboardUrl = it.dashboardUrl
            whitelistUrl = it.whitelistUrl
        }
    }

    // Validate URL: must match http(s)://... pattern and be longer than the bare "http://" prefix (7 chars)
    val isUrlsValid =
        remember(dashboardUrl, whitelistUrl) {
            urlRegex.matches(dashboardUrl) && dashboardUrl.length > 7
        }

    // WebRTC's RECORD_AUDIO is only meaningful for the mobile camera stack; TV boxes frequently
    // have no microphone, so requiring it would strand the user on the permissions slide. Camera
    // stays required (Camera2 external motion needs it).
    val isTv = LocalFormFactor.current == FormFactor.TV

    // Some device profiles (TV boxes / stripped ROMs) ship no system Settings Activity for these
    // permissions. Probe availability up front so we can hide the unusable rows and drop them from
    // the "all granted" gate below — otherwise the user is stranded on an impossible requirement and
    // can never advance past the permissions slide.
    val context = LocalContext.current
    val isOverlaySettingAvailable =
        remember {
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
                .resolveActivity(context.packageManager) != null
        }
    // Device admin uses the system feature flag rather than resolveActivity: it is the semantically
    // correct "is device admin supported" signal (TV boxes lack it) and, unlike an implicit-intent
    // probe, it is immune to API 30+ package-visibility filtering hiding the Settings activity.
    val isDeviceAdminAvailable =
        remember {
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_DEVICE_ADMIN)
        }
    val isWriteSettingsAvailable =
        remember {
            Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:${context.packageName}"))
                .resolveActivity(context.packageManager) != null
        }

    val allPermissionsGranted =
        state.isCameraPermissionGranted &&
            (isTv || state.isAudioPermissionGranted) &&
            (!isOverlaySettingAvailable || state.isOverlayPermissionGranted) &&
            state.isPostNotificationPermissionGranted &&
            (!isDeviceAdminAvailable || state.isDeviceAdminGranted) &&
            (!isWriteSettingsAvailable || state.isWriteSettingsGranted)

    val onboardingPages =
        listOf(
            // Slide 1: Welcome - Initial branding and introduction to the app.
            WizardPageData(
                title = stringResource(R.string.onboarding_slide_1_title),
                description = stringResource(R.string.onboarding_slide_1_description),
                backgroundColor = Theme.color.brandVariant,
            ) {
                BoxWithConstraints(Modifier.fillMaxSize(), Alignment.Center) {
                    AnimatedCookieShape(
                        modifier = Modifier.fillMaxSquare(maxWidth, maxHeight),
                        color = Theme.color.warning,
                    )
                    Image(
                        modifier =
                        Modifier
                            .fillMaxSquare(maxWidth, maxHeight, 1f, 0.7f)
                            .align(Alignment.Center),
                        imageVector = IcLogo48,
                        contentDescription = null,
                    )
                }
            },
            // Slide 2: Permissions - Critical step to ensure the app can function as a kiosk.
            WizardPageData(
                title = stringResource(R.string.onboarding_slide_2_title),
                description = stringResource(R.string.onboarding_slide_2_description),
                backgroundColor = Theme.color.error,
                isNextEnabled = { allPermissionsGranted },
            ) {
                PermissionsList(
                    state = state,
                    onIntent = onIntent,
                    isOverlaySettingAvailable = isOverlaySettingAvailable,
                    isDeviceAdminAvailable = isDeviceAdminAvailable,
                    isWriteSettingsAvailable = isWriteSettingsAvailable,
                )
            },
            // Slide 3: Configuration - Collecting user input for Dashboard and Whitelist URLs.
            WizardPageData(
                title = stringResource(R.string.onboarding_slide_3_title),
                description = stringResource(R.string.onboarding_slide_3_description),
                backgroundColor = Theme.color.brandVariant,
                isNextEnabled = { isUrlsValid },
            ) {
                UrlConfigurationFields(
                    dashboardUrl = dashboardUrl,
                    whitelistUrl = whitelistUrl,
                    urlRegex = urlRegex,
                    onDashboardChange = { dashboardUrl = it },
                    onWhitelistChange = { whitelistUrl = it },
                    enabled = pagerState.currentPage == 2,
                )
            },
            // Slide 4: Final - Successful completion slide with visual confirmation.
            WizardPageData(
                title = stringResource(R.string.onboarding_slide_4_title),
                description = stringResource(R.string.onboarding_slide_4_description),
                backgroundColor = Theme.color.warning,
            ) {
                BoxWithConstraints(Modifier.fillMaxSize(), Alignment.Center) {
                    AnimatedCookieShape(
                        modifier = Modifier.fillMaxSquare(maxWidth, maxHeight),
                        color = Theme.color.brand,
                    )
                    Image(
                        modifier =
                        Modifier
                            .fillMaxSquare(maxWidth, maxHeight, 1f, 0.5f)
                            .align(Alignment.Center),
                        colorFilter = ColorFilter.tint(Theme.color.inkMain),
                        imageVector = IcSuccess24,
                        contentDescription = null,
                    )
                }
            },
        )

    SafeContainer(
        modifier =
        Modifier
            .fillMaxSize()
            .background(backgroundGradient()),
        snackbarHostState = snackbarHostState,
    ) {
        AnimationSequenceHost {
            AnimatedItem(
                index = 0,
                enter =
                fadeIn(
                    tween(
                        durationMillis = 250,
                    ),
                ),
            ) {
                WizardPager(
                    pages = onboardingPages,
                    pagerState = pagerState,
                    onAction = { action ->
                        // Always try to hide keyboard on any pager action to be safe on older devices
                        keyboardController?.hide()
                        focusManager.clearFocus(force = true)

                        when (action) {
                            WizardPagerAction.OnFinishClick -> {
                                onIntent(
                                    OnboardingIntent.OnFinishIntent(
                                        dashboardUrl,
                                        whitelistUrl,
                                    ),
                                )
                            }

                            WizardPagerAction.OnNextClick,
                            WizardPagerAction.OnBackClick,
                            -> {
                                // Focus already cleared above
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun PermissionsList(
    state: OnboardingState,
    onIntent: (OnboardingIntent) -> Unit,
    isOverlaySettingAvailable: Boolean,
    isDeviceAdminAvailable: Boolean,
    isWriteSettingsAvailable: Boolean,
) {
    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeL, Alignment.CenterVertically),
    ) {
        PermissionItem(
            stringResource(R.string.permission_camera_access),
            IcCamera24,
            state.isCameraPermissionGranted,
        ) {
            onIntent(OnboardingIntent.OnAskCameraPermissionIntent)
        }
        PermissionItem(
            stringResource(R.string.permission_audio_access),
            IcSensor24,
            state.isAudioPermissionGranted,
        ) {
            onIntent(OnboardingIntent.OnAskAudioPermissionIntent)
        }
        PermissionItem(
            stringResource(R.string.permission_post_notifications),
            IcNotification24,
            state.isPostNotificationPermissionGranted,
        ) {
            onIntent(OnboardingIntent.OnAskPostNotificationPermissionIntent)
        }
        // Only surface these when the device actually has the matching system Settings screen;
        // otherwise the row is un-grantable and would block the user (see the gate in OnboardingContent).
        if (isOverlaySettingAvailable) {
            PermissionItem(
                stringResource(R.string.permission_overlay_access),
                IcOverlay24,
                state.isOverlayPermissionGranted,
            ) {
                onIntent(OnboardingIntent.OnAskOverlayPermissionIntent)
            }
        }
        if (isDeviceAdminAvailable) {
            PermissionItem(
                stringResource(R.string.permission_device_admin),
                IcAdmin24,
                state.isDeviceAdminGranted,
            ) {
                onIntent(OnboardingIntent.OnAskDeviceAdminPermissionIntent)
            }
        }
        if (isWriteSettingsAvailable) {
            PermissionItem(
                stringResource(R.string.permission_system_settings),
                IcSystemSettings24,
                state.isWriteSettingsGranted,
            ) {
                onIntent(OnboardingIntent.OnAskWriteSettingsPermissionIntent)
            }
        }
    }
}

@Composable
private fun PermissionItem(text: String, icon: ImageVector, isChecked: Boolean, onClick: () -> Unit) {
    ToggleListItem(
        text = text,
        icon = icon,
        iconBackgroundColor = Theme.color.brand,
        iconForegroundColor = Theme.color.inkMain,
        isChecked = isChecked,
        onCheckedChange = { onClick() },
    )
}

@Composable
private fun UrlConfigurationFields(
    dashboardUrl: String,
    whitelistUrl: String,
    urlRegex: Regex,
    onDashboardChange: (String) -> Unit,
    onWhitelistChange: (String) -> Unit,
    enabled: Boolean = true,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeL, Alignment.CenterVertically),
    ) {
        TextInputListItem(
            modifier = Modifier.fillMaxWidth(),
            initialText = dashboardUrl,
            onTextChanged = onDashboardChange,
            placeholder = stringResource(R.string.url_configuration_dashboard_url_placeholder),
            icon = IcWeb24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            validationRegex = urlRegex,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Uri,
                autoCorrect = false,
            ),
        )
        TextInputListItem(
            modifier = Modifier.fillMaxWidth(),
            initialText = whitelistUrl,
            onTextChanged = onWhitelistChange,
            placeholder = stringResource(R.string.url_configuration_whitelist_placeholder),
            icon = IcWebProtected24,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain,
            validationRegex = urlRegex,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Uri,
                autoCorrect = false,
            ),
        )
    }
}
