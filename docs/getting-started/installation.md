# Installation

## Quick Install

The fastest way to get Kite running — no build tools required.

1. Download `kite-*.apk` from the [latest GitHub Release](https://github.com/andrew-malitchuk/kite-aos/releases/latest).
2. On your Android device, enable **Install from unknown sources** (Settings → Security).
3. Install the APK and follow the [onboarding wizard](configuration.md).

!!! tip "Which flavor?"
    Flavors are a 2-dimensional matrix — `distribution` (WebView engine) × `formfactor` (device) — giving four combinations: `gmsMobile`, `fossMobile`, `gmsTv`, `fossTv`.

    | Distribution | WebView Engine | Min API |
    |--------------|---------------|---------|
    | `gms`  | Android WebView + Firebase | API 25 (Android 7.1) |
    | `foss` | GeckoView (Firefox), no GMS | API 26 (Android 8.0) |

    Use **foss** if you need better WebRTC / camera support in your dashboard, or if you require an F-Droid-friendly, GMS-free build. Use **gms** for the lighter system WebView.

    Pick the form-factor that matches your hardware: **mobile** for phones/tablets, **tv** for Android TV boxes. A device is either mobile or TV — the two builds never coexist. See [Android TV support](../android-tv.md) for details.

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

# 2. Build a debug APK (GMS mobile flavor)
./gradlew assembleGmsMobileDebug

# 3. Build a debug APK (FOSS mobile flavor)
./gradlew assembleFossMobileDebug

# 4. Build and install directly to a connected device
./gradlew :presentation-core-application:installGmsMobileDebug
```

### Release build

```bash
# Requires a keystore configured in local.properties
./gradlew assembleGmsMobileRelease
./gradlew assembleFossMobileRelease
```

---

## Android TV

Kite runs on Android TV boxes as a large-screen Home Assistant dashboard. TV is a `tv`
form-factor of the same codebase — not a separate app. See [Android TV support](../android-tv.md)
for the full design and feature notes.

!!! warning "Supported TV hardware"
    Motion detection on TV uses a **USB webcam** (Camera2 external, with a UVC fallback) or an
    **MQTT/PIR presence topic** when no camera is attached. Target devices therefore need a
    **USB-A host port** — e.g. NVIDIA Shield TV Pro, Xiaomi Mi Box S, ONN 4K Pro.
    **Chromecast with Google TV** and **Fire TV Stick** are out of scope (no USB host).

### Build & install

TV variants combine the same `distribution` dimension with the `tv` form-factor:

```bash
# Build a debug TV APK (FOSS / GeckoView engine)
./gradlew :presentation-core-application:assembleFossTvDebug

# Build a debug TV APK (GMS / system WebView engine)
./gradlew :presentation-core-application:assembleGmsTvDebug

# Build and install directly to a connected TV box (or emulator)
./gradlew :presentation-core-application:installFossTvDebug

# Release build (requires a keystore in local.properties)
./gradlew :presentation-core-application:assembleGmsTvRelease
```

To sideload a prebuilt APK, connect over ADB and install:

```bash
adb install kite-fossTv-debug.apk
```

Once installed, the app appears on the **Android TV home-screen launcher row** (it ships a
leanback launcher entry and a TV banner). Launch it there and follow the D-pad-adapted
[onboarding wizard](configuration.md).

!!! tip "Which engine on TV?"
    Both engines are built for TV. GeckoView (`fossTv`) is heavier — on **low-end TV boxes** the
    `gmsTv` variant (system WebView) may perform noticeably better. There is no code difference;
    just pick the variant that runs best on your hardware.

!!! note "Shared app identity"
    Mobile and TV share one `applicationId` (`dev.kite.aos`) but get distinct `versionCode`s
    (TV offset by `1_000_000`) so the Play Store can serve the correct APK to each device via
    multi-APK delivery.

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
