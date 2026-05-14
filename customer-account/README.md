# Customer Account Reconciliation Assignment

This repository contains a Java take-home assignment for building a customer account reconciliation service.

Start with [Task.md](Task.md), then review [Banking_Identifiers_Oman.md](Banking_Identifiers_Oman.md) and the sample file at [sample-data/customer-identifiers.csv](sample-data/customer-identifiers.csv).

The included unit tests define the baseline identifier-validation contract. They are expected to fail in the starter project until the candidate implements the core behavior:

```bash
mvn test
```

The final submitted solution is expected to go beyond the starter tests by implementing CSV import, persistence, idempotency, row-level error handling, Docker Compose, documentation, and Makefile commands as described in the assignment.
