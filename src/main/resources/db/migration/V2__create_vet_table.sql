CREATE TABLE vet
(
    id               uuid PRIMARY KEY,
    created_at       timestamptz NOT NULL DEFAULT now(),
    last_modified_at timestamptz NOT NULL DEFAULT now(),
    first_name       text NOT NULL CHECK (char_length(first_name) BETWEEN 1 AND 255 AND btrim(first_name) = first_name),
    last_name        text NOT NULL CHECK (char_length(last_name) BETWEEN 1 AND 255 AND btrim(last_name) = last_name)
);
