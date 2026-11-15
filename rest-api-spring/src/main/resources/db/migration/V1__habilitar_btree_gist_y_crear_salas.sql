CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE salas (
    id        UUID PRIMARY KEY,
    nombre    VARCHAR(80) NOT NULL,
    capacidad INTEGER     NOT NULL,
    ubicacion VARCHAR(120),
    creada_en TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT salas_nombre_longitud  CHECK (char_length(nombre) BETWEEN 3 AND 80),
    CONSTRAINT salas_capacidad_valida CHECK (capacidad BETWEEN 1 AND 100)
);

CREATE UNIQUE INDEX ux_salas_nombre ON salas (lower(nombre));
