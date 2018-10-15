DO
$$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   pg_tables
    WHERE  schemaname = current_schema
    AND    tablename  = 'device_firmware_module') THEN

    CREATE TABLE device_firmware_module (
        device_id BIGINT NOT NULL REFERENCES device(id),
        firmware_module_id BIGINT NOT NULL REFERENCES firmware_module(id),
        module_version VARCHAR(100) NOT NULL,
        CONSTRAINT device_firmware_module_pkey PRIMARY KEY (device_id, firmware_module_id)
    );
    -- Index on device_id should be covered by the PK on (device_id, firmware_module_id).
    CREATE INDEX device_firmware_module_ix_firmware_module_id ON device_firmware_module (firmware_module_id);

    ALTER TABLE device_firmware_module OWNER TO osp_admin;

    GRANT ALL ON TABLE device_firmware_module TO osp_admin;
    GRANT SELECT ON TABLE device_firmware_module TO osgp_read_only_ws_user;

END IF;

END;
$$
