# Implementation Plan

Candidate features and hardening work for Kite AOS, ordered by impact. This document is a
technical companion to [ROADMAP.md](ROADMAP.md): the roadmap says *what* the backlog is, this plan
says *what we can build next, why it is worth building, and where it lands in the module graph*.

Every item states the problem it solves. Items that only exist because "other kiosks have it" are
not in this list.

**Legend**

| Tier | Meaning |
|:--|:--|
| **T1** | Ecosystem reach — unlocks integrations we cannot reach today |
| **T2** | Survivability — the difference between "works in a demo" and "runs on a wall for a year" |
| **T3** | Home Assistant depth — richer two-way control |
| **T4** | Presence & display — better wake/dim behaviour on more hardware |
| **T5** | Content — screensaver and dashboard surfaces |
| **T6** | Scheduling — unattended time-based behaviour |
| **T7** | Project hygiene — distribution readiness |

---

## T1 — Ecosystem Reach

### 1.1 Local HTTP Control API

**What.** An embedded HTTP server on a configurable port (default `2323`) exposing a
command surface in the form `GET /?cmd=<command>&password=<secret>`, returning JSON.

**Why.** Today the only inbound control channel is MQTT, which requires a broker. A local HTTP
API removes that dependency entirely and — critically — the command vocabulary is the *de facto*
standard already understood by existing Home Assistant kiosk integrations. Matching that
vocabulary means those integrations drive Kite with **zero new code on the Home Assistant side**.
It also gives users a trivial `curl`/`rest_command` path for automations, shell scripts, and
Node-RED without any broker at all.

**Design notes.**

- New package `presentation-core-platform/source/api/`. No new HTTP dependency:
  `MjpegHttpServer` already implements a raw `ServerSocket` server with per-client coroutines —
  extract its socket/request-parsing core into a shared `EmbeddedHttpServer` and have both the
  MJPEG stream and the control API mount on it as route handlers.
- Split handlers by domain (`DeviceInfoHandler`, `ScreenNavHandler`, `AudioHandler`,
  `SettingsHandler`, `StreamingHandler`) rather than one large `when` block — keeps Detekt's
  cyclomatic-complexity rule satisfiable without suppressions.
- Commands map onto the existing `RemoteCommandBus` and the same use cases the MQTT command path
  already calls. The API is a *transport*, not a second implementation of the behaviour.
- Auth: optional shared password compared per request, read live from DataStore so a change in
  Settings takes effect without a restart. Empty password = no auth (explicitly opt-in, warned in
  the UI).
- Bind to the LAN interface only. Log the caller IP at DEBUG so an allowlist can be added later
  without guessing at real deployment topologies.

**Command set — first pass.**

| Group | Commands |
|:--|:--|
| Device | `deviceInfo`, `listSettings` |
| Navigation | `loadUrl`, `loadStartUrl`, `refreshWebView`, `clearCache`, `clearWebstorage` |
| Screen | `screenOn`, `screenOff`, `startScreensaver`, `stopScreensaver`, `triggerMotion` |
| Lifecycle | `restartApp`, `rebootDevice`, `toForeground`, `startApplication` |
| Capture | `getScreenshot`, `getCamshot` |
| Audio | `setAudioVolume`, `muteAudio`, `unmuteAudio`, `textToSpeech` |
| Settings | `setStringSetting`, `setBooleanSetting` |
| Access | `lockKiosk`, `unlockKiosk`, `getPinStatus` |

**Done when.** A Home Assistant instance using a generic kiosk REST integration can read device
state and drive screen/navigation on a Kite tablet with no custom templates.

---

### 1.2 Network Presence & Discovery

**What.** Announce the tablet on the LAN so it can be found without typing an IP: mDNS/zeroconf
service registration plus a UPnP/SSDP responder serving a `/device.xml` description document.

**Why.** Every current setup path starts with "find the tablet's IP address". That is the single
worst step in onboarding and the most common support question for any kiosk app. Discovery turns
it into a list the user picks from. It is also the prerequisite for a future companion Home
Assistant integration that auto-detects Kite devices.

**Design notes.**

- `presentation-core-platform/source/discovery/`, registered via `NsdManager` (zeroconf) with a
  service type such as `_kite._tcp`, advertising the control-API port and app version in TXT
  records.
- SSDP responder is a small UDP multicast listener answering `M-SEARCH` with the URL of
  `/device.xml`, served by the same embedded HTTP server as 1.1.
- Depends on 1.1 — no point advertising a port that serves nothing. Ship together.
- Re-register on network change (reuse the connectivity callback added in 2.4).

**Done when.** A fresh tablet appears by friendly name in a zeroconf browser on the same LAN
within 10 s of app start, and its `/device.xml` resolves.

---

## T2 — Survivability

This tier is the highest-value engineering work in this document. A wall-mounted dashboard is
judged on uptime, and every item below exists because a specific class of Android behaviour will
otherwise take the dashboard down silently, overnight, with nobody watching.

### 2.1 Wake & WiFi Lock Manager — **DONE**

**What.** Explicit lifetime management of a `PARTIAL_WAKE_LOCK` and a `WifiLock`
(`WIFI_MODE_FULL_LOW_LATENCY` on API 29+, `WIFI_MODE_FULL_HIGH_PERF` below), owned by a single
component and driven by preferences.

**Why.** With the screen off, aggressive OEM power management (and Doze) will park the WiFi radio
and deprioritise the process. The visible symptom is a dashboard that has silently lost its MQTT
session and shows stale data when you walk up to it in the morning. Holding the locks keeps the
radio and the process alive through the screen-off window.

**Design notes.**

- `presentation-core-platform/source/power/LockManager.kt`, `@Single`, lifecycle driven by the
  existing foreground services.
- **The WiFi lock must be user-disableable.** On some budget WiFi chipsets, holding the radio out
  of power-save for hours wedges the driver — WiFi drops and refuses to reassociate (often
  reported as a bogus authentication failure) until the radio is fully reset. Without an escape
  hatch this feature becomes a support burden on exactly the cheap tablets our users mount on
  walls. Ship the toggle in the same release as the feature, defaulted on, in Settings →
  Performance.
- The CPU wake lock is held unconditionally while the kiosk is running and is *not* tied to the
  WiFi toggle — they solve different problems and fail differently.

**Done when.** MQTT session and dashboard state survive an 8-hour screen-off period on a test
device with stock power settings.

---

**As built — where reality differed from this plan.**

- **The locks are app-scoped, not service-scoped.** The plan drives their lifetime from the existing
  foreground services. That would reintroduce the exact gap item 2.5 exists to close: a user who
  runs neither motion detection nor MQTT — a reasonable configuration for a plain dashboard — would
  get no locks at all, and they are needed *most* on a panel with no other reason to stay alive.
  `LockManager.start()` is therefore called from `Application.onCreate`, alongside 2.2–2.4.
- **No new preference pipeline.** The WiFi-lock toggle is a sixth field on the existing
  `ResiliencePreference` rather than its own DataStore: it is the same class of unattended-operation
  safeguard, it renders in the same Settings section, and it reuses the inverted `*_disabled`
  encoding so upgrading users keep the lock on.
- **No new permission.** `WifiLock.acquire()` is gated on `WAKE_LOCK`, which the manifest already
  declares. `ACCESS_WIFI_STATE` is only needed to *query* WiFi state, which this does not do.
- **The WiFi lock does less on modern Android than the plan implies.** From API 29
  `WIFI_MODE_FULL` became a no-op and the platform claims to hold the association itself, leaving
  `WIFI_MODE_FULL_LOW_LATENCY` as the only mode with an effect — and it degrades to the old
  full-lock behaviour once the app is not foreground, which is the screen-off window we care about.
  Below API 29, `WIFI_MODE_FULL_HIGH_PERF` genuinely disables radio power-save. So the lock is
  requested everywhere but earns its keep mostly on older hardware, which is also where the driver-
  wedging failure that justifies the toggle lives.
- **No teardown call.** Both locks are meant to be held for the life of the process and the
  framework releases them when it dies, so there is no `stop()`. The one release path that matters —
  the user switching the WiFi lock off — is handled by the preference collector.
- **The CPU lock is acquired without a timeout**, which needs `@SuppressLint("WakelockTimeout")`. A
  timeout is not a safety net here; it would expire mid-night and hand the process back to the power
  manager during precisely the window the lock exists to cover.
- **The 8-hour acceptance test has not been run.** It needs a physical device left overnight, so the
  claim in "Done when" above is still unverified.

---

### 2.2 Crash Auto-Relaunch — **DONE**

**What.** An `UncaughtExceptionHandler` that persists a crash report and schedules a one-shot
`AlarmManager` alarm to relaunch `HostActivity` a few seconds later, then lets the process die.

**Why.** Nothing currently brings the app back after a fatal crash. `START_STICKY` restarts a
*service*, not the Activity, so an unattended device drops to the launcher and stays there until
somebody physically touches it — which on a wall-mounted panel with no visible launcher can mean
days. The alarm fires from *outside* the dead process, so this works with no overlay permission,
no device-owner privilege, and no surviving service.

**Design notes.**

- `presentation-core-platform/source/diagnostics/`. Chain to the previous default handler so
  Firebase Crashlytics (gms flavor) still receives the crash.
- **A rate limiter is mandatory, not optional.** A deterministic startup crash plus an
  unconditional relaunch is a boot loop that will flatten the battery and make the device
  unrecoverable without ADB. Rule: if N crashes (default 3) occurred within a rolling window
  (default 10 min), suppress the relaunch and let the device come to rest.
- Gate the whole feature behind a Settings toggle (Settings → Performance), default on.
- Crash reports land in app-private storage, exposed through the control API
  (`cmd=getDiagnosticsLog`) and a Settings → Diagnostics screen.

**Done when.** A forced crash relaunches the kiosk within ~5 s; three forced crashes in a row stop
relaunching and leave a readable report.

---

### 2.3 Memory Recovery & Scheduled Reload — **DONE**

**What.** Two related mechanisms: reactive handling of `onTrimMemory` / low-memory callbacks, and
a proactive scheduled WebView reload.

**Why.** A `WebView`/`GeckoView` rendering a live dashboard for weeks accumulates GPU texture and
JS heap. The end state is a renderer kill (blank white page) or an OOM. A periodic reload resets
that cost. Doing it *while the screensaver is up* means the user never sees it happen.

**Design notes.**

- Two triggers: **stealth reload** (fires only while the screensaver is active — invisible to the
  user) and **daily scheduled reload** (fires at a configured hour regardless of state, for
  deployments that never idle).
- **Both must use `AlarmManager.setAndAllowWhileIdle()`, never a coroutine `delay` or `Handler`.**
  In Doze, in-process timers do not fire — which is precisely the overnight window in which the
  reload is most needed. A timer-based implementation appears to work in testing and silently
  never runs in production.
- Preserve and restore the current dashboard path across the reload, so a reload does not kick the
  user back to the home URL.
- On memory-pressure callbacks while the screen is *on*, defer the reload until the screensaver
  engages rather than blanking a dashboard somebody is looking at.

**Done when.** RSS of a device left running for 72 h with a live dashboard stays flat rather than
climbing monotonically.

---

### 2.4 Dashboard Connection Monitor — **DONE**

**What.** Detect that the dashboard backend has gone away, then **pause the WebView** for the
duration of the outage, poll for recovery on a slow interval, and resume + reload once the backend
answers.

**Why.** When a Home Assistant instance restarts, its frontend JavaScript retries its WebSocket
roughly once per second, indefinitely. Behind a reverse proxy or an intrusion-prevention layer,
that traffic pattern is indistinguishable from an attack: the tablet gets rate-limited or its IP
banned, and then stays broken *after* Home Assistant is healthy again. Pausing the WebView stops
the retry storm at the source. This is a bug class the app cannot currently avoid, and the user's
only fix is a manual reboot.

**Design notes.**

- State machine: `HEALTHY → SUSPECT (grace period, default 20 s) → PAUSED (WebView paused, TCP
  probe every 10 s) → HEALTHY (resume + reload)`.
- The grace period matters — brief network hiccups must not trigger a pause/resume cycle.
- Inject the clock, the probe, and the backoff timings so the state machine is unit-testable with
  a fake clock. This is the one piece of survivability logic that is worth real test coverage.
- Also fires an immediate one-shot reload **on screen wake** if the last known state was an error:
  covers the "backend restarted while the tablet slept" case, where in-process recovery timers
  were frozen by Doze and never ran.
- Feed the state into the MQTT `last_seen` / availability entity from 3.1.

**Done when.** Restarting Home Assistant while the tablet is asleep results in a healthy dashboard
on wake, with no more than a handful of connection attempts logged during the outage.

---

**As built — where reality differed from this plan.**

- **The crash-relaunch setting cannot be read at crash time.** DataStore's API is `suspend`, and a
  coroutine started while the process is being torn down is not guaranteed to run. The flag is
  therefore mirrored continuously into `SharedPreferences` (`CrashRelaunchSettingMirror`) and the
  handler reads that. This is the only preference in the app not served from Proto DataStore, and
  the rate-limiter state is written with `commit()` rather than `apply()` for the same reason —
  an `apply()` that never lands would reset the limiter and re-enable the boot loop.
- **`getDiagnosticsLog` is not exposed.** The plan routes crash reports through the control API,
  which is item 1.1 and does not exist yet. Reports are written to app-private storage and read
  back through `CrashDiagnosticsStore.readReports()`; the Settings viewer is still to build.
- **Reload path preservation needed no machinery.** The plan calls for preserving and restoring the
  dashboard path across a reload; `reload()` re-requests the current page, so it is preserved for
  free. The distinction that mattered instead was the opposite one: the inactivity reset must
  deliberately *discard* the path, so it assigns the home URL rather than reloading.
- **2.4 does not feed device availability.** The plan says to feed connection state into the
  `last_seen`/availability entity from 3.1. That would be wrong: during a backend outage the panel
  itself is healthy, and marking the device unavailable would hide its own working controls exactly
  when someone is diagnosing the server. It is published as a separate `dashboard` binary sensor
  (`device_class: connectivity`, `entity_category: diagnostic`) instead.
- **Proto3 field presence bit again.** `scheduled_reload_hour` is stored offset by one, because 0 is
  a legitimate hour (midnight) and would otherwise be indistinguishable from "never configured".
  The default-on booleans are stored inverted, same idiom as `mqtt_data.proto`.
- **`DashboardConnectionMachine` has no tests yet.** It was built pure and injectable precisely so
  it could be tested, but the project still has no test suite at all, so the coverage this plan
  calls for is outstanding.

---

### 2.5 Process Priority Floor

**What.** A minimal foreground service with an `IMPORTANCE_MIN` notification channel, running
whenever the kiosk is active and no other foreground service (motion, MQTT) happens to be running.

**Why.** `MotionService` and `MqttService` currently provide process-priority protection as a side
effect. A user who disables both — a perfectly reasonable configuration for a simple dashboard —
loses that protection entirely and gets their app killed during screen-off on aggressive ROMs.
This guarantees a floor independent of feature toggles.

**Design notes.** `IMPORTANCE_MIN` keeps the notification silent and collapsed, which matters on a
wall panel. Start it only when no other foreground service is up, to avoid a second permanent
notification.

---

## T3 — Home Assistant Depth

### 3.1 Expanded Entity Set & Discovery Lifecycle — **DONE**

**What.** Additional auto-discovered entities, correct entity categorisation, and proper discovery
teardown.

**Why.** The device currently appears in Home Assistant as a partial citizen. Filling out the
sensor set turns the tablet into a first-class device users can build automations against
(e.g. "if the panel has been offline for 10 minutes, notify me").

**New entities.**

| Domain | Entity | Purpose |
|:--|:--|:--|
| `sensor` | `uptime` | Seconds since boot — detects silent restarts |
| `sensor` | `app_version` | Fleet management across multiple panels |
| `sensor` | `ip_address` | Removes the "which IP is this one" problem |
| `sensor` | `current_url` | What the panel is actually showing right now |
| `sensor` | `ram_usage` | Early warning before a renderer kill |
| `sensor` | `illuminance` | Exposes the ambient light sensor (see 4.1) |
| `binary_sensor` | `last_seen` / availability | Marks the panel unavailable when it stops publishing |
| `button` | `clear_cache` | One-click recovery from a wedged dashboard |

**Implementation notes.**

- Follow the existing `*ConfigMqtt` + publisher pattern in `data-mqtt-impl`. New
  `CompanionTelemetryMqtt` carrying the low-frequency values; `current_url` published reactively
  from a `StateFlow` with a ~1 s debounce; `uptime` / `ip_address` / `ram_usage` ride the existing
  telemetry ticker.
- **Diagnostic-class entities must set `entity_category: diagnostic`.** Without it, uptime,
  version and RAM clutter the primary device card and make the useful controls harder to find.
- **Discovery teardown**: disabling MQTT or an individual sensor must publish an *empty retained
  payload* to the entity's `config` topic. Otherwise the entity is orphaned in Home Assistant
  forever and the user has to clean it up by hand — a genuinely bad first impression.
- **Boolean value-template gotcha**: Home Assistant's Jinja renders booleans capitalised
  (`True`/`False`) while `payload_on`/`payload_off` are conventionally lowercase. A
  `binary_sensor` template must therefore end in `| lower` — e.g.
  `{{ value_json.charging | lower }}`. Without it the state comparison silently never matches and
  the entity never updates. This applies to every existing and new binary sensor; audit the
  current ones as part of this item.
- Per-entity opt-out toggles in Settings → MQTT.

---

**As built — where reality differed from this plan.**

- **`current_url` already existed.** `UrlConfigMqtt` + the `_url/url/state` topic shipped earlier,
  published from `MainViewModel`. It gained `entity_category: diagnostic` and an availability topic;
  nothing else was needed.
- **The boolean value-template audit found nothing to fix.** There are zero `value_template`
  entries in the codebase — every boolean entity uses literal `payload_on`/`payload_off`. The
  `| lower` rule still matters for any *future* templated entity and is now recorded in
  `data-mqtt-impl/CLAUDE.md`, but no existing entity was affected.
- **There was no telemetry ticker to ride.** This plan assumed uptime / IP / RAM could join "the
  existing telemetry ticker", but battery is broadcast-driven (`ACTION_BATTERY_CHANGED` via
  `BatteryReceiver`) and no periodic publisher existed. A new 60 s ticker
  (`MqttService.publishCompanionTelemetry`) was added instead.
- **Availability is MQTT Last Will, not a `binary_sensor`.** The plan listed `last_seen` as a
  `binary_sensor`. The correct mechanism is an `availability_topic` on every entity backed by a
  retained Last Will, so the broker reports the panel gone with nothing needing to notice. A graceful
  shutdown publishes `offline` explicitly, because a clean DISCONNECT makes the broker discard the
  will.
- **`keepAlive` could not be shortened** to detect absence faster. `kmqtt` sends PINGREQ only inside
  the 0.9x–1.0x `keepAlive` window while the connection loop polls `step()` every 5 s, so a shorter
  `keepAlive` narrows that window below the poll interval and the client trips its own keep-alive
  timeout. Left at the 60 s default; absence shows up in ~90 s.
- **`ram_usage` is device-wide, not per-process.** Android's low-memory killer decides from free
  system memory, so device-wide usage is what actually predicts the renderer kill this sensor exists
  to forewarn.
- **`clear_cache` preserves the session.** Only HTTP and image caches are dropped
  (`ClearFlags.ALL_CACHES` on GeckoView, `clearCache(true)` on Android WebView). Clearing cookies or
  local storage would sign the panel out of Home Assistant, turning a remote recovery into a physical
  visit.
- **Opt-out flags are stored inverted.** proto3 has no field presence for scalars, so a new `bool`
  reads back `false` for every config written before it existed. Stored as `*_disabled`, that same
  `false` means "not disabled" and upgrading users keep their sensors. `illuminance` was left out —
  it depends on 4.1.

---

### 3.2 Inbound Command Payload Compatibility

**What.** Accept a JSON object on the command topic where **each key is a command**, and support
**multiple commands in one payload**.

**Why.** This is the payload shape already used by the incumbent open-source panel apps, so users
migrating from them keep their existing Home Assistant automations verbatim. Batching also lets a
single automation set brightness, volume and URL atomically instead of racing three publishes.

**Command vocabulary.** `wake` / `screenOn` (bool), `brightness` (0–255), `volume` (0–100), `url`
(string), `reload`, `relaunch`, `clearCache` (bool triggers), `speak` (string), `audio` (URL),
`camera` (bool).

**Design notes.** Extend the existing MQTT command flow into a `RemoteCommand` sealed class in
`domain-core`; both the MQTT subscriber and the HTTP API (1.1) decode into that same type, so
behaviour is defined once. Unknown keys are logged and ignored, never fatal.

---

## T4 — Presence & Display

### 4.1 Ambient Light Sensor Dimming

**What.** Map `Sensor.TYPE_LIGHT` readings onto screen brightness between user-configured minimum
and maximum values.

**Why.** Camera-based motion is the only presence input today, and many target devices — TV boxes,
tablets mounted in a recess, units with a dead or covered front camera — have no usable camera at
all. The light sensor is nearly universal, needs no permission, and costs almost no power. It also
solves a problem motion cannot: a panel at full brightness in a dark bedroom at 3 a.m.

**Design notes.**

- `presentation-core-platform/source/sensor/LightSensorController.kt`, feeding the existing
  brightness pipeline in `DevicePowerManager`.
- Practical mapping: below ~10 lx → minimum brightness; above ~1000 lx → maximum; interpolate
  between. Offer three response curves (linear / aggressive / gentle) rather than exposing raw lux
  numbers in the UI.
- **Two dampers are required or the screen will visibly pulse**: a debounce (~1 s minimum between
  applied changes) and a minimum-delta threshold (~2 %) below which a change is discarded. Ambient
  sensors are noisy and a naive implementation flickers.
- Must yield to explicit user/MQTT brightness commands — add a pause flag rather than fighting the
  user.
- Runs independently of, or alongside, camera motion. Publishes `illuminance` (see 3.1).

---

### 4.2 Face-Confirmed Wake (gms flavor only)

**What.** A two-stage wake path: existing luma-difference motion detection acts as a cheap trigger
that arms a face detector; a wake only fires when a face is actually found.

**Why.** Frame-differencing wakes on pets, curtains, shadows and passing headlights. On a bedroom
or hallway panel that means a screen lighting up all night. Requiring a face means the panel wakes
when a *person* approaches it and stays dark otherwise.

**Design notes.**

- ML Kit face detection is a Google Play Services component and is **only available in the `gms`
  flavor**. The `foss` flavor keeps pure motion detection. Implement behind an interface in
  `presentation-core-platform` with the real detector in a `gms` source set and a no-op in `foss`
  — the same shape as the existing analytics provider split.
- Staging: motion arms face search; face search stays armed for a cooldown (~5 s) after the last
  motion event, so somebody who approaches and then stands still is still detected; the detector
  stops when the cooldown lapses. This bounds the cost — the expensive stage only runs in short
  bursts.
- Lazy-init the detector (the model is several MB) so users who never enable the mode never pay
  for it.
- Publishes `binary_sensor` `face` with `device_class: occupancy` (see 3.1).
- Threading: frame analysis runs on the camera executor while arm/disarm arrives on main — keep
  the shared state confined or explicitly synchronised.

---

## T5 — Content

### 5.1 Screensaver Photo Source Abstraction

**What.** Formalise photo provisioning behind an interface —
`getPhotos()`, `supportsAutoSync()`, `suspend sync(): SyncResult`, `sourceType` — and ship several
implementations behind it.

**Why.** The screensaver is the surface users look at most of the day, and photo source is the
single most-requested variation. Defining the seam *before* adding the second source avoids a
rewrite; every later source is then an additive change with no risk to the existing ones.

**Sources, in build order.**

1. **Local folder** — a device directory, watched via `FileObserver` for auto-sync. No network, no
   auth, works offline; the correct default.
2. **Home Assistant media folder** — reads the already-configured HA media directory. This is the
   highest-value remote source precisely because it needs **no new credentials**: the connection
   the kiosk already has is enough. Lowest-friction path to "my photos on the panel".
3. **Immich** — self-hosted photo server, widely deployed in the same self-hosting community that
   deploys Home Assistant. Requires its own auth flow and a metadata mapper.
4. **Unsplash** — zero-setup fallback for users who want a nice-looking panel immediately and have
   no photo library to point at.

**Design notes.** A shared `PhotoCache` (bounded on-disk, LRU) sits under the remote sources so the
slideshow never blocks on network and degrades gracefully offline. `SyncResult` carries
found/new/failed counts so Settings can show a meaningful sync status instead of a spinner.

---

## T6 — Scheduling

### 6.1 Generic Scheduled Action Engine

**What.** A single scheduling subsystem — `ScheduledAction(id, trigger, recurrence, action,
conditions, enabled)` — rather than a purpose-built sleep/wake feature.

**Why.** Sleep/wake, daily reload (2.3), auto-reboot, and "switch to a different dashboard at
dinner time" are all the same problem: *do X at time T, repeatedly, surviving reboots*. Building
one engine and expressing sleep/wake as one action type costs marginally more up front and removes
three future re-implementations. The existing `AutoRebootScheduler` becomes its first migrated
consumer.

**Design notes.**

- Model serialised with `kotlinx.serialization` into DataStore. Actions are a sealed hierarchy in
  `domain-core`: `Sleep`, `Wake`, `Reload`, `LoadUrl`, `Reboot`, `SetBrightness`.
- `AlarmManager.setAlarmClock()` per active rule; re-register on `ACTION_BOOT_COMPLETED`,
  `ACTION_TIME_CHANGED` and `ACTION_TIMEZONE_CHANGED` — all three, or rules silently stop firing
  after a DST shift or a reboot.
- Overnight spans (sleep 23:00 → wake 07:00) are handled by always computing the *next future*
  occurrence rather than reasoning about ordering within a day.
- Requires `SCHEDULE_EXACT_ALARM`; prompt when a rule is first created, not at app start.
- Optional conditions (e.g. "only if no motion in the last 15 min") keep automations from fighting
  the user.
- Settings → Schedule: rule list with add/edit/delete, per-rule toggle, time picker.

---

## T7 — Project Hygiene

### 7.1 Third-Party Provenance

**What.** A `NOTICE` file enumerating every redistributed third-party component with its licence,
upstream source URL and vendored location; full licence texts under `third_party/licenses/`; a
checksum manifest for any binary artifact checked into the tree.

**Why.** Kite targets F-Droid, whose review process checks exactly this. Doing it incrementally as
dependencies are added is cheap; reconstructing it later for a tree that already contains vendored
AARs and native libraries is not. It is also the honest thing to do for the projects whose work
ships inside our APK.

**Design notes.** If any prebuilt `.aar`/`.so` is ever committed, its SHA-256 goes in a manifest
alongside a note on where the expected hash came from — otherwise nobody can verify the binary was
not tampered with, and a reproducible-build claim is unfounded.

---

### 7.2 Startup Profile & Size Gate

**What.** A Baseline Profile for the startup and first-dashboard-paint path, plus a Gradle check
that fails the build when APK size crosses a committed baseline by more than a set margin.

**Why.** Startup time is the most visible quality signal on the low-end hardware this app targets,
and a baseline profile is one of the cheapest wins available. The size gate exists because APK
growth is otherwise invisible — a dependency that quietly adds 8 MB is only noticed by users on
metered connections after release.

---

## Deliberately Out of Scope

Recorded so the decisions do not get relitigated:

- **On-device voice / wake-word.** Large native model payloads, per-locale accuracy work, and
  echo-cancellation tuning. The dashboard itself already provides voice through Home Assistant.
- **NVR-specific video timelines.** Deep integration with any single camera backend is a product in
  its own right and pulls in heavy media dependencies.
- **Multi-room audio.** Out of the kiosk's remit; the platform already has media players that do
  this well.
- **RTSP publishing.** The existing MJPEG stream satisfies the "use the tablet camera in Home
  Assistant" use case. RTSP would require vendored encoder binaries for a marginal quality gain.
- **Cloud accounts / hosted services.** Kite has no backend and should not acquire one.

---

## Suggested Sequencing

| Release | Contents | Theme |
|:--|:--|:--|
| Next | ~~2.1~~ (done), ~~2.2~~ (done), ~~2.4~~ (done) | Stop losing devices overnight |
| +1 | 1.1, 1.2, 3.2 | Reachable without a broker, findable without an IP |
| +2 | ~~3.1~~ (done), 4.1, ~~2.3~~ (done), 2.5 | Full companion device; dimming on camera-less hardware |
| +3 | 5.1, 6.1 | Content and unattended scheduling |
| Ongoing | 4.2, 7.1, 7.2 | Flavor-gated and hygiene work |

Rationale: T2 first. Every feature added before the survivability work is a feature that will be
reported as broken by users whose real problem is that the app was killed at 03:00.
