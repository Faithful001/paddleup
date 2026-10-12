CREATE TABLE categories (
    id          UUID PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ
);
