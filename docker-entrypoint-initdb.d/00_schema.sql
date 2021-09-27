CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    username TEXT        NOT NULL UNIQUE,
    password TEXT        NOT NULL,
    active   BOOLEAN     NOT NULL DEFAULT TRUE,
    created  timestamptz NOT NULL DEFAULT current_timestamp
);
CREATE TABLE tokens
(
    token         TEXT PRIMARY KEY,
    "userId"      BIGINT      NOT NULL REFERENCES users,
    created       timestamptz NOT NULL DEFAULT current_timestamp,
    "endLifeTime" timestamptz NOT NULL DEFAULT current_timestamp + interval '600 second'
);
CREATE TABLE roles
(
    "userId" BIGINT      NOT NULL REFERENCES users,
    role     BIGINT      NOT NULL DEFAULT 2,
    created  timestamptz NOT NULL DEFAULT current_timestamp
);
CREATE TABLE recovery_password
(
    username TEXT        NOT NULL UNIQUE,
    code     BIGINT      NOT NULL,
    created  timestamptz NOT NULL DEFAULT current_timestamp
);
CREATE TABLE cards
(
    id        BIGSERIAL PRIMARY KEY,
    "ownerId" BIGINT  NOT NULL REFERENCES users,
    number    TEXT    NOT NULL,
    balance   BIGINT  NOT NULL DEFAULT 0,
    active    BOOLEAN NOT NULL DEFAULT TRUE
);
