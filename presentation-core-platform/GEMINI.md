# Module: presentation-core-platform

## Overview
This module provides low-level platform integration and system services required by the presentation layer. It handles device-specific capabilities that fall outside standard UI logic, such as motion detection, power management, and administrative policies.

## Responsibilities
*   **Motion Detection**: Implements `MotionService`, which uses CameraX to analyze frames from the front camera to detect user presence or movement.
*   **Power & Display Management**: Automatically manages screen brightness, wakes the device on motion, and enforces inactivity-based locking.
*   **Device Administration**: Provides `ApplicationDeviceAdminReceiver` to handle administrative privileges required for kiosk-mode features like programmatically locking the screen.
*   **MQTT Lifecycle**: Manages the MQTT connection as a foreground service, ensuring telemetry can be reported even when the UI is not active.
*   **Telemetry**: Monitors system events like battery level changes and reports them to the telemetry system.
*   **System Navigation**: Contains extensions to simplify interaction with Android system settings (e.g., language settings).

## Architecture
This module sits at the base of the presentation layer, interacting directly with Android framework components:
*   **Foreground Services**: `MotionService` and `MqttService` run as foreground services to maintain active background processing regardless of the application's visibility.
*   **Broadcast Receivers**: Handles system events related to device administration (`ApplicationDeviceAdminReceiver`) and battery status (`BatteryReceiver`).
*   **CameraX Integration**: Uses `ImageAnalysis` for real-time frame processing with minimal power consumption.

## Key Components
*   **`MotionService`**: The core service orchestrating camera-based motion detection. It delegates frame analysis to `MotionAnalyzer` and power management to `DevicePowerManager`.
*   **`MotionAnalyzer`**: Encapsulates the mathematical logic for detecting motion from camera frames using luma analysis and scoring.
*   **`DevicePowerManager`**: A helper class that abstracts interactions with system services for managing brightness, wake locks, and device security policies.
*   **`MqttService`**: A reactive service that observes the MQTT configuration and manages the connection state automatically.
*   **`ApplicationDeviceAdminReceiver`**: Entry point for device administrator policies, primarily used for the `force-lock` capability.
*   **`BatteryReceiver`**: A broadcast receiver that calculates battery percentage and triggers MQTT telemetry updates.

## Dependencies
*   **CameraX**: For frame analysis.
*   **Lifecycle Service**: For lifecycle-aware foreground processing.
*   **Domain Use Cases**: Orchestrates business logic for motion emission and MQTT operations.
