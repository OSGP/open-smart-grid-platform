ALTER TABLE throttling_config ADD COLUMN max_new_connections integer NOT NULL;
ALTER TABLE throttling_config ADD COLUMN max_new_connections_reset_time_in_ms bigint NOT NULL;
ALTER TABLE throttling_config ADD COLUMN max_new_connections_wait_time_in_ms bigint NOT NULL;

ALTER TABLE throttling_config DROP CONSTRAINT throttling_config_max_concurrency_non_negative;

ALTER TABLE throttling_config ADD CONSTRAINT throttling_config_max_concurrency_check CHECK (max_concurrency >= -1);
ALTER TABLE throttling_config ADD CONSTRAINT throttling_config_max_new_connections_check CHECK (max_new_connections >= -1);
ALTER TABLE throttling_config ADD CONSTRAINT throttling_config_max_new_connections_reset_time_in_ms_non_negative CHECK (max_new_connections_reset_time_in_ms > -1);
ALTER TABLE throttling_config ADD CONSTRAINT throttling_config_max_new_connections_wait_time_in_ms_non_negative CHECK (max_new_connections_wait_time_in_ms > -1);
