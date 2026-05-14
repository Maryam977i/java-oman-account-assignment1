




















# API Contract

This file describes the minimum API expected from an HTTP-based solution. Equivalent commands are acceptable only if they are documented and provide the same behavior.

## Import CSV

```http
POST /api/account-checks/import
Content-Type: multipart/form-data
```

Form field:

```text
file=<customer-identifiers.csv>
```

Expected response fields:

| Field | Description |
| :--- | :--- |
| `batchId` | Unique identifier for the import batch. |
| `totalRows` | Number of data rows received from the CSV. |
| `importedRows` | Number of rows newly imported. |
| `duplicateRows` | Number of rows skipped because `request_id` was already processed. |
| `invalidRows` | Number of rows with row-level validation errors. |
| `rows` | Per-row result details. |

## Get Request

```http
GET /api/account-checks/{requestId}
```

Returns the stored request, identifiers, validation details, and ownership result.

## Get Requests By Customer

```http
GET /api/account-checks?customerReference=CUST-1001
```

Returns all stored requests for the supplied customer reference.

## Get Batch Summary

```http
GET /api/import-batches/{batchId}
```

Returns import counts and row statuses for the batch.

## Get Validation Failures

```http
GET /api/account-checks/failures
```

Returns stored rows or identifiers that failed validation.
