# 08 — Security & Privacy

## Privacy model
Local-first: schedules remain on-device; no backend or analytics SDK is required; message contents are not uploaded.

## Data minimization
Store only contact display name, phone number, message, schedule metadata and status/error metadata. Do not collect location, call logs, SMS, microphone recordings, camera data, WhatsApp authentication data or unrelated device identifiers.

## Database
`whatsapp_scheduler.db` lives in Android app-private storage through Room.

## Logging
Never log message bodies, phone numbers, credentials or authentication/session material in production.

## Android hardening
- Minimize exported components.
- Validate receiver extras.
- Protect internal components.
- Enable R8/minification for release as appropriate.
- Audit dependencies.
- Review backup configuration.
- Do not rely on obfuscation as security.

## Threats
Handle duplicate alarms, stale alarms, process death, reboot, accidental exports and malicious invocation of exported components. The app does not claim protection from a fully compromised/rooted device.

## Principle
Security comes from data minimization, permissions, isolation and supported platform APIs—not from hiding automation.
