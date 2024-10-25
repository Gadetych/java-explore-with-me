CREATE TABLE IF NOT EXISTS hits
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    app       varchar(512)                        NOT NULL,
    uri       varchar(512)                        NOT NULL,
    ip        varchar(255)                        NOT NULL,
    timestamp TIMESTAMP                           NOT NULL,
    CONSTRAINT pk_hits PRIMARY KEY (id)
);