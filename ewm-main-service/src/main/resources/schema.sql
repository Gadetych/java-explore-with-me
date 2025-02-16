CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    email VARCHAR(254) UNIQUE                 NOT NULL,
    name  VARCHAR(250)                        NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT min_length_email CHECK (LENGTH(email) >= 6),
    CONSTRAINT min_length_name_user CHECK (LENGTH(name) >= 2)
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name VARCHAR(50) UNIQUE                  NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT min_length_name_categories CHECK (LENGTH(name) >= 1)
);

CREATE TABLE IF NOT EXISTS locations
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    latitude  FLOAT                               NOT NULL,
    longitude FLOAT                               NOT NULL,
    CONSTRAINT pk_location PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    annotation         VARCHAR(2000)                       NOT NULL,
    category_id        BIGINT                              NOT NULL,
    initiator_id       BIGINT                              NOT NULL,
    description        VARCHAR(7000)                       NOT NULL,
    event_date         TIMESTAMP                           NOT NULL,
    created_on         TIMESTAMP                           NOT NULL,
    published_on TIMESTAMP,
    location_id        BIGINT                              NOT NULL,
    paid               BOOLEAN DEFAULT (false),
    participant_limit  INTEGER DEFAULT (0),
    request_moderation BOOLEAN DEFAULT (true),
    state              VARCHAR(10)                         NOT NULL,
    title              VARCHAR(120) UNIQUE                 NOT NULL,
    CONSTRAINT pk_events PRIMARY KEY (id),
    CONSTRAINT min_length_annotation_events CHECK (LENGTH(annotation) >= 20),
    CONSTRAINT min_length_description_events CHECK (LENGTH(description) >= 20),
    CONSTRAINT min_length_title_events CHECK (LENGTH(title) >= 3),
    CONSTRAINT fk_category_id_events FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_initiator_id_events FOREIGN KEY (initiator_id) REFERENCES users (id),
    CONSTRAINT fk_location_id_events FOREIGN KEY (location_id) REFERENCES locations (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT state_check CHECK (state IN ('PENDING', 'PUBLISHED', 'CANCELED'))
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    pinned BOOLEAN DEFAULT (false),
    title  VARCHAR(50) UNIQUE                  NOT NULL,
    CONSTRAINT pk_compilations PRIMARY KEY (id),
    CONSTRAINT min_length_title CHECK (LENGTH(title) >= 1)
);

CREATE TABLE IF NOT EXISTS list_events_for_compilations
(
    id             BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    compilation_id BIGINT                              NOT NULL,
    event_id       BIGINT                              NOT NULL,
    CONSTRAINT pk_list_events_for_compilations PRIMARY KEY (id),
    CONSTRAINT fk_compilation_id_list_events_for_compilations FOREIGN KEY (compilation_id) REFERENCES compilations (id),
    CONSTRAINT fk_event_id_list_events_for_compilations FOREIGN KEY (event_id) REFERENCES events (id)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    created      TIMESTAMP                           NOT NULL,
    event_id     BIGINT                              NOT NULL,
    requester_id BIGINT                              NOT NULL,
    status       varchar(10)                         NOT NULL,
    CONSTRAINT pk_requests PRIMARY KEY (id),
    CONSTRAINT fk_event_id_requests FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_requester_id_requests FOREIGN KEY (requester_id) REFERENCES users (id),
    CONSTRAINT status_check CHECK (status IN ('PENDING', 'CONFIRMED', 'REJECTED', 'CANCELED'))
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    author_id BIGINT                              NOT NULL,
    event_id  BIGINT                              NOT NULL,
    text      VARCHAR(2000)                       NOT NULL,
    created   TIMESTAMP                           NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_author_id_comments FOREIGN KEY (author_id) REFERENCES users (id),
    CONSTRAINT fk_event_id_comments FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT min_length_text CHECK (LENGTH(text) >= 5)
);