DO
$$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   pg_tables
    WHERE  schemaname = current_schema
    AND    tablename  = 'device_key_processing') THEN

    CREATE TABLE device_key_processing
	(
	    device_identification character varying(40)       NOT NULL,
	    start_time            timestamp without time zone,
	    CONSTRAINT device_identification_pkey PRIMARY KEY (device_identification)
	);

    ALTER TABLE device_key_processing OWNER TO osp_admin;

END IF;

END;
$$
