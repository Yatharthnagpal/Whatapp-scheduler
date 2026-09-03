# 03 — Production Architecture

```text
Compose UI
   ↓
ViewModel / StateFlow
   ↓
Use Cases
   ↓
Repository Interfaces
   ↓
Room / Contacts Provider / Scheduler
   ↓
Android OS
   ↓
Supported WhatsApp interaction boundary
```

## Package structure

```text
app/src/main/java/com/yatharth/whatsappscheduler/
├── core/common/
├── core/time/
├── core/security/
├── core/permissions/
├── data/local/dao/
├── data/local/entity/
├── data/contacts/
├── data/repository/
├── domain/model/
├── domain/repository/
├── domain/usecase/
├── scheduler/
├── messaging/
├── ui/navigation/
├── ui/home/
├── ui/schedule/
├── ui/contacts/
├── ui/details/
├── ui/settings/
├── ui/theme/
├── di/
└── MainActivity.kt
```

## Scheduler contract

```kotlin
interface MessageScheduler {
    fun schedule(messageId: Long, timestamp: Long)
    fun cancel(messageId: Long)
}
```

## Messaging contract

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

## Execution
AlarmManager → Receiver → Room lookup → status validation → atomic claim → MessageSender → persist result → notification/history.

Keep WhatsApp-specific code isolated in the messaging adapter.
