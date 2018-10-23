DO
$$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   pg_tables
    WHERE  schemaname = current_schema
    AND    tablename  = 'response_data') THEN

    ALTER TABLE rtu_response_data RENAME TO response_data;

END IF;

END;
$$