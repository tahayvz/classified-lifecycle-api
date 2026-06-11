# Classified Lifecycle API

Classified Lifecycle API is a Spring Boot service for creating classified listings,
managing their publication status, tracking status history, and exposing aggregate
dashboard statistics.

The project keeps the core domain rules isolated from persistence and web adapters,
so the business behavior can be tested without depending on HTTP or database details.

## Features

- Create classified listings with title, description, and category validation.
- Detect duplicate listings by category, title, and description.
- Calculate listing expiration dates by category.
- Manage listing status transitions such as pending approval, active, inactive, and duplicate.
- Block invalid status transitions and immutable duplicate updates.
- Record listing status history.
- Expose dashboard statistics for listing totals by status/category.
- Log API calls that take longer than 5 ms.
- Provide Swagger/OpenAPI documentation.
- Run with Docker or Docker Compose.

## Business Rules

- A title must start with a letter or number and must be between 10 and 50 characters.
- A description must be between 20 and 200 characters.
- A listing category must be one of `REAL_ESTATE`, `VEHICLE`, `SHOPPING`, or `OTHER`.
- Words listed in `Badwords.txt` are not allowed in title or description content.
- New `REAL_ESTATE`, `VEHICLE`, and `OTHER` listings start as `PENDING_APPROVAL`.
- New `SHOPPING` listings start as `ACTIVE`.
- Expiration dates are calculated from the creation date:
  - `REAL_ESTATE`: 4 weeks
  - `VEHICLE`: 3 weeks
  - `SHOPPING`: 8 weeks
  - `OTHER`: 8 weeks
- A listing with the same category, title, and description as an existing listing is marked as `DUPLICATE`.
- Duplicate listings cannot be updated.
- A `PENDING_APPROVAL` listing can be approved and moved to `ACTIVE`.
- An `ACTIVE` or `PENDING_APPROVAL` listing can be moved to `INACTIVE`.

## API Highlights

- `POST /classifieds`: create a listing.
- `GET /classifieds/{id}`: get listing details.
- `PATCH /classifieds/{id}/status`: update listing status.
- `GET /classifieds/{id}/history`: list status history.
- `GET /dashboard/classifieds/statistics`: get aggregate listing statistics.
- `GET /actuator/health`: health check.

## Tech Stack

- Java 17
- Spring Boot 3
- Gradle with Kotlin DSL
- Spring Web
- Spring Data JPA
- H2 in-memory database
- Bean Validation
- Lombok
- JUnit 5 and Mockito
- Springdoc OpenAPI
- Docker

## Run Locally

Build and test the project:

```bash
./gradlew clean build
```

Run the application:

```bash
./gradlew bootRun
```

The API starts on:

```text
http://localhost:8080
```

## Docker

Build the image:

```bash
docker build -t classified-lifecycle-api .
```

Run the container:

```bash
docker run -d --name classified-lifecycle-api-container -p 8080:8080 classified-lifecycle-api
```

Check the health endpoint:

```bash
curl http://localhost:8080/actuator/health
```

Stop and remove the container:

```bash
docker stop classified-lifecycle-api-container
docker rm classified-lifecycle-api-container
```

## Docker Compose

Start the service:

```bash
docker compose up --build -d
```

View logs:

```bash
docker compose logs -f classified-lifecycle-api
```

Stop the service:

```bash
docker compose down
```

## API Documentation

After the application starts, OpenAPI documentation is available at:

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Tests

Run the full test suite:

```bash
./gradlew test
```

The test suite covers domain services, application use cases, web controllers,
exception handling, and performance logging behavior.
