CREATE TABLE IF NOT EXISTS "user" (
    id BIGSERIAL NOT NULL,
    email TEXT NOT NULL,
    preferred_name TEXT NOT NULL,
    password TEXT NOT NULL,
    salt TEXT NOT NULL,
    creation_date_time TIMESTAMP NOT NULL DEFAULT(now() at time zone 'utc'),
    created_by_user_id INTEGER,
    modification_date_time TIMESTAMP NOT NULL DEFAULT(now() at time zone 'utc'),
    modified_by_user_id INTEGER,
    CONSTRAINT pk_user PRIMARY KEY(id),
    CONSTRAINT uq_user_email UNIQUE(email),
    CONSTRAINT fk_user_created_by_user_id FOREIGN KEY(created_by_user_id) REFERENCES "user",
    CONSTRAINT fk_user_modified_by_user_id FOREIGN KEY(modified_by_user_id) REFERENCES "user"
);

INSERT INTO "user" (
    id, email, preferred_name, password, salt, created_by_user_id, modified_by_user_id
) VALUES(
    -1, '-', 'system', '-', '-', -1, -1
) ON CONFLICT ON CONSTRAINT pk_user DO NOTHING;
