DO $$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'device_authorization_device_idx'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX device_authorization_device_idx ON device_authorization (device);
END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'device_authorization_organisation_idx'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX device_authorization_organisation_idx ON device_authorization (organisation);
END IF;

END;
$$
