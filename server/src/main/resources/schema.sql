CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT       NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);
CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description  VARCHAR                     NOT NULL,
    requester_id BIGINT                      NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_requests_to_users FOREIGN KEY (requester_id) REFERENCES users (id)
);
CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT        NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name        VARCHAR(255)  NOT NULL,
    description VARCHAR(1000) NOT NULL,
    available   Boolean       NOT NULL,
    user_id     BIGINT        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    request_id  BIGINT REFERENCES requests (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings
(
    id            BIGINT                      NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    item_id       BIGINT                      NOT NULL REFERENCES items (id) ON DELETE CASCADE,
    booker_id     BIGINT                      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    status        VARCHAR                     NOT NULL,
    start_booking TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_booking   TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT                      NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text      VARCHAR,
    item_id   BIGINT                      NOT NULL REFERENCES items (id) ON DELETE CASCADE,
    author_id BIGINT                      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    created   TIMESTAMP WITHOUT TIME ZONE NOT NULL
);