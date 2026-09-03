# 06 — WhatsApp Integration Boundary

## Rule
Use only supported Android/WhatsApp interaction mechanisms.

## Prohibited
Do not implement WhatsApp reverse engineering, private/internal APIs, WhatsApp DB extraction, session/token extraction, network interception, credential scraping, WhatsApp Web automation, hidden accessibility automation, anti-ban/evasion logic, or fingerprint manipulation.

## Adapter

```kotlin
interface MessageSender {
    suspend fun send(recipient: Contact, message: String): SendResult
}
```

```kotlin
sealed class SendResult {
    data object Success : SendResult()
    data class RequiresUserAction(val reason: String) : SendResult()
    data class Failure(val reason: String) : SendResult()
}
```

## Platform limitation
A personal WhatsApp account may not provide a supported mechanism for arbitrary silent third-party sending. Do not promise guaranteed silent delivery. If Android/WhatsApp requires interaction, return `RequiresUserAction` and make the UI explicit.

## Contact handling
Use Android Contacts Provider and store a normalized phone-number snapshot with the schedule. Never scrape WhatsApp's private contact database.

## Future
An officially documented messaging API can be added as a separate adapter only after eligibility and compliance review.
