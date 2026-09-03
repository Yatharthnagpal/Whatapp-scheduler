# WhatsApp Scheduler — Antigravity Docs

Complete engineering specification for the personal WhatsApp Scheduler Android app.

## Start here
Read `00_INDEX.md`, then use `11_ANTIGRAVITY_PROMPT.md` as the Antigravity execution prompt.

## Database
`whatsapp_scheduler.db` — local Room/SQLite database stored in Android application-private phone storage.

## Architecture
Kotlin + Jetpack Compose + Material 3 + Clean Architecture/MVVM + Room + AlarmManager.

## Important platform rule
WhatsApp integration must use supported Android/WhatsApp mechanisms. Reverse engineering, private APIs, session/token extraction, network interception, hidden automation and anti-ban evasion are explicitly prohibited.
