CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(254) UNIQUE NOT NULL,
    name  VARCHAR(250)        NOT NULL
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS locations
(
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    lat DOUBLE PRECISION NOT NULL,
    lon DOUBLE PRECISION NOT NULL
);


CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation         VARCHAR(2000)               NOT NULL,
    category_id        BIGINT REFERENCES categories (id),
    created_on         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description        VARCHAR(7000)               NOT NULL,
    event_date         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id       BIGINT REFERENCES users (id),
    location_id        BIGINT REFERENCES locations (id),
    paid               BOOLEAN                     NOT NULL,
    participant_limit  INTEGER                         NOT NULL,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN DEFAULT TRUE,
    state              VARCHAR(200)                NOT NULL,
    title              VARCHAR(120)                NOT NULL
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    event_id     BIGINT REFERENCES events (id),
    requester_id BIGINT REFERENCES users (id),
    status       VARCHAR(200)                NOT NULL,
    CONSTRAINT event_requester_unique UNIQUE (event_id, requester_id)
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    pinned BOOLEAN      NOT NULL,
    title  VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations_events
(
    compilations_id BIGINT REFERENCES compilations (id),
    events_id       BIGINT REFERENCES events (id),
    CONSTRAINT pk_compilations_events PRIMARY KEY (compilations_id, events_id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text VARCHAR (1000) NOT NULL,
    event_id BIGINT REFERENCES events(id),
    commentator_id BIGINT REFERENCES users(id),
    created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    patched_on TIMESTAMP WITHOUT TIME ZONE,
    likes BIGINT NOT NULL
)