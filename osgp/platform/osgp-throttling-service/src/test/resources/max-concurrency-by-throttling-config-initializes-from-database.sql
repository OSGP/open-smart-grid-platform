-- Insert database content expected to be present executing test methods annotated with
-- @Sql(scripts = "/max-concurrency-by-throttling-config-initializes-from-database.sql")
INSERT INTO throttling_config (id, name, max_concurrency, max_new_connection_requests, max_new_connection_reset_time_in_ms) VALUES
  (1, 'one', 3, 4, 5),
  (2, 'two', 7123, 7124, 7125),
  (3, 'three', 2, 3, 4),
  (4, 'four', 3, 4, 5),
  (5, 'five', 383, 384, 385);
ALTER SEQUENCE throttling_config_id_seq RESTART WITH 6;
