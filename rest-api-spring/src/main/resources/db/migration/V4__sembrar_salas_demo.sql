INSERT INTO salas (id, nombre, capacidad, ubicacion)
VALUES ('11111111-1111-4111-8111-111111111111', 'Andes', 8, 'Piso 1'),
       ('22222222-2222-4222-8222-222222222222', 'Illimani', 12, 'Piso 2'),
       ('33333333-3333-4333-8333-333333333333', 'Titicaca', 4, 'Piso 3')
ON CONFLICT DO NOTHING;
