DO
$$
BEGIN

  IF NOT EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = current_schema AND table_name = 'device' AND column_name = 'last_successful_connection_timestamp')
  THEN
    ALTER TABLE device ADD COLUMN last_successful_connection_timestamp timestamp without time zone;
    ALTER TABLE device ADD COLUMN last_failed_connection_timestamp timestamp without time zone;
    ALTER TABLE device ADD COLUMN failed_connection_count INT NOT NULL DEFAULT 0;

    COMMENT ON COLUMN device.last_successful_connection_timestamp IS 'Timestamp which indicates the last successful connection with this device.';
    COMMENT ON COLUMN device.last_failed_connection_timestamp IS 'Timestamp which indicates the last failed connection attempt for this device.';
    COMMENT ON COLUMN device.failed_connection_count IS 'Counter which is incremented when a connection attempt fails.';

  END IF;

END;
$$ 