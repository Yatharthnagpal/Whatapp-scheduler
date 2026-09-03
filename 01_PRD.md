# 01 — Product Requirements Document

## Product
Personal Android WhatsApp Scheduler.

## Goal
Select a contact, compose a message, choose a future date/time, save locally, schedule execution, and report the result clearly.

## V1 included
- Android Contacts Provider contact selection
- Message composition
- Date/time scheduling
- Upcoming schedules
- Details, edit, cancel, delete, reschedule
- Execution history
- Local persistence
- Reboot recovery
- Duplicate-execution protection
- Permission handling
- Local notifications
- Dark-first Material 3 UI

## V1 excluded
- AI/LLM or chatbot
- Bulk messaging
- Contact scraping
- WhatsApp Web automation
- Private WhatsApp APIs
- WhatsApp database extraction
- Session/token extraction
- Network interception
- Anti-ban/evasion systems
- Cloud synchronization

## Statuses
`SCHEDULED`, `PROCESSING`, `COMPLETED`, `FAILED`, `CANCELLED`, `MISSED`.

## Main flow
Create → Persist → Schedule OS alarm → Load DB record → Atomically claim → Messaging adapter → Persist result → Notify/update history.

## Principles
Local-first, predictable, transparent, minimal permissions, no hidden automation, graceful handling of Android power-management limitations.
