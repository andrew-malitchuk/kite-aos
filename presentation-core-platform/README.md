# presentation-core-platform

The platform and hardware engine for the Home Kiosk application.

## Features
- **Intelligent Motion Guard**: Uses the front camera to detect movement, waking the screen and dimming it when idle.
- **Feedback Protection**: Specialized "blindness" logic prevents the service from being triggered by its own light changes.
- **Kiosk Security**: Integration with Android Device Administration for programmatic screen locking.
- **Background MQTT**: Persistent foreground service for maintaining MQTT connectivity.
- **Battery Tracking**: Real-time battery status reporting via MQTT.

## Requirements
- **Permissions**:
    - `CAMERA`: Required for motion detection.
    - `WRITE_SETTINGS`: Required for managing screen brightness.
    - `POST_NOTIFICATIONS`: Required for foreground service visibility (Android 13+).
- **Device Setup**: The application must be enabled as a **Device Administrator** in system settings for the remote lock feature to function.

## Internal Structure
- `core/extension/`: Useful Android platform extensions (e.g., `Context.openAppLanguageSettings`).
- `core/helper/`: System interaction helpers (e.g., `DevicePowerManager`).
- `source/analyzer/`: Mathematical and frame analysis logic (e.g., `MotionAnalyzer`).
- `source/receiver/`: Broadcast receivers for system events.
- `source/service/`: Foreground services for long-running background tasks.

## Service Management
The services in this module are typically started and stopped based on user configuration managed by the `domain` layer. They are designed to be resilient and handle lifecycle transitions (like screen on/off) gracefully.
