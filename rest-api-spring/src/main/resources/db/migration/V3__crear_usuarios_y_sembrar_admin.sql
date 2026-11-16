CREATE TABLE usuarios (
    id         UUID PRIMARY KEY,
    usuario    VARCHAR(60)  NOT NULL,
    clave_hash VARCHAR(100) NOT NULL,
    roles      VARCHAR(120) NOT NULL,
    creado_en  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX ux_usuarios_usuario ON usuarios (lower(usuario));

INSERT INTO usuarios (id, usuario, clave_hash, roles)
VALUES ('9f0e6a7b-3c2d-4e5f-8a9b-0c1d2e3f4a5b', 'admin',
        '$2a$10$lTQCp1oC/e/PCE0sUZqN8.8lWZaOmWMp1aRADkKqMA.mAiYwqZyHO', 'ADMIN');
