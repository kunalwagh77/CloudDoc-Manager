# CloudDoc Manager

CloudDoc Manager stores document metadata in PostgreSQL and document content in a configured Cloudinary or AWS S3 provider.

## Backend

Requirements: Java 17+ and Maven 3.9+.

1. Copy `backend/.env.example` to a local environment file or export its variables.
2. Start PostgreSQL and create the `clouddoc_db` database.
3. From the repository root, run `./start.ps1 -DatabasePassword "YOUR_POSTGRES_PASSWORD"`.
4. Open the frontend at `http://localhost:5500`.
5. The backend runs at `http://localhost:8081`; Swagger UI is at `http://localhost:8081/swagger-ui.html`.

Never commit credentials. The backend reads database and cloud credentials from environment variables referenced by `application.yml`.

## Frontend

The `start.ps1` script starts the frontend and backend together, keeps the API port and CORS origin aligned, and verifies PostgreSQL/database availability before launch.
