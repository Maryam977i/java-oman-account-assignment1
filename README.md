# Customer Account Reconciliation Service

A Spring Boot service that reads customer banking identifiers from a CSV file, validates Oman IBAN / Account Number / CIF, checks if they belong to the same person, and saves results in PostgreSQL.

## How to Run
1. Start the database: `docker compose up -d postgres`
2. Run the app: `mvn spring-boot:run`
3. Service runs on: http://localhost:8081

## Test
`mvn test`

## Technologies Used
Java 17 · Spring Boot · PostgreSQL · Docker · Maven
