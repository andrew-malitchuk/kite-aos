# System Controls

System-level actions are grouped at the bottom of **Settings → System**.

---

## Set as Default Launcher

Tap **Set as default launcher** to open Android's launcher chooser. Select *Kite* and choose **Always** to make Kite the home screen.

Once set, pressing the Home button from any app returns to the Kite dashboard instead of the regular Android launcher — the core of the kiosk lockdown.

!!! tip "Undoing this"
    To remove Kite as the default launcher, go to your Android **Settings → Apps → Default apps → Home app** and select another launcher.

---

## Restart Application

Tap **Restart application** to perform a hard restart of the Kite process. This is useful after changing settings that don't take effect until reboot, or to recover from an unresponsive state.

!!! warning
    The app closes and relaunches immediately. Any in-progress WebView session is lost.

---

## Version

The version footer at the bottom of the System section shows the currently installed version and build number in the format `vX.Y.Z (build)`. Use this when reporting issues.

---

## About Screen

Tap the overflow icon (⋮) in the top-right corner of the Settings screen to navigate to the About screen, which contains project information and links.
