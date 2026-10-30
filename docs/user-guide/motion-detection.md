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

## Quiet Hours

By default, motion wakes the screen at any hour — useful for a kitchen or living-room panel, less so for one mounted in a bedroom or hallway that shouldn't light up every time someone walks past at 3 a.m.

**Quiet hours** add a nightly window during which Kite ignores motion entirely. The screen still dims and locks on the normal *Dim delay* / *Screen timeout* schedule below — it just never wakes back up from presence until the window ends.

| Setting | Range | Default | What it does |
|---|---|---|---|
| **Quiet hours** | toggle | off | Suppresses motion-triggered wake during the window below. |
| **Start hour** | 0–23 | 22 | Hour the quiet window begins (24-hour clock). |
| **Start minute** | 0–59 | 0 | Minute the quiet window begins. |
| **End hour** | 0–23 | 7 | Hour the quiet window ends. |
| **End minute** | 0–59 | 0 | Minute the quiet window ends. |

The window can cross midnight — the default, 22:00 to 07:00, means "all night." If start and end are set to the exact same time, the window is zero-length and never engages, rather than silently blocking motion for the whole day.

!!! info "Applies on the next restart"
    Like the rest of the Mode detector section, a quiet-hours change is picked up the next time the motion service restarts — flip the master **Mode detector** toggle off and on, or restart the app, to apply it immediately.

---

## Tuning Tips

**Screen dims too quickly** — increase *Dim delay*. 60–120 s is a comfortable range for a living room panel.

**Screen wakes from across the room** — lower *Sensitivity* toward 20–30. At high sensitivity the camera reacts to light changes (curtains moving, passing cars outside a window).

**Control drawer keeps disappearing** — increase *FAB delay*. On a touch-heavy dashboard 120 s or more keeps it accessible between interactions.

**Screen stays on too long after leaving** — reduce *Screen timeout*. For energy-conscious setups, 120–300 s is a reasonable balance.
