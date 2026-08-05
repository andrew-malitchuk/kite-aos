# Android TV Support

> Status: **Implemented** (shipped in **v1.2.0**).
> This document began as the Android TV scoping-survey design and is now the **as-built
> reference** for the `tv` form-factor: §1–§5 describe the shipped architecture, §6 tracks
> phasing, and §7 records which design-time risks were resolved. It supersedes the shorter
> "Android TV Support" entry in [roadmap.md](roadmap.md) (now under _Done_).

## 0. Implementation map (as shipped)

Where each decision landed in the codebase:

| Area | Decision | As built |
|---|---|---|
| Build | `formfactor` dimension (`mobile`/`tv`) | `AndroidApplicationConventionPlugin` + `AndroidFeatureConventionPlugin`; 4 flavors `gmsMobile`/`fossMobile`/`gmsTv`/`fossTv`, `BuildConfig.IS_TV`, `versionCode + TV_VERSION_CODE_OFFSET (1_000_000)` |
| Runtime detection | Leanback / UI-mode guard | `AppConfig` / `AppConfigImpl` in `presentation-core-platform` (`UI_MODE_TYPE_TELEVISION` OR `FEATURE_LEANBACK` OR seeded `BuildConfig.IS_TV`); seeded by `YahkApplication` |
| 10-foot UI | Hybrid adaptive layouts | `FormFactor` + `LocalFormFactor` + `LocalWindowSizeClass`, `Theme.is10Foot`, `AppTheme` up-scales tokens by `TEN_FOOT_SCALE = 1.3f`; D-pad focus rings via `Modifier.tvFocusRing` |
| D-pad access | Long-press combo → drawer, no PIN | `HostActivity.dispatchKeyEvent` intercepts a hidden Konami D-pad sequence → `RemoteCommandBus.emitOpenDrawer()`; TV settings use a single-focus-target master/detail list (`onPreviewKeyEvent`) |
| Motion | USB camera → UVC → MQTT fallback | `MotionSourceFactory`: `Camera2ExternalMotionSource` → `UvcMotionSource` (libausbc) → `MqttMotionSource` → `NoOpMotionSource`; `HeadlessGlSurfaceTexture` for the UVC surface requirement |
| Manifest | Leanback launcher + banner | `presentation-feature-host/src/tv/AndroidManifest.xml` (leanback + `LEANBACK_LAUNCHER`); `presentation-core-application/src/tv/AndroidManifest.xml` (`android:banner`, `MotionService` `camera\|specialUse`); main `<queries>` adds `LEANBACK_LAUNCHER` |
| Deps | UVC fallback | `androidusbcamera-libausbc` + `libuvc` `3.2.7` (JitPack) as plain `implementation` on **every** variant (a USB-OTG webcam works on phones too, not just TV), with demo-transitive excludes; `material3-window-size-class` for WindowSizeClass |

## 1. Goal

Run kite-aos on Android TV boxes as a large-screen Home Assistant dashboard from a
**single codebase**, delivered as a `tv` form-factor rather than a fork. The target is
**full feature parity with the tablet build** — including USB-webcam motion detection and
MJPEG streaming — not a reduced subset.

**Market rationale (from roadmap):** no reliable HA-native kiosk dashboard exists for
Android TV; the closest competitor is rated 2.6/5 and functionally broken. Android TV holds
~35 % of the smart-TV OS market.

## 2. Target hardware

| Tier | Devices | Purpose |
|---|---|---|
| Primary test | **Android TV emulator** (API 30+) | UI / D-pad / focus iteration |
| Primary test | **NVIDIA Shield TV Pro**, **Xiaomi Mi Box S** | Real USB-host, good performance |
| Supported | Generic Android TV boxes with **USB-A host port** (ONN 4K Pro, Rockchip boxes) | Field deployment |
| Out of scope | Chromecast with Google TV, Fire TV Stick | No USB-A / no USB host |

> **Note:** the primary test devices (emulator + Shield/Mi Box) are strong hardware and do
> **not** surface the performance ceiling of cheap boxes. Before release, smoke-test on at
> least one low-end generic box — see risks §7.

## 3. Decisions (survey outcomes)

| # | Question | Decision |
|---|---|---|
| 1 | WebView engine on TV | **Both engines available** — GeckoView (foss) and system WebView (gms) |
| 2 | MVP scope | **Full parity with tablet**, including USB webcam |
| 3 | Motion detection priority | **USB camera first** (Camera2), then UVC fallback + MQTT |
| 4 | Test hardware | Android TV emulator + NVIDIA Shield / Xiaomi Mi Box |
| 5 | Kiosk lockdown | **Regular app in v1** — no lock-task / no home-app replacement |
| 6 | Settings access | **Hidden D-pad combo** opens the control drawer; **no PIN** (home use). _As built:_ a Konami-style directional sequence (Up, Up, Down, Down, Left, Right, Left, Right) — long-press was dropped because many remotes lack a MENU key |
| 7 | USB camera strategy | **Camera2 + UVC (AndroidUSBCamera) fallback immediately** |
| 8 | App identity | **Same `applicationId`** (`dev.kite.aos`) |
| 9 | D-pad inside HA WebView | **Rely on HA's own focus** — forward DPAD → key events only |
| 10 | Screensaver / ambient | **Already shipped on mobile** — mirror the existing ambient/clock for TV |
| 11 | Acoustic (mic) detection | **Out of scope** |
| 12 | 10-foot UI | **Hybrid adaptive layouts** (WindowSizeClass; TV = expanded / 10-foot) |
| 13 | MJPEG streaming | **Yes**, bundled with the USB camera |
| 14 | Onboarding on TV | **Adapt the existing wizard for D-pad** |

> **Roadmap correction:** the roadmap lists Screensaver under _In Progress / Next_, but it is
> already shipped on mobile (including the ambient clock variant). TV reuses it; it is **not**
> a blocker for TV work. Roadmap should be updated to move Screensaver to _Done_.

## 4. Build & module architecture

### 4.1 Second flavor dimension

The project already has one flavor dimension `distribution` (`foss` = GeckoView, `gms` =
system WebView) in
`build-logic/.../AndroidApplicationConventionPlugin.kt`. TV is added as a **second dimension**
`formfactor` (`mobile`, `tv`). Both engines stay available on TV, so **no `variantFilter`
cuts any combination** — the full matrix is intentional:

```
distribution × formfactor × buildType
  foss ┐                    ┌ debug
  gms  ┘ × mobile / tv ×    └ release

⇒ 4 flavors:  gmsMobile, fossMobile, gmsTv, fossTv
⇒ 8 variants (× debug/release)
```

```kotlin
// AndroidApplicationConventionPlugin (sketch)
flavorDimensions += listOf("distribution", "formfactor")
productFlavors {
    // existing distribution dimension
    create("foss") { dimension = "distribution"; minSdk = 26 }
    create("gms")  { dimension = "distribution" }
    // new form-factor dimension
    create("mobile") { dimension = "formfactor" }
    create("tv")     { dimension = "formfactor" }
}
```

### 4.2 App identity

Single `applicationId` = `dev.kite.aos` for every flavor. This is compatible with **Play
Store multi-APK delivery**: mobile and TV APKs share the app listing and are disambiguated by
the `leanback` feature declaration + distinct `versionCode`s. A device is either a phone/tablet
or a TV, so the two form-factors never need to coexist on one device.

- MQTT `device_class` published as **`tv`** instead of `tablet` (per roadmap).
- `versionCode` scheme for multi-APK to be defined at release time (see §7 open items).

### 4.3 Source-set layout

Unchanged (shared) across form-factors: **all `domain-*`, all `data-*`**. Divergence lives in:

- `presentation-core-platform` — motion source (USB camera), key handling, kiosk mechanics
- `presentation-feature-*` — D-pad focus, 10-foot layouts, onboarding navigation

TV-specific code and manifest bits go into `src/tv/` source-sets of the relevant modules;
shared code stays in `src/main/`.

### 4.4 Manifest (TV flavor overlay — `src/tv/AndroidManifest.xml`)

The current `src/main/AndroidManifest.xml` is touch/camera-first and must **not** be edited to
break mobile. TV additions belong in the flavor overlay:

- `<uses-feature android:name="android.software.leanback" android:required="false" />`
- `<uses-feature android:name="android.hardware.touchscreen" android:required="false" />`
- `<uses-feature android:name="android.hardware.camera" android:required="false" />`
- `<uses-feature android:name="android.hardware.camera.external" android:required="false" />`
- Leanback launcher `intent-filter` (`android.intent.category.LEANBACK_LAUNCHER`) on the host
  activity so the app appears on the Android TV home screen.
- **`MotionService` `foregroundServiceType`** must not hard-fail when no camera is present.
  The `type="camera"` declaration is currently hard-coded in `main`; on TV without a camera,
  declaring a camera FGS while never using the camera can crash on Android 14+. Resolve by
  flavor-specific service declaration or a runtime fallback FGS type. **(Risk — see §7.)**

## 5. Feature adaptation plan

### Category 1 — Direct port (no logic change)
HA WebView kiosk, `MqttService` (telemetry / HA Discovery / control), battery telemetry,
screen wake/dim/lock, backup/restore, periodic refresh, screensaver + ambient clock.

### Category 2 — Adapted

**D-pad / remote navigation**
- `HostActivity` intercepts `KEYCODE_DPAD_*` / `KEYCODE_DPAD_CENTER` and forwards them into the
  WebView as key events. **We rely on the HA dashboard's own focus handling** — no custom JS
  focus manager in v1. (Fallback: inject a JS focus manager later only if real HA themes focus
  poorly — tracked, not built.)
- FAB hidden on TV; swipe gestures suppressed.

**Settings access without touch**
- A **hidden D-pad combo** opens the control drawer (the same side-effect the FAB fires on
  mobile). **No PIN gate** — this is for home use. _As built:_ `HostActivity.dispatchKeyEvent`
  matches a Konami-style directional sequence (Up, Up, Down, Down, Left, Right, Left, Right) and
  fires `RemoteCommandBus.emitOpenDrawer()`; only the completing key is consumed, and the combo
  resets after a 3s pause. `dispatchKeyEvent` is used (not a Compose key modifier) because the
  modifier is bypassed while the `AndroidView`-hosted WebView owns focus.

**USB-webcam motion detection** (replaces the built-in front camera)
- **Primary:** Camera2 API + `FEATURE_CAMERA_EXTERNAL` (API 28+, no extra dependency). Same
  luma-analysis pipeline as the existing `MotionAnalyzer` / `ImageAnalysis` interface.
- **Fallback (shipped in v1):** [`AndroidUSBCamera`](https://github.com/jiangdongguo/AndroidUSBCamera)
  (UVC, Apache 2.0) for devices exposing a USB camera via USB Host but not Camera2.
  ⚠️ Last release Feb 2023, 488 open issues — fallback only, guarded behind capability checks.
- USB permission requested at runtime via `UsbManager` on first attach.
- If no camera path is available → MQTT motion fallback (Category 3).

**MJPEG camera streaming**
- Bundled with the USB camera: reuse the existing `MjpegHttpServer` in `MotionService`, sourced
  from the USB webcam frames (`/stream.mjpg`, `/snapshot.jpg`).

**Onboarding**
- Same wizard and logic; navigation adapted for D-pad (focusable steps, remote-driven
  permission prompts).

**10-foot UI**
- **Hybrid adaptive layouts.** Shared Composables with `WindowSizeClass` branches; TV renders
  as the expanded / 10-foot mode (larger typography, focus rings, generous spacing). No parallel
  screen tree unless a specific screen genuinely needs one.

### Category 3 — New TV-specific

**MQTT motion fallback**
- Subscribe to a configurable topic (e.g. a PIR sensor via HA). On message → fire the same
  `MOTION_DETECTED` event (wake screen, reset dim timer, publish presence).
- Enabled automatically when no camera is detected at startup.

**TV remote key mapping**
- `HostActivity.dispatchKeyEvent` maps configurable keycodes to app intents (settings bar open,
  control overlay). Consumed only for mapped combos; all other keys propagate to system / WebView.

**Ambient mode**
- Reuse the shipped mobile screensaver / ambient clock. On TV it dismisses on MQTT motion or
  remote keypress instead of camera motion; typography scaled for 10-foot viewing.

## 6. Phasing

| Phase | Deliverable | Status |
|---|---|---|
| **Phase 0 — Scaffold (PoC)** | `formfactor` dimension + `tv` source-sets + leanback manifest; app launches from the TV home screen and loads the HA dashboard; DPAD forwarded to WebView. | ✅ Done |
| **Phase 1 — Core parity** | MQTT service + telemetry + HA Discovery (`device_class = tv`); wake/dim/lock; hidden D-pad settings access; hybrid 10-foot layouts for onboarding + settings; ambient/screensaver mirrored. | ✅ Done |
| **Phase 2 — USB camera** | Camera2 external-camera motion; UVC (`AndroidUSBCamera`) fallback; MJPEG streaming from USB webcam; MQTT motion fallback when no camera. | ✅ Done |
| **Phase 3 — Hardening** | Multi-APK `versionCode` scheme (shipped via `TV_VERSION_CODE_OFFSET`). Remaining: low-end box smoke tests; kiosk-lockdown investigation (still out of v1). | 🔶 Partial |

## 7. Risks & open items

- ✅ **`MotionService` camera FGS type on cameraless TV boxes** — _Resolved._ The `tv` overlay in
  `presentation-core-application/src/tv/AndroidManifest.xml` uses `tools:replace` to declare
  `foregroundServiceType="camera|specialUse"` (+ `PROPERTY_SPECIAL_USE_FGS_SUBTYPE`), so the FGS
  is legal on Android 14+ whether or not a camera is present. Mobile keeps `type="camera"`.
- ✅ **Multi-APK `versionCode` scheme** — _Resolved._ Shared `applicationId`; the `tv` flavor sets
  `versionCode = base + TV_VERSION_CODE_OFFSET` (`1_000_000`) so Play serves mobile vs TV distinctly.
- ✅ **`RECORD_AUDIO` / WebRTC permissions** — _Resolved._ The host `tv` overlay marks
  `android.hardware.microphone` `required="false"`, and onboarding skips the mic requirement on TV.
- 🔶 **UVC library staleness** — `AndroidUSBCamera` (`libausbc`) is from 2023 with many open issues;
  kept as a **guarded fallback** behind Camera2, not the primary path. Pinned to `3.2.7` (the last
  real release — `3.3.3` was a phantom tag JitPack built on request that never published
  `libnative.aar`). Demo transitives (immersionbar, glide/webpdecoder, mmkv) are excluded, and
  `libuvc` is added explicitly because `libausbc` leaks `UsbControlBlock` through its public API.
  Also needs a non-null render surface (`HeadlessGlSurfaceTexture`) or `openCamera` crashes
  uncatchably on the engine thread.
- 🔴 **`GeckoView` performance on low-end TV boxes** — _Open._ `fossTv` is in the matrix but
  GeckoView is heavy; on cheap boxes steer users to `gmsTv` (no code change — both are built).
- 🔴 **HA dashboard native focus quality** — _Open._ Relying on HA's own focus (decision #9) means
  D-pad UX depends on the user's HA theme; the JS-focus-manager fallback remains a later item.
- 🔴 **Cheap-hardware coverage gap** — _Open._ Primary test devices are high-end; smoke-test at
  least one low-end box before broad release.

## 8. Explicitly out of scope for v1

- Kiosk lockdown (lock-task / home-app replacement) — regular app for now.
- PIN gate on settings access.
- Acoustic / microphone motion detection.
- Chromecast / Fire TV (no USB host).
