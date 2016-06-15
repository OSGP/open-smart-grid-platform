DROP INDEX meter_response_data_correlation_uid_idx;

CREATE UNIQUE INDEX ON meter_response_data (correlation_uid)