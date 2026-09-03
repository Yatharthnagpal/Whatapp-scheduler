# WhatsApp Scheduler — Antigravity Documentation Index

## Read order
1. `01_PRD.md` — product requirements
2. `02_TRD.md` — technical requirements
3. `03_ARCHITECTURE.md` — production architecture
4. `04_DATABASE.md` — Room/SQLite schema
5. `05_SCHEDULER.md` — AlarmManager scheduling
6. `06_WHATSAPP_INTEGRATION.md` — supported WhatsApp boundary
7. `07_UI_UX.md` — UI/UX
8. `08_SECURITY_PRIVACY.md` — security/privacy
9. `09_TESTING.md` — testing
10. `10_IMPLEMENTATION_PLAN.md` — implementation phases
11. `11_ANTIGRAVITY_PROMPT.md` — master Antigravity execution prompt

## Non-negotiable
- Native Android, Kotlin, Jetpack Compose, Material 3.
- Clean Architecture + MVVM.
- Room/SQLite database named `whatsapp_scheduler.db`.
- Database stays in Android app-private local phone storage.
- No backend, AI agent, cloud DB, or unnecessary network service in V1.
- No WhatsApp reverse engineering, private APIs, DB extraction, token/session extraction, network interception, Web automation, or anti-ban evasion.
- Use only supported Android/WhatsApp mechanisms.
- If silent sending is not supported for a personal WhatsApp account, return `RequiresUserAction` instead of bypassing platform restrictions.

## Source of truth
Read the complete documentation before coding. If platform/security constraints conflict with convenience, platform/security constraints win.
