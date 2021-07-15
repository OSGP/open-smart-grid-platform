DO
$$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   information_schema.columns
    WHERE  table_schema = current_schema
    AND    table_name  = 'device'
    AND    column_name = 'firmware_version') THEN

    ALTER TABLE device ADD COLUMN firmware_version character varying(100) NOT NULL DEFAULT('R01');

END IF;

END;
$$