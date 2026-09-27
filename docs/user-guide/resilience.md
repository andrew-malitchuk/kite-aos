# Resilience & Interaction

A wall-mounted panel is unattended by definition — nobody is standing there to notice the app crashed at 3 a.m., or that the dashboard has been stuck on a spinner since Home Assistant restarted. These two Settings sections cover the safeguards that keep the panel running on its own, and the ways you can reach it when the on-screen controls are impractical.

---

## Resilience

Navigate to **Settings → Resilience**. Every safeguard here is independent — turn off anything that misbehaves on your hardware without losing the rest.

| Setting | Default | What it does |
|---|---|---|
| **Crash auto-relaunch** | On | If the app crashes, it relaunches itself a moment later instead of leaving the device sitting on the launcher. Rate-limited: three crashes in ten minutes suppress the relaunch, so a genuinely broken build doesn't turn into a battery-draining boot loop. |
| **Connection monitor** | On | Detects that the dashboard backend has gone away and pauses the WebView for the outage. Without it, the page keeps retrying, which on some setups gets the panel rate-limited or IP-banned by the very server it is trying to reach. Recovery is automatic: the dashboard reloads as soon as the backend answers again. |
| **Memory recovery** | On | Silently reloads the dashboard when Android reports memory pressure, so weeks of accumulated WebView memory don't end in a blank page or a renderer kill. The reload waits for the screensaver, so you never see it happen. |
| **Keep WiFi awake** | On | Holds the WiFi radio out of power-save while the screen is off, so MQTT and the dashboard survive the night instead of quietly dropping. |
| **Scheduled reload** | Off | Reloads the dashboard once a day at the hour you choose (0–23), whether or not anything looks wrong. A blunt instrument, but the reliable one for dashboards with heavy custom cards. |

!!! tip "Which one do I actually need?"
    Leave the defaults alone unless something is wrong. The one worth turning **on** deliberately is **Scheduled reload** — pick an hour nobody looks at the panel (3 a.m. works) if your dashboard tends to get sluggish after a few days.

!!! info "Keep WiFi awake and budget hardware"
    A handful of cheap WiFi chipsets wedge their driver when the radio is held out of power-save for hours. If your panel drops off the network overnight *with* this on, try turning it off — that inverts the usual cause and effect, but it is a real failure mode on low-end tablets.

### Dashboard reachability in Home Assistant

When MQTT is enabled, the connection monitor publishes its verdict as `binary_sensor.<clientId>_dashboard`. This is deliberately separate from the device's own availability: during a Home Assistant restart the panel is perfectly healthy, so the device stays available and only this sensor flips. See [Home Assistant & MQTT → Diagnostic Sensors](home-assistant-and-mqtt.md#diagnostic-sensors).

---

## Interaction

Navigate to **Settings → Interaction**.

| Setting | Default | What it does |
|---|---|---|
| **Inactivity page reset** | Off (`0`) | Returns the dashboard to your home URL after this many minutes of no touch or remote input. Set to `0` to disable. Useful when guests wander off into a sub-page and nobody navigates back. Range: 0–240 minutes. |
| **Volume-button gesture** | Off | Opens the control drawer after a burst of hardware volume presses. |
| **Press count** | 3 | How many presses the gesture needs. Range: 3–10. |

### Volume-button gesture

For a panel behind glass, wall-mounted, or running as the device launcher, the on-screen FAB can be awkward or invisible. The gesture gives you a physical way in.

- Press **volume up or volume down** the configured number of times, each press within about two seconds of the last. Pause too long and the count resets.
- Only the press that *completes* the gesture is swallowed. Every earlier press still adjusts the volume normally, so the keys keep working as volume keys.
- The minimum count is 3 for exactly that reason — a one- or two-press gesture would fire during ordinary volume adjustment and make the hardware keys unusable.

!!! tip "Android TV"
    On TV there is also a hidden D-pad sequence (Up, Up, Down, Down, Left, Right, Left, Right) that opens the control drawer. The volume gesture works on both TV and mobile.

---

## Troubleshooting

**The app keeps restarting in a loop** — crash auto-relaunch gives up after three crashes in ten minutes, so a loop should stop on its own. If it doesn't, the crashes are more than ten minutes apart; turn the setting off and investigate the underlying crash.

**The dashboard goes blank and comes back** — that is memory recovery doing its job. It normally waits for the screensaver, so seeing it means the device was under real memory pressure.

**Volume keys stopped changing the volume** — you've likely set the press count to its minimum and are hitting the gesture by accident. Raise the count, or turn the gesture off.

**Scheduled reload doesn't fire** — some aggressive OEM power managers kill background alarms. Check that Kite is excluded from battery optimisation in Android's settings.
