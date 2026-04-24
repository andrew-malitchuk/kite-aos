package presentation.feature.onboarding.source.onboarding

/**
 * User actions (intents) that can be performed during onboarding.
 *
 * Intents are dispatched from [OnboardingContent] and processed by
 * [OnboardingViewModel.handleIntent].
 *
 * @see OnboardingViewModel
 * @see OnboardingContent
 * @since 0.0.1
 */
public sealed class OnboardingIntent {
    /** User requested camera permission. */
    public data object OnAskCameraPermissionIntent : OnboardingIntent()

    /** User requested overlay permission. */
    public data object OnAskOverlayPermissionIntent : OnboardingIntent()

    /** User requested notification permission. */
    public data object OnAskPostNotificationPermissionIntent : OnboardingIntent()

    /** User requested device admin privilege. */
    public data object OnAskDeviceAdminPermissionIntent : OnboardingIntent()

    /** User requested write settings permission. */
    public data object OnAskWriteSettingsPermissionIntent : OnboardingIntent()

    /** Direct navigation to main (e.g. if setup is already complete). */
    public data object GoToMainIntent : OnboardingIntent()

    /** User finished the wizard, providing the final [dashboardUrl] and [whitelistUrl]. */
    public data class OnFinishIntent(
        val dashboardUrl: String,
        val whitelistUrl: String,
    ) : OnboardingIntent()
}
