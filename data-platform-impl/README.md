# data-platform-impl

Native Android implementation of platform services for the "kite-aos" project.

## Features
- **Reactive Connectivity**: Uses `callbackFlow` to provide a robust stream of network status updates.
- **Smart App Filtering**: Enumerates applications with valid launch intents across both the standard (`CATEGORY_LAUNCHER`) and Android TV (`CATEGORY_LEANBACK_LAUNCHER`) launcher categories, de-duplicates them by package name, and sorts them alphabetically. This ensures TV-only apps (registered solely under leanback) appear on TV devices.
- **Seamless DI**: Uses Koin for easy integration of platform-specific singletons.

## Implementation Details
The `ConnectivityObserverImpl` automatically checks the initial network state upon subscription and monitors for subsequent changes via system callbacks.
