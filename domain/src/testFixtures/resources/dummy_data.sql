INSERT INTO category (id, created_at, updated_at, name)
VALUES (1, NOW(), NOW(), 'Test Category 1');

INSERT INTO category (id, created_at, updated_at, name)
VALUES (2, NOW(), NOW(), 'Test Category 2');

INSERT INTO category (id, created_at, updated_at, name)
VALUES (3, NOW(), NOW(), 'Test Category 3');

INSERT INTO category (id, created_at, updated_at, name)
VALUES (4, NOW(), NOW(), 'Test Category 4');

INSERT INTO category (id, created_at, updated_at, name)
VALUES (5, NOW(), NOW(), 'Test Category 5');

INSERT INTO category (id, created_at, updated_at, name)
VALUES (6, NOW(), NOW(), 'Test Category 6');

SELECT setval('category_sequence', (SELECT MAX(id) FROM category));

INSERT INTO post (id, created_at, updated_at, title, content, category_id, view_count, writer_id)
SELECT
    i,
    NOW() - (i || ' minutes')::INTERVAL,
    NOW(),
    'Test Post [Cat 3] - Title ' || i,
    'This is a generated test content for Test Category 3. Item Number: ' || i,
    3,
    0,
    'test-user-' || i
FROM generate_series(1, 50) AS i;

INSERT INTO post (id, created_at, updated_at, title, content, category_id, view_count, writer_id)
SELECT
    i,
    NOW() - (i || ' minutes')::INTERVAL,
    NOW(),
    'Test Post [Cat 4] - Title ' || i,
    'This is a generated test content for Test Category 4. Item Number: ' || i,
    4,
    0,
    'test-user-' || i
FROM generate_series(51, 80) AS i;

INSERT INTO post (id, created_at, updated_at, title, content, category_id, view_count, writer_id)
SELECT
    i,
    NOW() - (i || ' minutes')::INTERVAL,
    NOW(),
    'Test Post [Cat 5] - Title ' || i,
    'This is a generated test content for Test Category 5. Item Number: ' || i,
    5,
    0,
    'test-user-' || i
FROM generate_series(81, 95) AS i;

INSERT INTO post (id, created_at, updated_at, title, content, category_id, view_count, writer_id)
SELECT
    i,
    NOW() - (i || ' minutes')::INTERVAL,
    NOW(),
    'Test Post [Cat 6] - Title ' || i,
    'This is a generated test content for Test Category 6. Item Number: ' || i,
    6,
    0,
    'test-user-' || i
FROM generate_series(96, 100) AS i;

SELECT setval('post_sequence', (SELECT MAX(id) FROM post));