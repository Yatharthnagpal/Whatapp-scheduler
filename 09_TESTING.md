# 09 — Testing Strategy

## Unit
Test validation, time conversion, status transitions, repository/use cases, atomic claim and missed-schedule policy.

## Room
Test CRUD, status queries, indexes, atomic claim, execution logs and migrations.

## Scheduler
Test scheduling, cancellation, duplicate alarms, reboot restoration, stale alarms, past timestamps and timezone/clock changes.

## UI
Test form validation, contact selection, editing, date/time, status rendering, cancellation confirmation and accessibility.

## Device scenarios
Test app open/background, process killed, locked device, battery saver, reboot, notification/contacts permission denied, WhatsApp unavailable, WhatsApp requires user action, changed/deleted contact, past schedule, timezone change and repeated alarm callback.

## Security
Review exported components, intent validation, release build, secret scanning, logging, permissions and dependencies.

## Acceptance
No duplicate execution; reboot recovery works; cancelled messages do not execute; no sensitive logs; no prohibited WhatsApp technique; release build passes.
