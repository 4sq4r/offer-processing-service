BEGIN;

INSERT INTO cities (id, name, created_at, updated_at)
VALUES (
    'almaty',
    'Almaty',
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;


INSERT INTO merchants (id, name, created_at, updated_at)
VALUES (
    'technostore',
    'TechnoStore',
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;;

INSERT INTO warehouses (id, name, merchant_id, city_id, created_at, updated_at)
VALUES
    ('warehouse1', 'Warehouse 1', 'technostore', 'almaty', NOW(), NOW()),
    ('warehouse2', 'Warehouse 2', 'technostore', 'almaty', NOW(), NOW()),
    ('warehouse3', 'Warehouse 3', 'technostore', 'almaty', NOW(), NOW())
    ON CONFLICT (id) DO NOTHING;

COMMIT;
