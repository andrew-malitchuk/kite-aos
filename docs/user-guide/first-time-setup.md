# First-Time Setup

When you launch Kite for the first time, the onboarding wizard walks you through four steps to get your kiosk up and running. You cannot skip steps — each one builds on the previous.

!!! note "System bars"
    The status bar and navigation bar are hidden throughout onboarding. Swipe from the edge to reveal them temporarily if you need to check another app mid-setup.

---

## Step 1 — Welcome

An introduction screen explaining what Kite does. No input required — tap **Next** to continue.

---

## Step 2 — System Permissions

Kite requires six Android permissions to operate as a kiosk. The **Next** button stays disabled until every permission is granted.

| Permission | Why it's needed |
|---|---|
| **Camera** | Powers motion detection — the camera watches for movement to wake or dim the screen. |
| **Microphone** | Required by WebRTC for Home Assistant's camera and video-call streams, even if you only plan to watch (not talk). |
| **Post Notifications** | Lets Kite show a persistent notification for its background services (required on Android 13+). |
| **Overlay (Draw over other apps)** | Keeps the kiosk interface on top whenever another app tries to surface. |
| **Device Administrator** | Allows Kite to lock the screen programmatically (used by the MQTT lock command). |
| **System Settings (Write Settings)** | Lets Kite control screen brightness automatically. |

Tap each toggle. Android will open the relevant system settings screen for the more sensitive permissions (Device Admin, Overlay, Write Settings) — grant access there, then return to Kite.

!!! warning "Device Administrator"
    Without Device Administrator, the MQTT screen-lock command will not work. You can grant it later in **Settings → Security → Device admin apps**, but the toggle will remain unavailable in Kite until you do.

---

## Step 3 — URL Configuration

Enter the two URLs that define your kiosk's web boundaries.

| Field | Purpose |
|---|---|
| **Dashboard URL** | The page Kite loads on startup (typically your Home Assistant URL, e.g. `http://192.168.1.100:8123`). Must start with `http://` or `https://` and be longer than 7 characters. |
| **Whitelist** | Additional hosts the WebView is allowed to navigate to. Optional — leave at the default `http://` if you don't need it. |

Both fields are prefilled with `http://`. The **Next** button activates once the Dashboard URL passes validation.

!!! tip "Don't know your Home Assistant URL?"
    After setup you can use **Settings → Web kiosk → Scan for Home Assistant** to discover it automatically on your local network.

---

## Step 4 — Ready to Go

Setup is complete. Tap **Finish** to save your URLs and open the main kiosk screen.

Everything you configured here can be changed later in [Settings](../getting-started/configuration.md).
