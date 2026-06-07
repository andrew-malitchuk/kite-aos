# Installation

## Quick Install

The fastest way to get Kite running — no build tools required.

1. Download `kite-*.apk` from the [latest GitHub Release](https://github.com/andrew-malitchuk/kite-aos/releases/latest).
2. On your Android device, enable **Install from unknown sources** (Settings → Security).
3. Install the APK and follow the [onboarding wizard](configuration.md).

!!! tip "Which flavor?"
    Two APK flavors are available:

    | Flavor | WebView Engine | Min API |
    |--------|---------------|---------|
    | `gms`  | Android WebView | API 25 (Android 7.1) |
    | `foss` | GeckoView (Firefox) | API 26 (Android 8.0) |

    Use **foss** if you need better WebRTC / camera support in your dashboard.

---

## Build from Source

### Prerequisites

| Tool | Version |
|------|---------|
| Android Studio | Ladybug or newer |
| JDK | 21 |
| Android device | API 26+ (API 25 for GMS flavor) |

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/andrew-malitchuk/kite-aos.git
cd kite-aos

# 2. Build a debug APK (GMS flavor)
./gradlew assembleGmsDebug

# 3. Build a debug APK (FOSS flavor)
./gradlew assembleFossDebug

# 4. Build and install directly to a connected device
./gradlew :presentation-core-application:installGmsDebug
```

### Release build

```bash
# Requires a keystore configured in local.properties
./gradlew assembleGmsRelease
./gradlew assembleFossRelease
```

---

## Verifying the Build

```bash
# Run static analysis (must pass with 0 issues)
./gradlew detekt

# Run code style check
./gradlew ktlintCheck

# Run both quality gates
./gradlew check
```
