# Spring Boot Project
This repository is a **base template** for a Spring Boot application, designed to demonstrate common real-world practices, including:

- **Layered architecture** (domain, service, controller, etc.)
- **Correlation ID** for logging
- **API versioning** (via custom `@Version` annotation)
- **Global exception handling** + consistent `BaseResponse` format
- **Bean Validation** for request DTOs
- **Logging** with environment-specific configuration using `logback-spring.xml`
- **GitHub Actions CI** pipeline (run unit, integration, E2E tests)
- **Git Hook** (`pre-commit`) to run code style checks (`mvn checkstyle:check`) and spotbugs (`mvn spotbugs:check`) before committing
- **Dockerfile** for building a Docker image (with multi-stage build)
- **Example unit, integration, and E2E tests** (using JUnit 5, Mockito, RestAssured)
- **Swagger UI** for API documentation
- **Flyway** for database migrations
- **i18n** for multiple languages

