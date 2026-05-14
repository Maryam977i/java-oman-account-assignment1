# Customer Account Reconciliation Service

## Context

ProgressSoft receives customer banking identifiers from partner systems in Oman. The same customer may be represented by an IBAN, a local account number, a CIF number, or a mixture of all three.

The business needs a service that imports customer account-check requests, validates and classifies every supplied identifier, persists the result, and reports whether the valid identifiers in each request belong to the same person.

This is a take-home assignment for the Associate Java Developer - Oman position. It is intentionally scoped as a small production-style service rather than a single-method kata.

## Goal

Build a Java HTTP backend service that imports account-check requests from CSV, validates them, persists every processed request into a real database, and exposes APIs to query the import results.

You may use Spring Boot or another Java web framework, but the project must remain a Maven or Gradle project.

## Input File

The service must accept a CSV file with the following header:

```csv
request_id,customer_reference,identifier_1,identifier_2,identifier_3,submitted_at
```

Field rules:

| Field | Required | Description |
| :--- | :---: | :--- |
| `request_id` | Yes | Unique request identifier supplied by the source system. |
| `customer_reference` | Yes | External customer reference. Treat as an opaque string. |
| `identifier_1` | Yes | First banking identifier. |
| `identifier_2` | No | Optional second banking identifier. |
| `identifier_3` | No | Optional third banking identifier. |
| `submitted_at` | Yes | ISO-8601 timestamp, for example `2026-04-27T10:15:30Z`. |

A sample file is provided at `sample-data/customer-identifiers.csv`.

## Identifier Rules

Each identifier must be classified as one of:

- `IBAN`
- `ACCOUNT_NUMBER`
- `CIF_NUMBER`
- `UNKNOWN`

### General Rules

- `null`, empty, and blank values are invalid.
- Trim leading and trailing spaces before validation.
- Invalid identifiers must be stored with their validation error.
- Invalid identifiers must not participate in ownership matching.
- Processing one bad row must not prevent other rows from being processed.

### Oman IBAN

For this assignment, only Oman IBANs are supported.

A valid Oman IBAN must:

- start with `OM`;
- contain exactly 23 characters after spaces are removed;
- contain only letters and digits;
- pass standard IBAN mod-97 checksum validation.

Ownership key extraction:

```text
OM + 2 check digits + 3 bank-code digits + 16 account digits
```

The final 16 account digits contain:

```text
6 padding digits + 7 CIF digits + 3 account-type digits
```

Example:

```text
OM040270000001234567001
```

Extracted CIF:

```text
1234567
```

### Account Number

A valid account number must:

- contain digits only;
- contain exactly 14 digits;
- not be all zeros.

Ownership key extraction:

```text
4 branch-code digits + 7 CIF digits + 3 account-type digits
```

Example:

```text
03151234567001
```

Extracted CIF:

```text
1234567
```

### CIF Number

A valid CIF number must:

- contain digits only;
- contain exactly 7 digits;
- not be all zeros.

The ownership key is the CIF value itself.

## Required Behavior

### Import

Provide an HTTP endpoint that imports a CSV file.

Minimum API expectation:

```http
POST /api/account-checks/import
Content-Type: multipart/form-data
```

The response must include:

- total rows received;
- imported rows count;
- duplicate rows count;
- invalid rows count;
- per-row status or a reference that allows the caller to query details.

### Persistence

Use an actual database. Acceptable options:

- PostgreSQL
- MySQL
- MongoDB

The database must persist at least:

- import batch metadata;
- each request row;
- each identifier in the row;
- validation status and validation errors;
- extracted ownership key when available;
- final ownership result for the request.

### Idempotency

The service must not import the same `request_id` twice.

If a file contains a `request_id` that was already imported in a previous run, the row must be reported as duplicate and must not create a second request record.

If a file contains duplicate `request_id` values in the same file, only the first occurrence should be processed.

### No Rollback Across Rows

Do not roll back the whole import because one row is invalid.

Rows that can be processed must be persisted even when other rows in the same file are invalid.

### Ownership Result

For every imported request:

- If no valid identifiers exist, mark ownership result as `NO_VALID_IDENTIFIERS`.
- If one valid identifier exists, mark ownership result as `SAME_PERSON`.
- If all valid identifiers share the same extracted CIF, mark ownership result as `SAME_PERSON`.
- If any valid identifiers have different extracted CIF values, mark ownership result as `MISMATCH`.

Store enough detail to explain which identifiers matched or mismatched.

### Querying

Expose APIs or documented commands to retrieve:

- one request by `request_id`;
- all requests for a `customer_reference`;
- import batch summary;
- rows that failed validation.

## Technical Requirements

The final submission must include:

- Maven or Gradle project with full source code.
- Real database integration.
- Docker Compose for local execution.
- Sample CSV file.
- Database migration or initialization strategy.
- Proper error and exception handling.
- Proper logging.
- Unit tests for validation and ownership logic.
- Integration tests for import and persistence behavior.
- Documentation in Markdown.
- Makefile or equivalent command shortcuts.
- GitHub repository link or compressed project folder.

## Starter Project

This package includes a small starter class and public unit tests around identifier summary behavior. The tests are expected to fail until the core behavior is implemented. These tests are not the whole assignment; they are a baseline contract for the core validation rules.

You may refactor the production code freely as long as the public behavior remains compatible.

Do not modify the provided tests in your submitted solution.

Hidden tests may cover additional valid and invalid identifiers, import idempotency, partial failure behavior, and persistence.

## Suggested API Response Shape

You may choose a different response shape if it is documented, but it should carry equivalent information.

```json
{
  "batchId": "8f0d1b0e-9b10-48be-b651-c6cb97c4e74a",
  "totalRows": 5,
  "importedRows": 3,
  "duplicateRows": 1,
  "invalidRows": 1,
  "rows": [
    {
      "requestId": "REQ-001",
      "status": "IMPORTED",
      "ownershipResult": "SAME_PERSON"
    }
  ]
}
```

## Evaluation Criteria

Reviewers will consider:

- correctness against visible and hidden tests;
- clear separation between parsing, validation, persistence, and API layers;
- database constraints that support idempotency;
- transaction handling that preserves partial success;
- robust CSV parsing and row-level error reporting;
- readable Java code and naming;
- meaningful tests, including edge cases;
- practical Docker Compose and Makefile commands;
- concise documentation explaining how to run and test the service.

## Time Expectation

The assignment is designed for approximately one working day. A complete, simple solution is preferred over an over-engineered one.
