# Backup & Restore

Kite can export all settings to a JSON file and import them back later. This is useful for:

- Backing up a working configuration before making changes.
- Deploying identical settings across multiple kiosk tablets.
- Migrating settings after a clean app install.

Both actions are in **Settings → Advanced**.

---

## Export Configuration

1. Tap **Export configuration**.
2. Android's file picker opens ("Create Document"). Choose a folder and file name, then tap **Save**.
3. Kite writes all current settings — Dashboard URL, whitelist, MQTT credentials, motion detector values, theme, language, etc. — into the JSON file.

Store the exported file somewhere safe (cloud storage, a shared drive) if you plan to use it for multi-device deployment.

---

## Import Configuration

1. Tap **Import configuration**.
2. Android's file picker opens ("Open Document"). Navigate to your previously exported JSON file and tap **Open**.
3. Kite reads the file and applies all settings immediately. No restart is required.

!!! warning "All settings are overwritten"
    Importing replaces your current configuration entirely. If you want to keep part of your current setup, export it first so you can restore it.

---

## Multi-Device Deployment

To set up multiple tablets identically:

1. Configure one device fully (onboarding + all settings).
2. Export the configuration to a shared location (e.g. a USB drive, cloud folder, or local file server).
3. On each additional device, install Kite, complete the [onboarding wizard](first-time-setup.md) to grant permissions and set an initial URL, then immediately import the exported file to overwrite with the canonical configuration.

!!! info
    Permissions granted during onboarding (camera, microphone, device admin, etc.) are Android system grants and cannot be exported or imported. Each device must go through Step 2 of the wizard manually.
