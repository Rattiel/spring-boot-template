CREATE SEQUENCE IF NOT EXISTS category_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE category
(
    id              BIGINT                      NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    name            VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS post_sequence START WITH 1 INCREMENT BY 50;

CREATE TABLE post
(
    id              BIGINT                      NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    title           VARCHAR(255)                NOT NULL,
    content         VARCHAR(4096)               NOT NULL,
    category_id     BIGINT                      NOT NULL,
    view_count      BIGINT                      NOT NULL,
    writer_id       VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_post PRIMARY KEY (id)
);

ALTER TABLE post
    ADD CONSTRAINT FK_POST_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE CASCADE;