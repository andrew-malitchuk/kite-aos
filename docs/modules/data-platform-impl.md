# data-platform-impl

Native Android implementation of platform services for the "kite-aos" project.

## Features
- **Reactive Connectivity**: Uses `callbackFlow` to provide a robust stream of network status updates.
- **Smart App Filtering**: Automatically filters for applications with valid launch intents and sorts them alphabetically.
- **Seamless DI**: Uses Koin for easy integration of platform-specific singletons.

## Implementation Details
The `ConnectivityObserverImpl` automatically checks the initial network state upon subscription and monitors for subsequent changes via system callbacks.
