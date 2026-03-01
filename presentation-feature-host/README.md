# presentation-feature-host

The entry point and host module for the "kite-aos" application.

## Features
- **Single Activity Architecture**: Hosts the entire application within a single `HostActivity`.
- **Advanced Splash Screen**: Custom splash screen logic with smooth transitions and state-based dismissal.
- **Kiosk Mode Enforcement**: Immersive full-screen mode and global back-gesture blocking.
- **Boot Persistence**: Automatically launches on device boot via `BootReceiver`.

## Usage
This module is the application's entry point. The `HostActivity` is declared as the launcher activity in the `AndroidManifest.xml`. No manual instantiation of components from this module is required in other feature modules.
