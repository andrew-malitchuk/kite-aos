# Auto Reboot

Kite can reboot the device automatically on a schedule. This is useful for long-running kiosk deployments where a periodic restart clears memory, refreshes network connections, and ensures the device stays in a clean state.

Navigate to **Settings → Auto Reboot** to configure the schedule.

---

## Enabling Auto Reboot

Flip the **Auto Reboot** master toggle to **on**. The device will reboot at the next scheduled time that matches the configured hour, minute, and interval.

---

## Settings Reference

| Setting | What it does |
|---|---|
| **Enabled** | Activates or deactivates the reboot schedule. |
| **Hour** | Hour of the reboot time (0–23, 24-hour format). |
| **Minute** | Minute of the reboot time (0–59). |
| **Interval** | How often the reboot occurs. Tap the field to cycle through: **Daily**, **Weekly**, **Bi-weekly**, **Monthly**. |

---

## Choosing a Schedule

Set a time when the tablet is least likely to be in use — typically late at night or early morning.

For most home setups, **daily** at 03:00–04:00 is a good default. For production deployments with higher uptime requirements, **weekly** or **bi-weekly** keeps maintenance windows predictable while reducing disruption.

!!! warning "Unsaved WebView state"
    A reboot closes the app and all open WebView sessions. Any unsaved state in the Home Assistant dashboard (e.g. an open dialog or active media session) will be lost.

---

## Troubleshooting

**Device does not reboot at the scheduled time** — verify that Kite has the necessary system permissions granted during [onboarding](first-time-setup.md). Auto Reboot requires the device owner or device admin permission to issue a reboot command.

**Reboot fires at the wrong time** — confirm the device's system clock and timezone are set correctly (**Android Settings → General management → Date and time**).
