# Web Kiosk

This page covers the settings that control which web pages are loaded, which rendering engine is used, and how the kiosk handles navigation outside the dashboard.

Navigate to **Settings → Web kiosk** to find all options below.

---

## Dashboard URL

The primary URL Kite loads on startup. See [Home Assistant & MQTT](home-assistant-and-mqtt.md#dashboard-url) for details on how to find and set it, including the auto-discovery scan.

---

## URL Whitelist

A list of additional hosts the WebView is allowed to navigate to. Requests to any URL not matching the Dashboard URL or the whitelist are blocked.

Enter hosts as comma-separated values, e.g.:

```
http://192.168.1.100:8123, http://192.168.1.200
```

Leave at the default `http://` (empty) if you want the kiosk locked to the dashboard URL only.

---

## Browser Engine

Kite supports two web rendering engines:

| Engine | Based on | Best for |
|---|---|---|
| **Android WebView** (default) | Chromium (system component) | General dashboards, broad compatibility. |
| **GeckoView** | Firefox | Better WebRTC support, stronger codec coverage, improved camera stream reliability. |

!!! info "GeckoView availability"
    GeckoView is only available on Android 8.0 (API 26) and above. On older devices the toggle is not shown.

Switch engines by tapping the engine icon toggle. The change takes effect immediately — the WebView reloads.

!!! tip "When to choose GeckoView"
    If your Home Assistant dashboard includes camera streams, two-way audio, or WebRTC-based entities, GeckoView generally handles them more reliably than the system WebView.

---

## Allowed Applications

Tap **Application** to open the allowed-applications picker. This is the list of apps that Kite's control drawer can launch — i.e., the only apps users can reach from the kiosk.

Add or remove apps in this list to define the permitted launcher scope. Apps not on this list cannot be opened from within Kite.

---

## Auto-Return to Kiosk

**Default: on.**

When enabled, Kite automatically returns to the dashboard after a user has navigated to an external app. The return happens as soon as Kite regains focus (the user presses Back or switches back).

Toggle this off if you need users to stay in an external app indefinitely without being redirected.
