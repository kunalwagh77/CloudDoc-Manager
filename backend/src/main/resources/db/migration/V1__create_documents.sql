CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(160) NOT NULL,
    category VARCHAR(80) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    content_type VARCHAR(160) NOT NULL,
    cloud_url VARCHAR(2048) NOT NULL,
    provider_key VARCHAR(512) NOT NULL,
    storage_provider VARCHAR(32) NOT NULL,
    uploaded_at TIMESTAMPTZ NOT NULL,
    owner_id VARCHAR(160) NOT NULL
);

CREATE INDEX idx_documents_owner_uploaded_at ON documents (owner_id, uploaded_at DESC);
CREATE INDEX idx_documents_owner_category ON documents (owner_id, category);
CREATE INDEX idx_documents_owner_title ON documents (owner_id, title);
CREATE INDEX idx_documents_owner_file_name ON documents (owner_id, file_name);
