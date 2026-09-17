# CloudDoc Manager

CloudDoc Manager stores document metadata in PostgreSQL and document content in a configured Cloudinary or AWS S3 provider.

## Backend

Requirements: Java 17+ and Maven 3.9+.

1. Copy `backend/.env.example` to a local environment file or export its variables.
2. Start PostgreSQL and create the `clouddoc_db` database.
3. Run `mvn spring-boot:run` from `backend/`.
4. Open Swagger UI at `http://localhost:8080/swagger-ui.html`.

Never commit credentials. The backend reads database and cloud credentials from environment variables referenced by `application.yml`.

## Frontend

Serve `frontend/` with any static HTTP server, for example `python -m http.server 5500 --directory frontend`, then open `http://localhost:5500`.
