DO
$$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   pg_indexes
    WHERE  indexname = 'ean_ix_device'
    AND    schemaname = 'public'
    ) THEN
	CREATE INDEX ean_ix_device ON ean (device);
END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   pg_indexes
    WHERE  indexname = 'relay_status_ix_device_id'
    AND    schemaname = 'public'
    ) THEN

	CREATE INDEX relay_status_ix_device_id ON relay_status (device_id);
END IF;

END;
$$
