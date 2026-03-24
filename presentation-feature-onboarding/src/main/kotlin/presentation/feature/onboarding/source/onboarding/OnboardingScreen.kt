package presentation.feature.onboarding.source.onboarding

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import presentation.core.localisation.R
import presentation.core.navigation.api.core.composition.LocalAppNavigator
import presentation.core.navigation.api.source.destination.Destination
import presentation.core.platform.source.receiver.ApplicationDeviceAdminReceiver
import presentation.core.ui.source.kit.atom.snackbar.rememberStackedSnackbarHostState

/**
 * The main entry point for the onboarding feature.
 *
 * This screen manages several [rememberLauncherForActivityResult] to interact with the
 * Android system for permissions and administrative tasks. It synchronizes the local UI state
 * with the actual system settings on every initialization via [LaunchedEffect].
 */
@Composable
public fun OnboardingScreen(viewModel: OnboardingViewModel = koinViewModel()) {
    val context = LocalContext.current
    val appNavigator = LocalAppNavigator.current
    val state by viewModel.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    val snackbarHostState = rememberStackedSnackbarHostState()

    // 1. Camera Launcher
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            viewModel.onCameraPermission(it)
        }

    // 2. Overlay Launcher
    val overlayLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.onOverlayPermission(Settings.canDrawOverlays(context))
        }

    // 2.1 Notification Launcher
    val notificationLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            viewModel.onPostNotificationPermission(it)
        }

    // 3. Device Admin Launcher
    val adminLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val adminName = ComponentName(context, ApplicationDeviceAdminReceiver::class.java)
            viewModel.onDeviceAdminPermission(dpm.isAdminActive(adminName))
        }

    // 4. Write Settings Launcher
    val writeLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.onWriteSettingsPermission(Settings.System.canWrite(context))
        }

    LaunchedEffect(Unit) {
        val cameraGranted =
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_GRANTED

        val notificationGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }

        val overlayGranted = Settings.canDrawOverlays(context)

        val writeSettingsGranted = Settings.System.canWrite(context)

        // For Device Admin, check via DevicePolicyManager
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val adminName = ComponentName(context, ApplicationDeviceAdminReceiver::class.java)
        val adminGranted = dpm.isAdminActive(adminName)

        // Update ViewModel with actual values
        with(viewModel) {
            onCameraPermission(cameraGranted)
            onPostNotificationPermission(notificationGranted)
            onOverlayPermission(overlayGranted)
            onDeviceAdminPermission(adminGranted)
            onWriteSettingsPermission(writeSettingsGranted)
        }
    }

    // Side Effect handling
    viewModel.collectSideEffect { effect ->
        when (effect) {
            OnboardingSideEffect.AskCameraPermissionEffect -> cameraLauncher.launch(Manifest.permission.CAMERA)
            OnboardingSideEffect.AskPostNotificationPermissionEffect -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    viewModel.onPostNotificationPermission(true)
                }
            }
            OnboardingSideEffect.AskOverlayPermissionEffect -> {
                val intent =
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}"),
                    )
                overlayLauncher.launch(intent)
            }

            OnboardingSideEffect.AskDeviceAdminEffect -> {
                val intent =
                    Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                        putExtra(
                            DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                            ComponentName(context, ApplicationDeviceAdminReceiver::class.java),
                        )
                        putExtra(
                            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                            context.getString(R.string.permission_device_admin_explanation),
                        )
                    }
                adminLauncher.launch(intent)
            }

            OnboardingSideEffect.AskWriteSettingsEffect -> {
                val intent =
                    Intent(
                        Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:${context.packageName}"),
                    )
                writeLauncher.launch(intent)
            }

            OnboardingSideEffect.GoToMainEffect -> {
                keyboardController?.hide()
                focusManager.clearFocus()
                // Small delay to let the system hide the keyboard before navigation
                // This is crucial for older Android versions (API 26-28)
                scope.launch {
                    kotlinx.coroutines.delay(100)
                    appNavigator?.navigate(Destination.Main)
                }
            }
            is OnboardingSideEffect.ShowError -> {
                snackbarHostState.showSnackbar(
                    title = context.getString(effect.messageId),
                )
            }
        }
    }

    OnboardingContent(
        state = state,
        onIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
    )
}
