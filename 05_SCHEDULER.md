# 05 — Scheduler Specification

## Technology
Use Android `AlarmManager` for user-visible scheduled execution. Do not maintain a continuously running service for every schedule.

## Creation
1. Validate message/contact/time.
2. Insert DB row as `SCHEDULED`.
3. Register OS alarm using only message ID.
4. Confirm success.

## Receiver
1. Extract ID.
2. Load Room record.
3. Verify `SCHEDULED`.
4. Atomically claim `PROCESSING`.
5. Invoke `MessageSender`.
6. Persist result and execution log.
7. Notify user when required.

## Missed schedules
Never silently send stale schedules. Recommended V1 policy: mark materially late schedules `MISSED` and let the user reschedule.

## Reboot
BootReceiver queries future `SCHEDULED` rows and restores alarms without duplicating records.

## Time
Persist epoch milliseconds. Display using device local timezone. Handle timezone and clock changes deliberately.

## Power management
Android may defer alarms depending on device state and exact-alarm capability. Communicate limitations honestly.

## Cancellation
Cancel the OS alarm and update DB to `CANCELLED`, with concurrency protection.

## Duplicate protection
Atomic `SCHEDULED → PROCESSING` transition is mandatory.
