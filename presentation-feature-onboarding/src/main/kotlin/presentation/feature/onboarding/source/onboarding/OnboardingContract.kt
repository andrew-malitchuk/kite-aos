package presentation.feature.onboarding.source.onboarding

import domain.core.source.model.DashboardModel

/**
 * Represents the UI state of the onboarding flow.
 *
 * @property isCameraPermissionGranted Status of the Manifest.permission.CAMERA.
 * @property isOverlayPermissionGranted Status of the Settings.canDrawOverlays check.
 * @property isPostNotificationPermissionGranted Status of the notification permission.
 * @property isDeviceAdminGranted Status of the Device Administration privilege.
 * @property isWriteSettingsGranted Status of the Settings.System.canWrite check.
 * @property dashboardUrls Initial URLs loaded from persistent storage, if any.
 */
public data class OnboardingState(
    val isCameraPermissionGranted: Boolean,
    val isOverlayPermissionGranted: Boolean,
    val isPostNotificationPermissionGranted: Boolean,
    val isDeviceAdminGranted: Boolean,
    val isWriteSettingsGranted: Boolean,
    val dashboardUrls: DashboardModel?
)

/**
 * One-off side effects for the onboarding screen.
 */
public sealed class OnboardingSideEffect {
    /** Launch system camera permission dialog. */
    public data object AskCameraPermissionEffect : OnboardingSideEffect()
    /** Launch system overlay permission settings screen. */
    public data object AskOverlayPermissionEffect : OnboardingSideEffect()
    /** Launch system notification permission dialog. */
    public data object AskPostNotificationPermissionEffect : OnboardingSideEffect()
    /** Launch system Device Administrator activation screen. */
    public data object AskDeviceAdminEffect : OnboardingSideEffect()
    /** Launch system Write Settings toggle screen. */
    public data object AskWriteSettingsEffect : OnboardingSideEffect()
    /** Navigate to the main dashboard after successful setup. */
    public data object GoToMainEffect : OnboardingSideEffect()
    /** Show an error message via snackbar. */
    public data class ShowError(val messageId: Int) : OnboardingSideEffect()
}