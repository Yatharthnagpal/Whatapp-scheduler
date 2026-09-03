# 10 — Implementation Plan

## Phase 0 — Inspect
Inspect repository, Gradle, SDK, package name and existing code. Read all docs before coding.

## Phase 1 — Foundation
Set up Kotlin, Compose, Material 3, Hilt, navigation, theme and core utilities.

## Phase 2 — Database
Create Room, `whatsapp_scheduler.db`, entities, DAOs, migrations and tests.

## Phase 3 — UI
Implement Home, Schedule, Contacts, Details/History and Settings.

## Phase 4 — Scheduler
Implement AlarmManager, PendingIntent, ScheduledMessageReceiver, BootReceiver, atomic claim and missed policy.

## Phase 5 — Messaging
Implement MessageSender and supported WhatsApp integration. Handle `RequiresUserAction`.

## Phase 6 — Hardening
Permissions, notifications, errors, sanitized logs, exported-component review, R8 and backup policy.

## Phase 7 — Testing
Unit, Room, scheduler, UI and device scenarios.

## Phase 8 — Release
Clean release build, manifest/permission audit, DB migration check, security check and documentation update.

## Definition of Done
Requirements are implemented or explicitly marked unsupported; tests pass; schedules survive process death/reboot; duplicate execution is prevented; DB is local and named `whatsapp_scheduler.db`; prohibited WhatsApp techniques are absent.
