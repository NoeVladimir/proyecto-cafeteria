# Cafeteria Project Guide

## Stack

- Backend: Java 25, Spring Boot 3.5, Maven, Spring Data JPA.
- Database: PostgreSQL 16, available as the `db` Compose service.
- Frontend: Vue 3 and Vite on Node.js 22.
- Development environment: VS Code Dev Container using Docker Compose.

## Development

Open the project in VS Code and choose **Reopen in Container**. The container installs backend and frontend dependencies automatically.

- Start the API: `mvn -f backend/pom.xml spring-boot:run`
- Start the frontend: `npm --prefix frontend run dev`
- Build the API: `mvn -f backend/pom.xml verify`
- Build the frontend: `npm --prefix frontend run build`

The API is available on port 8080, the Vue development server on port 5173, and PostgreSQL on port 5432. Database credentials are provided by `docker-compose.yml` for local development only (`dev` / `dev`, database `appdb`).

## Project conventions

- Keep domain logic in the backend and call it from Vue through `/api` endpoints.
- Add automated tests with each backend feature.
- Never commit credentials or production connection strings.