# Configuration

After installing Kite, the onboarding wizard walks you through granting permissions and entering your Dashboard URL. Everything can be adjusted later from the Settings screen (swipe open the control drawer → tap the settings icon).

## Quick Reference

| Area | Where to configure | Guide |
|---|---|---|
| First launch & permissions | Onboarding wizard (runs automatically) | [First-Time Setup](../user-guide/first-time-setup.md) |
| Dashboard URL & browser engine | Settings → Web kiosk | [Web Kiosk](../user-guide/web-kiosk.md) |
| Home Assistant URL discovery | Settings → Web kiosk → Scan for HA | [Home Assistant & MQTT](../user-guide/home-assistant-and-mqtt.md) |
| MQTT broker | Settings → MQTT | [Home Assistant & MQTT](../user-guide/home-assistant-and-mqtt.md) |
| Motion / presence detection | Settings → Mode detector | [Motion Detection](../user-guide/motion-detection.md) |
| Theme, language, dock position | Settings → UI & UX | [Appearance](../user-guide/appearance.md) |
| Default launcher, restart | Settings → System | [System Controls](../user-guide/system-controls.md) |
| Config export / import | Settings → Advanced | [Backup & Restore](../user-guide/backup-restore.md) |

## Settings Screen Order

The Settings screen renders sections in this fixed order, which the User Guide mirrors:

1. **Mode detector** — motion presence service
2. **MQTT** — broker connection and telemetry
3. **Web kiosk** — Dashboard URL, engine, whitelist, apps
4. **UI & UX** — theme, dock position, language
5. **System** — launcher, restart, version
6. **Advanced** — export / import

!!! info "Persistence"
    Toggle changes save immediately. Text field changes debounce by ~1 second before persisting — wait a moment after typing before navigating away.
