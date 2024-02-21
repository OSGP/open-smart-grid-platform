-- Insert database content expected to be present executing test methods annotated with
-- @Sql(scripts = "/permits-by-throttling-config-initializes-from-database.sql")
INSERT INTO throttling_config (id, name, max_concurrency, max_new_connection_requests, max_new_connection_reset_time_in_ms) VALUES
  (1, 'config-one', 10, 11, 12),
  (2, 'config-two', 15, 16, 17),
  (3, 'config-three', 2, 3, 4),
  (4, 'config-four', 1, 2, 3);
ALTER SEQUENCE throttling_config_id_seq RESTART WITH 5;

INSERT INTO permit (id, throttling_config_id, client_id, bts_id, cell_id, request_id, created_at) VALUES
  ( 1, 1, 1,  1,  1,  1, CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  ( 2, 1, 1,  1,  1,  2, CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  ( 3, 1, 1,  1,  1,  3, CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  ( 4, 1, 1, 27,  2,  4, CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  ( 5, 1, 1, 27,  3,  5, CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  ( 6, 1, 1, 27,  3,  6, CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  ( 7, 1, 1, 27,  3,  7, CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  ( 8, 1, 1, 27,  3,  8, CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  ( 9, 1, 1, 92,  2,  9, CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  (10, 1, 1, 92,  2, 10, CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  (11, 2, 1, 93,  1, 11, CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  (12, 2, 1, -1, -1, 12, CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  (13, 2, 2,  1,  1,  1, CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  (14, 2, 2,  1,  2,  2, CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  (15, 2, 2,  2,  3,  3, CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  (16, 3, 2, -1, -1, -1, CURRENT_TIMESTAMP AT TIME ZONE 'UTC');
ALTER SEQUENCE permit_id_seq RESTART WITH 17;
