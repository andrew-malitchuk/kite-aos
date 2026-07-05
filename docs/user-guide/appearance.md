# Appearance

Adjust Kite's visual style and interface layout from **Settings → UI & UX**.

---

## Theme

| Option | Description |
|---|---|
| **Light** (default) | Standard light background. |
| **Dark** | Dark background, easier on the eyes in low-light rooms. |
| **Material You** | Adapts colors to match your device's wallpaper (requires Android 12 or later). |

Theme changes animate with a circular-reveal transition from the point of interaction.

!!! info "Material You availability"
    The Material You option is only available on Android 12 (API 31) and above. It will not appear on older devices.

---

## Dock Position

Controls where the control drawer handle (FAB) is anchored on screen.

| Option | Position |
|---|---|
| **Left** (default) | Handle appears on the left edge. |
| **Up** | Handle appears at the top edge. |

Choose based on where the tablet is mounted and how users will interact with it most naturally.

---

## Language

Kite is available in **English** and **Ukrainian**.

- On **Android 13 and later**: tapping Language opens the system's per-app language settings. Set the language there to apply it to Kite without changing the rest of your device.
- On **older Android versions**: an in-app dialog appears. Select your language and Kite restarts to apply the change.

---

## Reduce Motion

Toggle **Reduce motion** to disable all UI animations globally — page transitions, FAB appearance, circular-reveal theme changes, and all other Kite animations are replaced with instant cuts.

This improves responsiveness on lower-end tablets where animation rendering causes visible lag or missed touch events.

!!! tip
    Enable Reduce motion if the UI feels sluggish or if transitions stutter on your hardware. It has no effect on the WebView content itself, only on Kite's own UI chrome.
