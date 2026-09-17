<!--
Sync Impact Report
- Version change: unversioned scaffold -> 1.0.0
- Modified principles: scaffold placeholders -> Architecture, Cloud Storage,
  Credential Management, and Modular API Contracts
- Added sections: Technical Constraints, Development Workflow
- Removed sections: none
- Follow-up TODOs: Confirm the original ratification date.
-->

# CloudDoc Manager Constitution

## Core Principles

### I. Spring Boot REST Architecture
The backend MUST be implemented as a Spring Boot 3 REST API. Persistent application
data MUST use PostgreSQL through a clearly defined persistence boundary. New features
MUST expose behavior through versionable REST resources rather than framework-specific
UI or direct database access.

### II. Cloud File Storage
File content MUST be stored and retrieved through a cloud storage abstraction backed by
the AWS S3 or Cloudinary SDK. Application services MUST depend on that abstraction rather
than provider-specific calls, so storage providers can be configured or replaced without
changing domain behavior.

### III. Credential Safety
Credentials, tokens, connection strings, and other secrets MUST NOT be hardcoded in
source code, tests, configuration committed to version control, or documentation.
Runtime configuration MUST be supplied through environment variables referenced by
`application.yml`; secret values MUST remain outside the repository.

### IV. Modular API Contracts
The REST API MUST use modular resource and service boundaries with consistent request,
response, validation, and error contracts. Every public endpoint MUST be documented with
OpenAPI/Swagger, and contract changes MUST update the generated documentation and
relevant tests in the same change.

## Technical Constraints

Spring Boot MUST remain on the 3.x line. PostgreSQL is the system of record for
structured application data. AWS S3 and Cloudinary integrations MUST be isolated behind
configuration and storage interfaces. Environment-specific values MUST be externalized
through `application.yml` and environment variables.

## Development Workflow

Every change MUST include appropriate automated tests for its persistence, storage,
security, or API contract behavior. Reviews MUST verify that no secrets are introduced,
REST boundaries remain modular, and OpenAPI/Swagger documentation matches the endpoint
implementation.

## Governance

This constitution supersedes conflicting project practices. Amendments MUST document the
reason for change, update the version and last-amended date, and describe any migration
impact. Versioning follows semantic versioning: MAJOR for incompatible governance changes,
MINOR for new or materially expanded principles, and PATCH for clarifications.

Every pull request and implementation review MUST check compliance with the principles
above. Any exception MUST be explicitly justified, approved by the project owners, and
recorded with the affected change.

**Version**: 1.0.0 | **Ratified**: TODO(RATIFICATION_DATE): confirm original adoption date | **Last Amended**: 2026-09-16
