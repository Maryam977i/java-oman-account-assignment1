# Submission Checklist

Use this checklist before sending the completed assignment.

- The project builds with `mvn test` or `gradle test`.
- Unit tests cover identifier classification, validation, and ownership matching.
- Integration tests cover CSV import, persistence, duplicate `request_id` handling, and partial success.
- Docker Compose starts the selected database.
- The application can be run locally from documented commands.
- The sample CSV can be imported successfully.
- Invalid rows are persisted or reported without stopping the whole import.
- Duplicate rows are reported without creating duplicate request records.
- Database migrations or initialization scripts are included.
- API endpoints and example requests are documented.
- Logging and error responses are implemented intentionally.
- A Makefile or equivalent command shortcut is included.
