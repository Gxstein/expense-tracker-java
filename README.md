# Expense Tracker

A personal expense tracking API built with Java 21 and Spring Boot. Users can create an account, manage their own expenses, filter records by month, and view monthly totals. Each expense is scoped to its authenticated owner.

[Leia em português](docs/pt-br/README.md)

## Features

- Account registration and login with BCrypt password hashing.
- Stateless JWT access tokens.
- Create, list, retrieve, update, and delete expenses.
- Monthly filtering and database-backed monthly summaries.
- Request validation and consistent HTTP error responses.
- PostgreSQL persistence with versioned Flyway migrations.
- Interactive OpenAPI documentation.
- Docker Compose setup for the API and database.

## Technology

Java 21, Spring Boot 3.5, Spring Web, Spring Data JPA, Spring Security, OAuth2 Resource Server, PostgreSQL, Flyway, Bean Validation, OpenAPI, Docker Compose.

## Run locally

Requirements: Java 21+, Docker Desktop with Compose, and Git.

1. Clone the repository and enter its directory.
2. Copy .env.example to .env.
3. In .env, set unique values for POSTGRES_PASSWORD and JWT_SECRET. Use at least 32 random characters for the JWT secret.
4. Start the application:

~~~powershell
docker compose up --build
~~~

The API is available at http://localhost:8080; Swagger UI is at http://localhost:8080/swagger-ui.html. Flyway applies database migrations automatically at startup. Stop the containers with Ctrl+C or docker compose down; the database volume is preserved.

For a local Maven run, start only PostgreSQL with docker compose up -d db, then run ./mvnw spring-boot:run (Windows: mvnw.cmd spring-boot:run). Configure DB_URL, DB_USERNAME, DB_PASSWORD, and JWT_SECRET in the shell first.

## Automated tests

Run the unit test suite with `./mvnw test` (Windows: `mvnw.cmd test`). The GitHub Actions workflow runs the same Maven verification on pushes to `main` and pull requests.

The JDBC console version uses the same PostgreSQL database and its own expense_v2 table. Start the full Compose stack once so Flyway can create the table, then run:

~~~powershell
mvnw.cmd -Pconsole-v2 exec:java
~~~

When running the console directly on Windows, set DB_PASSWORD in the terminal to the value from .env first.

## API overview

All expense routes require Authorization: Bearer <accessToken>.

| Method | Route | Description |
| --- | --- | --- |
| POST | /api/v1/auth/register | Create an account and receive a token |
| POST | /api/v1/auth/login | Log in and receive a token |
| POST | /api/v1/expenses | Create an expense |
| GET | /api/v1/expenses | List the current user's expenses |
| GET | /api/v1/expenses?month=10&year=2026 | Filter by month |
| GET | /api/v1/expenses/monthly-summary?month=10&year=2026 | Get count and total |
| GET | /api/v1/expenses/{id} | Retrieve one of the current user's expenses |
| PUT | /api/v1/expenses/{id} | Update one of the current user's expenses |
| DELETE | /api/v1/expenses/{id} | Delete one of the current user's expenses |

### Example: create an account

~~~http
POST /api/v1/auth/register
Content-Type: application/json

{
  "name": "Marcio",
  "email": "marcio@example.com",
  "password": "change-this-password"
}
~~~

The response contains accessToken, tokenType, and expiresInSeconds. Use the token as a Bearer token on expense endpoints.

### Example: create an expense

~~~http
POST /api/v1/expenses
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "description": "Groceries",
  "amount": 125.90,
  "date": "2026-10-29",
  "category": "FOOD"
}
~~~

Allowed categories: FOOD, TRANSPORT, HOUSING, LEISURE, HEALTH, OTHER.

## Architecture

~~~mermaid
flowchart LR
    Client -->|register / login| AuthController
    Client -->|Bearer JWT| Security
    Security --> ExpenseController
    AuthController --> AuthService
    AuthService --> UserRepository
    AuthService --> JwtService
    ExpenseController --> ExpenseService
    ExpenseService --> ExpenseRepository
    UserRepository --> PostgreSQL
    ExpenseRepository --> PostgreSQL
    Expense --> User
~~~

The JWT contains the authenticated user's ID. Expense queries always include that owner ID, so one account cannot read or change another account's records.

## Project history

- src/v0: procedural console exercise.
- src/v1: object-oriented console application with collections.
- src/v2: JDBC console application with DAO, PostgreSQL persistence, and monthly queries.
- src/main: Spring Boot REST API with JPA, JWT authentication, and per-user data isolation.

The Mermaid class diagrams in docs/diagrams/src describe the console, JDBC, and REST API versions.

## Security notes

- Passwords are stored as BCrypt hashes, never as plain text.
- The default JWT secret in application.properties is for local development only.
- Set a unique JWT_SECRET before running outside a local machine; do not commit .env.
- The API uses stateless Bearer authentication and validates token signature, issuer, and expiration.

## Author

[Gxstein](https://github.com/Gxstein)
