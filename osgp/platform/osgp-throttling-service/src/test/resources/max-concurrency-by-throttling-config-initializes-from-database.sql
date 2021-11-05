-- Insert database content expected to be present executing test methods annotated with
-- @Sql(scripts = "/max-concurrency-by-throttling-config-initializes-from-database.sql")
INSERT INTO throttling_config (id, name, max_concurrency) VALUES
  (1, 'one', 3),
  (2, 'two', 7123),
  (3, 'three', 2),
  (4, 'four', 3),
  (5, 'five', 383);
ALTER SEQUENCE throttling_config_id_seq RESTART WITH 6;
