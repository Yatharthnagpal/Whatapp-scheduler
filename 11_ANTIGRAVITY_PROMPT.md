# 11 — MASTER ANTIGRAVITY EXECUTION PROMPT

You are the lead Android engineer implementing this repository as a production-ready personal WhatsApp Scheduler.

## READ FIRST
Read `00_INDEX.md` and every numbered specification file `01` through `10` before modifying code. Then inspect the repository, Gradle configuration, package name and Android SDK configuration.

## PRODUCT
Build an Android app that lets a user select a WhatsApp contact, type a message, choose a future date/time, save locally, schedule an OS alarm, and execute through a supported WhatsApp mechanism when possible.

No AI agent, chatbot or V1 cloud backend.

## REQUIRED STACK
Kotlin, Jetpack Compose, Material 3, Clean Architecture, MVVM, Coroutines/Flow, Hilt, Room/SQLite, AlarmManager, BroadcastReceiver and Android Contacts Provider.

## DATABASE — NON-NEGOTIABLE
Database filename: `whatsapp_scheduler.db`.

Store it in Android application-private internal storage through Room. Do not hardcode its physical path and do not use shared external storage.

## ARCHITECTURE
```text
UI → ViewModel → Use Case → Repository → Room
                           ↓
                     Scheduler Adapter
                           ↓
                     Messaging Adapter
```

Keep WhatsApp-specific code isolated.

## ENTITIES
`ScheduledMessageEntity`: id, contactName, phoneNumber, message, scheduledAt, createdAt, updatedAt, status, executionAttempts, lastError.

`ExecutionLogEntity`: id, scheduledMessageId, attemptedAt, result, errorCode, errorMessage.

Statuses: SCHEDULED, PROCESSING, COMPLETED, FAILED, CANCELLED, MISSED.

## SCHEDULER
Persist before registering AlarmManager. PendingIntent contains only the stable message ID.

Receiver: load record → verify SCHEDULED → atomically claim PROCESSING → invoke MessageSender → persist result → write execution log → notify/update UI.

Implement reboot restoration and duplicate protection.

## MESSAGING
Implement:

```kotlin
interface MessageSender {
    suspend fun send(recipient: Contact, message: String): SendResult
}

sealed class SendResult {
    data object Success : SendResult()
    data class RequiresUserAction(val reason: String) : SendResult()
    data class Failure(val reason: String) : SendResult()
}
```

## HARD PLATFORM/SECURITY RULE
DO NOT reverse engineer WhatsApp.

DO NOT use private APIs, private databases, token/session extraction, network interception, credential scraping, WhatsApp Web automation, hidden accessibility automation, anti-ban evasion or fingerprint manipulation.

Use only supported Android/WhatsApp mechanisms.

If arbitrary silent sending from a personal WhatsApp account is unsupported, do not invent a workaround. Return `RequiresUserAction` and build the rest of the product around that limitation.

## UI
Create Home, Schedule Message, Contact Picker, Message Details/History and Settings. Use a premium dark-first Material 3 system with restrained green accents. Do not clone WhatsApp.

## PERMISSIONS
Request only required permissions such as READ_CONTACTS, POST_NOTIFICATIONS and RECEIVE_BOOT_COMPLETED. Evaluate exact-alarm requirements against the target Android version/policy.

## SECURITY
No secrets in source control. No WhatsApp credentials/session data. No message bodies or phone numbers in production logs. Validate receiver extras. Minimize exported components. Enable release hardening/R8. Audit dependencies. Deliberately configure backup behavior.

## IMPLEMENTATION PROCESS
1. Inspect repository.
2. Establish foundation.
3. Implement Room/database.
4. Implement UI.
5. Implement scheduler.
6. Implement messaging boundary.
7. Harden security/privacy.
8. Add tests.
9. Validate release.

After every major phase, compile and run relevant tests. Fix failures before proceeding. Do not make speculative rewrites.

## FINAL VALIDATION
Before completion: run tests; build release; inspect manifest/permissions; verify `whatsapp_scheduler.db`; verify reboot recovery; verify atomic duplicate protection; verify cancellation; verify missed schedules; verify sanitized logging; verify no prohibited WhatsApp integration.

If a requirement conflicts with Android/WhatsApp limitations, document the limitation and implement the safest supported fallback.
