-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

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
        id bigserial NOT NULL,
        creation_time timestamp without time zone NOT NULL,
        modification_time timestamp without time zone NOT NULL,
        version bigint,
        device_identification VARCHAR(40) NOT NULL,
        firmware_module_type VARCHAR(100) NOT NULL,
        firmware_version VARCHAR(100) NOT NULL,
        organisation_identification VARCHAR(40) NOT NULL,
        correlation_uid VARCHAR(255),
        CONSTRAINT ssld_pending_firmware_update_pkey PRIMARY KEY (id)
    );

    CREATE INDEX ssld_pending_firmware_update_device_identification_index ON ssld_pending_firmware_update (device_identification);

    ALTER TABLE ssld_pending_firmware_update OWNER TO osp_admin;

    GRANT ALL ON TABLE ssld_pending_firmware_update TO osp_admin;

    -- Set comments for columns.
    COMMENT ON COLUMN ssld_pending_firmware_update.device_identification IS 'The identification of the SSLD';
    COMMENT ON COLUMN ssld_pending_firmware_update.firmware_module_type IS 'The firmware module type.';
    COMMENT ON COLUMN ssld_pending_firmware_update.firmware_version IS 'The firmware version to update to.';
    COMMENT ON COLUMN ssld_pending_firmware_update.organisation_identification IS 'The organisation identification of the organisation which issued the update firmware request.';
    COMMENT ON COLUMN ssld_pending_firmware_update.correlation_uid IS 'The correlation UID for the get firmware version call.';

END IF;

END;
$$
