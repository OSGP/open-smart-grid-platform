DO $$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'dlms_device_id_idx'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX dlms_device_id_idx ON security_key (dlms_device_id);
END IF;

END;
$$
