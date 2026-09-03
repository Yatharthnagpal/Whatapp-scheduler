# 04 — Database Specification

## Database
**Filename:** `whatsapp_scheduler.db`

**Storage:** Android application-private local phone storage through Room/SQLite.

```text
Android Phone
└── App Private Storage
    └── databases/
        └── whatsapp_scheduler.db
```

Do not hardcode the physical filesystem path.

## scheduled_messages

| Column | Type | Constraints |
|---|---|---|
| id | INTEGER | PK, auto-generated |
| contact_name | TEXT | NOT NULL |
| phone_number | TEXT | NOT NULL |
| message | TEXT | NOT NULL |
| scheduled_at | INTEGER | NOT NULL, epoch ms |
| created_at | INTEGER | NOT NULL |
| updated_at | INTEGER | NOT NULL |
| status | TEXT | NOT NULL |
| execution_attempts | INTEGER | NOT NULL DEFAULT 0 |
| last_error | TEXT | NULL |

## execution_logs

| Column | Type | Constraints |
|---|---|---|
| id | INTEGER | PK, auto-generated |
| scheduled_message_id | INTEGER | FK |
| attempted_at | INTEGER | NOT NULL |
| result | TEXT | NOT NULL |
| error_code | TEXT | NULL |
| error_message | TEXT | NULL |

## Indexes
`scheduled_at`, `status`, `(status, scheduled_at)`, and `scheduled_message_id`.

## Atomic claim

```sql
UPDATE scheduled_messages
SET status = 'PROCESSING', execution_attempts = execution_attempts + 1
WHERE id = :id AND status = 'SCHEDULED';
```

Only the successful claimant may execute.

## Never store
WhatsApp authentication tokens, session files, private DB copies, WhatsApp encryption keys, network cookies, or credentials.

## Backup
Operational storage remains local. Any future export/backup must be explicit and user initiated. Review Android automatic backup rules; exclude the operational database if strict local-only semantics are required.
