# Motion Detection

Kite uses the device's front camera to detect presence and automatically manage the display — waking it when someone is nearby and dimming or turning it off during idle periods.

The detection runs entirely on-device using low-resolution luma (brightness) analysis at 176×144 pixels. No image data is stored or transmitted.

---

## Enabling Motion Detection

Open **Settings** and find the **Mode detector** section at the top.

The master **Mode detector** toggle activates the camera service. It is on by default but requires all four timing values to be non-zero before it can be switched on.

---

## Settings Reference

| Setting | Range | Default | What it does |
|---|---|---|---|
| **Mode detector** | toggle | on | Starts/stops the camera presence service. |
| **Sensitivity** | 0–100 | 50 | How easily motion is detected. Higher values respond to subtle or distant movement; lower values require more prominent motion. |
| **Dim delay** | 0–300 s | 30 | Seconds of no motion before the screen dims. |
| **Screen timeout** | 0–3600 s | 60 | Seconds of no motion before the screen turns off completely. |
| **FAB delay** | 0–3600 s | 60 | Seconds of sustained motion required before the control button (FAB) appears on screen. Prevents the drawer handle from flashing up on brief, incidental motion. |

!!! info "Enable gate"
    The Mode detector toggle is disabled if any timing field is set to zero. Set all four numeric fields to a value greater than 0 first, then toggle the service on.

---

## Tuning Tips

**Screen dims too quickly** — increase *Dim delay*. 60–120 s is a comfortable range for a living room panel.

**Screen wakes from across the room** — lower *Sensitivity* toward 20–30. At high sensitivity the camera reacts to light changes (curtains moving, passing cars outside a window).

**Control drawer keeps disappearing** — increase *FAB delay*. On a touch-heavy dashboard 120 s or more keeps it accessible between interactions.

**Screen stays on too long after leaving** — reduce *Screen timeout*. For energy-conscious setups, 120–300 s is a reasonable balance.
