# data-platform-api

Android platform interaction contracts for the "kite-aos" project.

## Features
- **Network Observability**: Interface for reactive monitoring of network connectivity changes.
- **App Discovery**: Contracts for retrieving launcher-enabled applications installed on the device.

## Usage
Inject `ConnectivityObserver` to react to network changes or `ApplicationPlatformSource` to list available applications for the kiosk dashboard.
