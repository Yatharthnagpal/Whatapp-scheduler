# 07 — UI/UX Specification

## Design direction
Premium utility app: dark-first, Material 3, restrained green accent, strong hierarchy, generous spacing, accessible contrast, not a WhatsApp clone.

## Screens
### Home
Upcoming schedules, quick Schedule Message CTA, empty state, status chips, history summary.

### Schedule Message
Contact selector, message field, date picker, time picker, review section, Schedule CTA.

### Contact Picker
Search contacts, name, normalized phone number, selection state, permission explanation.

### Message Details / History
Recipient, message, scheduled time, status, attempts, error/missed reason, edit, cancel, delete, reschedule.

### Settings
Notifications, exact-alarm status where applicable, battery guidance, privacy and version.

## UX rules
- Never silently alter message text.
- Show timezone-aware time.
- Reject invalid/past timestamps.
- Clearly distinguish MISSED, FAILED and REQUIRES USER ACTION.
- Confirm destructive actions.
- Support dynamic text sizing, content descriptions and accessible touch targets.
