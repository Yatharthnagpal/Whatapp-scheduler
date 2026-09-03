# 02 — Technical Requirements Document

## Stack
- Android + Kotlin
- Jetpack Compose + Material 3
- Clean Architecture + MVVM
- Coroutines + Flow/StateFlow
- Hilt
- Room/SQLite
- AlarmManager + BroadcastReceiver
- Android Contacts Provider
- Android Keystore where genuinely required

## Storage
Database filename: `whatsapp_scheduler.db`.

Store it in Android application-private internal storage through Room. Never hardcode the physical path or use shared external storage for the operational DB.

## Components
- MainActivity
- ScheduledMessageReceiver
- BootReceiver
- Notification channel management
- Contacts integration
- AlarmManager scheduler

## Reliability
- Persist before scheduling.
- Atomic `SCHEDULED → PROCESSING` claim.
- PendingIntent contains only message ID.
- Restore future alarms after reboot.
- Never silently execute stale schedules.
- Handle timezone/clock changes deliberately.

## Permissions
Request only permissions required by implemented features, such as `READ_CONTACTS`, `POST_NOTIFICATIONS`, and `RECEIVE_BOOT_COMPLETED`. Evaluate exact-alarm requirements for the target Android version and policy.

## Platform constraint
Personal WhatsApp accounts may not expose a supported API for arbitrary silent third-party sending. Never bypass this with reverse engineering or private interfaces. Expose `RequiresUserAction` when necessary.
