CREATE TABLE pet
(
    id   uuid PRIMARY KEY,
    name text NOT NULL CHECK (char_length(name) BETWEEN 1 AND 255 AND btrim(name) = name)
);
