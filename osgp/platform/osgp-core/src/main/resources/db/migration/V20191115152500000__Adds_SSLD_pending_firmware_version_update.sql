DO
$$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   pg_tables
    WHERE  schemaname = current_schema
    AND    tablename  = 'ssld_pending_firmware_update') THEN

    -- Create new table.
    CREATE TABLE ssld_pending_firmware_update (
        id bigint NOT NULL,
        creation_time timestamp without time zone NOT NULL,
        modification_time timestamp without time zone NOT NULL,
        version bigint,
        pending_firmware_update BOOL NOT NULL,
        firmware_module_type VARCHAR(100) NOT NULL,
        firmware_version VARCHAR(100) NOT NULL,
        domain VARCHAR(255) NOT NULL,
        domain_version VARCHAR(255) NOT NULL,
        organisation_identification VARCHAR(40) NOT NULL,
        correlation_uid VARCHAR(255),
        CONSTRAINT ssld_pending_firmware_update_pkey PRIMARY KEY (id)
    );

    CREATE SEQUENCE ssld_pending_firmware_update_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

    ALTER SEQUENCE ssld_pending_firmware_update_id_seq OWNED BY ssld_pending_firmware_update.id;

    ALTER TABLE ONLY ssld_pending_firmware_update ALTER COLUMN id SET DEFAULT nextval('ssld_pending_firmware_update_id_seq'::regclass);

    CREATE INDEX ssld_pending_firmware_update_id_index ON ssld_pending_firmware_update (id);

    ALTER TABLE ssld_pending_firmware_update OWNER TO osp_admin;

    GRANT ALL ON TABLE ssld_pending_firmware_update TO osp_admin;
    GRANT SELECT ON TABLE ssld_pending_firmware_update TO osgp_read_only_ws_user;

    -- Add column for FK to SSLD table.
    ALTER TABLE ssld ADD COLUMN ssld_pending_firmware_update_id bigint;

    ALTER TABLE ONLY ssld
    ADD CONSTRAINT ssld_pending_firmware_update_fk
    FOREIGN KEY (ssld_pending_firmware_update_id)
    REFERENCES ssld_pending_firmware_update(id);

    -- Set comments for columns.
    COMMENT ON COLUMN ssld_pending_firmware_update.pending_firmware_update IS 'Flag indicating if a firmware update request has been issued for the SSLD.';
    COMMENT ON COLUMN ssld_pending_firmware_update.firmware_module_type IS 'The firmware module type.';
    COMMENT ON COLUMN ssld_pending_firmware_update.firmware_version IS 'The firmware version to update to.';
    COMMENT ON COLUMN ssld_pending_firmware_update.domain IS 'The domain indicating the OSGP component which handles the firmware version retrieval.';
    COMMENT ON COLUMN ssld_pending_firmware_update.domain_version IS 'The domain version indicating the OSGP component which handles the firmware version retrieval.';
    COMMENT ON COLUMN ssld_pending_firmware_update.organisation_identification IS 'The organisation identification of the organisation which issued the update firmware request.';
    COMMENT ON COLUMN ssld_pending_firmware_update.correlation_uid IS 'The correlation UID for the get firmware version call.';

    COMMENT ON COLUMN ssld.ssld_pending_firmware_update_id IS 'Foreign key to ssld_pending_firmware_update records.';

END IF;

END;
$$
