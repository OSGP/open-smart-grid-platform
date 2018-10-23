DO $$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'device_log_item_device_identification_modification_time_idx'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX device_log_item_device_identification_modification_time_idx ON device_log_item(device_identification, modification_time);
END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'device_log_item_modification_time_idx'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX device_log_item_modification_time_idx ON device_log_item(modification_time);
END IF;

END;
$$
