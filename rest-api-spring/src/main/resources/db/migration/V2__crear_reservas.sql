CREATE TABLE reservas (
    id                 UUID PRIMARY KEY,
    sala_id            UUID NOT NULL REFERENCES salas (id),
    nombre_responsable VARCHAR(120) NOT NULL,
    correo_responsable VARCHAR(180) NOT NULL,
    inicio             TIMESTAMPTZ  NOT NULL,
    fin                TIMESTAMPTZ  NOT NULL,
    estado             VARCHAR(16)  NOT NULL,
    cancelada_en       TIMESTAMPTZ,
    creada_en          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT reservas_rango_valido  CHECK (fin > inicio),
    CONSTRAINT reservas_estado_valido CHECK (estado IN ('ACTIVA', 'CANCELADA')),
    CONSTRAINT reservas_no_solape EXCLUDE USING gist (
        sala_id WITH =,
        tstzrange(inicio, fin, '[)') WITH &&
    ) WHERE (estado = 'ACTIVA')
);

CREATE INDEX idx_reservas_sala_inicio ON reservas (sala_id, inicio);
CREATE INDEX idx_reservas_inicio      ON reservas (inicio);
