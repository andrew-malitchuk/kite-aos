# Screensaver

Kite can display a screensaver overlay when the screen dims due to inactivity. The screensaver sits between the dim state and the full lock — the display stays on but the dashboard is hidden behind an image slideshow. It dismisses automatically when motion is detected (if the [Motion Detector](motion-detection.md) is active).

---

## Enabling the Screensaver

Open **Settings → Screensaver** and flip the master toggle to **on**.

---

## Settings Reference

| Setting | What it does |
|---|---|
| **Enabled** | Shows or hides the screensaver overlay entirely. |
| **Activation delay (sec)** | Seconds of inactivity after the screen dims before the screensaver appears. Set to `0` to disable the delay (screensaver appears immediately on dim). |
| **Slide interval (sec)** | Seconds between image transitions in slideshow mode. Set to `0` to show a single static image without cycling. |
| **Show clock** | Overlays a clock on top of the screensaver images. |
| **Pick image folder** | Opens a folder picker to select the directory of images used as screensaver slides. Supports common image formats (JPEG, PNG). |

---

## Choosing Images

Tap **Pick image folder** to open the Android storage picker. Navigate to a folder containing the images you want to use as screensaver slides, then confirm the selection.

- Images are displayed in the order they appear in the folder.
- The slide interval controls how long each image is shown before transitioning to the next.
- If no folder is selected or the folder is empty, the screensaver shows a blank dark overlay.

!!! tip
    Use a dedicated folder on internal storage or an SD card. For Home Assistant setups, you can sync a folder via a file-sharing add-on (e.g. Samba, Nextcloud) to update slides remotely without touching the tablet.

---

## Interaction with Motion Detection

If the [Motion Detector](motion-detection.md) is active:

1. Screen dims after **Dim delay** seconds of no motion.
2. Screensaver appears after **Activation delay** additional seconds of inactivity.
3. Motion is detected → screensaver dismisses immediately and the dashboard is revealed.
4. Screen locks after **Screen timeout** seconds if no motion occurs while dimmed.

If Motion Detection is disabled, the screensaver dismisses on any touch event.

---

## Troubleshooting

**Screensaver never appears** — check that the Screensaver toggle is on and that *Activation delay* is set to a non-negative value. Also confirm that the screen dims first (the [Mode detector](motion-detection.md) must be active and *Dim delay* must be non-zero).

**Slideshow shows only one image** — verify that *Slide interval* is greater than `0` and that the selected folder contains more than one image.

**Images are not loading** — confirm Kite has storage read permission. If you recently changed the folder, tap **Pick image folder** again to re-grant access to the new location.
