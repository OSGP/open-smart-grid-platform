ALTER TABLE throttling_config ADD COLUMN max_open_connections integer NOT NULL;
ALTER TABLE throttling_config ADD COLUMN max_new_connection_requests integer NOT NULL;
ALTER TABLE throttling_config ADD COLUMN max_new_connection_reset_time_in_ms bigint NOT NULL;

ALTER TABLE throttling_config ADD CONSTRAINT throttling_config_max_open_connections_non_negative CHECK (max_open_connections > -1);
ALTER TABLE throttling_config ADD CONSTRAINT throttling_config_max_new_connection_requests_non_negative CHECK (max_new_connection_requests > -1);
ALTER TABLE throttling_config ADD CONSTRAINT throttling_config_max_new_connection_reset_time_in_ms_non_negative CHECK (max_new_connection_reset_time_in_ms > -1);
