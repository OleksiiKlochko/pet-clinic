CREATE TABLE pet
(
    id               uuid PRIMARY KEY,
    created_at       timestamptz NOT NULL DEFAULT now(),
    last_modified_at timestamptz NOT NULL DEFAULT now(),
    name             text NOT NULL CHECK (char_length(name) BETWEEN 1 AND 255 AND btrim(name) = name)
);
