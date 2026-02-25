CREATE TABLE products (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    description TEXT,
    price       NUMERIC(12, 2) NOT NULL,
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255)   NOT NULL DEFAULT 'system',
    updated_by  VARCHAR(255)   NOT NULL DEFAULT 'system'
);

-- Envers revision metadata
CREATE TABLE revinfo (
    rev         SERIAL PRIMARY KEY,
    revtstmp    BIGINT
);

-- Envers audit table for products
CREATE TABLE products_aud (
    id              BIGINT       NOT NULL,
    rev             INTEGER      NOT NULL REFERENCES revinfo(rev),
    revtype         SMALLINT,
    name            VARCHAR(255),
    name_mod        BOOLEAN,
    description     TEXT,
    description_mod BOOLEAN,
    price           NUMERIC(12, 2),
    price_mod       BOOLEAN,
    created_at      TIMESTAMPTZ,
    created_at_mod  BOOLEAN,
    updated_at      TIMESTAMPTZ,
    updated_at_mod  BOOLEAN,
    created_by      VARCHAR(255),
    created_by_mod  BOOLEAN,
    updated_by      VARCHAR(255),
    updated_by_mod  BOOLEAN,
    PRIMARY KEY (id, rev)
);
