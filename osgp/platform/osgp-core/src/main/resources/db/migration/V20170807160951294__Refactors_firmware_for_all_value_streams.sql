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
    AND    tablename  = 'firmware_file') THEN

    -- Since we are dealing with firmware files, rename the firmware table to firmware_file.
    ALTER TABLE firmware RENAME TO firmware_file;

    -- Rename the sequence used by the firmware_file id column.
    ALTER SEQUENCE device_model_firmware_id_seq RENAME TO firmware_file_id_seq;

    -- Since a firmware file can be used by more than one device model,
    -- we need a many to many relationship between device_model and firmware_file.
    CREATE TABLE device_model_firmware_file (
        device_model_id BIGINT NOT NULL REFERENCES device_model(id),
        firmware_file_id BIGINT NOT NULL REFERENCES firmware_file(id),
        CONSTRAINT device_model_firmware_file_pkey PRIMARY KEY (device_model_id, firmware_file_id)
    );
    -- Index on device_model_id should be covered by the PK on (device_model_id, firmware_file_id).
    CREATE INDEX device_model_firmware_file_ix_firmware_file_id ON device_model_firmware_file (firmware_file_id);

    ALTER TABLE device_model_firmware_file OWNER TO osp_admin;

    GRANT ALL ON TABLE device_model_firmware_file TO osp_admin;
    GRANT SELECT ON TABLE device_model_firmware_file TO osgp_read_only_ws_user;

    -- Make sure the old firmware to device_model relations are migrated to device_model_firmware_file.
    INSERT INTO device_model_firmware_file (device_model_id, firmware_file_id)
        SELECT device_model_id, id FROM firmware_file WHERE device_model_id IS NOT NULL;

    ALTER TABLE firmware_file DROP COLUMN device_model_id CASCADE;

    CREATE TABLE firmware_module (
        id BIGSERIAL PRIMARY KEY,
        description VARCHAR(255) NOT NULL,
        CONSTRAINT firmware_module_description_key UNIQUE (description)
    );

    ALTER TABLE firmware_module OWNER TO osp_admin;

    GRANT ALL ON TABLE firmware_module TO osp_admin;
    GRANT SELECT ON TABLE firmware_module TO osgp_read_only_ws_user;

    -- Add firmware modules based on the version columns of the old firmware table.
    -- This way existing data can be migrated to firmware_file_firmware_module versions later-on in this script.
    INSERT INTO firmware_module (description) VALUES
        ('communication_module_active_firmware'),
        ('functional'),
        ('module_active_firmware'),
        ('m_bus'),
        ('security'),
        ('active_firmware');

    CREATE TABLE firmware_file_firmware_module (
        firmware_file_id BIGINT NOT NULL REFERENCES firmware_file(id),
        firmware_module_id BIGINT NOT NULL REFERENCES firmware_module(id),
        module_version VARCHAR(100) NOT NULL,
        CONSTRAINT firmware_file_firmware_module_pkey PRIMARY KEY (firmware_file_id, firmware_module_id)
    );
    -- Index on firmware_file_id should be covered by the PK on (firmware_file_id, firmware_module_id).
    CREATE INDEX firmware_file_firmware_module_ix_firmware_module_id ON firmware_file_firmware_module (firmware_module_id);

    ALTER TABLE firmware_file_firmware_module OWNER TO osp_admin;

    GRANT ALL ON TABLE firmware_file_firmware_module TO osp_admin;
    GRANT SELECT ON TABLE firmware_file_firmware_module TO osgp_read_only_ws_user;

    -- Make sure the old firmware module versions are migrated to firmware_file_firmware_module.
    INSERT INTO firmware_file_firmware_module (firmware_file_id, firmware_module_id, module_version)
        SELECT id, (SELECT id FROM firmware_module WHERE description = 'communication_module_active_firmware'), module_version_comm
        FROM firmware_file WHERE module_version_comm IS NOT NULL;

    INSERT INTO firmware_file_firmware_module (firmware_file_id, firmware_module_id, module_version)
        SELECT id, (SELECT id FROM firmware_module WHERE description = 'module_active_firmware'), module_version_ma
        FROM firmware_file WHERE module_version_ma IS NOT NULL;

    INSERT INTO firmware_file_firmware_module (firmware_file_id, firmware_module_id, module_version)
        SELECT id, (SELECT id FROM firmware_module WHERE description = 'm_bus'), module_version_mbus
        FROM firmware_file WHERE module_version_mbus IS NOT NULL;

    INSERT INTO firmware_file_firmware_module (firmware_file_id, firmware_module_id, module_version)
        SELECT id, (SELECT id FROM firmware_module WHERE description = 'security'), module_version_sec
        FROM firmware_file WHERE module_version_sec IS NOT NULL;

    -- The old module_version_func holds the new 'active_firmware' module version for smart meters,
    -- while it holds the new 'functional' module version for other types of devices.
    -- This method errs when the database contains firmware files that have not been linked to a
    -- device model that is related to a smart meter device or to a smart meter device directly.
    -- Such firmware files will be given a functional module version instead of an active_firmware module version.

    INSERT INTO firmware_file_firmware_module (firmware_file_id, firmware_module_id, module_version)
        SELECT DISTINCT id, (SELECT id FROM firmware_module WHERE description = 'active_firmware'), module_version_func
        FROM firmware_file
            WHERE module_version_func IS NOT NULL AND
            (
                EXISTS ( -- a smart meter that is linked to this firmware file
                    SELECT 1 FROM device_firmware
                        INNER JOIN device      ON device_firmware.device_id = device.id
                        INNER JOIN smart_meter ON device.id = smart_meter.id
                        WHERE device_firmware.firmware_id = firmware_file.id
                ) OR
                EXISTS ( -- a device model that is linked to both this firmware file and a smart meter
                    SELECT 1 FROM device_model_firmware_file
                        INNER JOIN device_model   ON device_model_firmware_file.device_model_id = device_model.id
                        INNER JOIN device         ON device_model.id = device.device_model
                        INNER JOIN smart_meter    ON device.id = smart_meter.id
                        WHERE device_model_firmware_file.firmware_file_id = firmware_file.id
                )
            );

    INSERT INTO firmware_file_firmware_module (firmware_file_id, firmware_module_id, module_version)
        SELECT id, (SELECT id FROM firmware_module WHERE description = 'functional'), module_version_func
        FROM firmware_file WHERE module_version_func IS NOT NULL AND id NOT IN (
            -- firmware file should not already have been determined to be for smart meters
            -- (in which case module_version_func is already mapped to active_firmware)
            SELECT fffm2.firmware_file_id FROM firmware_file_firmware_module fffm2
                WHERE fffm2.firmware_module_id = (SELECT id FROM firmware_module WHERE description = 'active_firmware')
        );

    ALTER TABLE firmware_file
        DROP COLUMN module_version_comm,
        DROP COLUMN module_version_func,
        DROP COLUMN module_version_ma,
        DROP COLUMN module_version_mbus,
        DROP COLUMN module_version_sec;

    ALTER TABLE device_firmware RENAME TO device_firmware_file;
    ALTER TABLE device_firmware_file RENAME firmware_id TO firmware_file_id;
    ALTER TABLE device_firmware_file RENAME CONSTRAINT fk7e0c025199350fa3 TO device_firmware_file_device_id_fkey;
    ALTER TABLE device_firmware_file RENAME CONSTRAINT fk8e1c015199350fa3 TO device_firmware_file_firmware_file_id_fkey;

    -- The database contains two sequences related to device_firmware.id (now device_firmware_file.id).
    -- Drop one and rename the other to make it obvious in which table it is used.
    DROP SEQUENCE firmware_id_sequence;
    ALTER SEQUENCE firmware_id_seq RENAME TO device_firmware_file_id_seq;

    ALTER TABLE device_firmware_file DROP COLUMN active;

    DELETE FROM device_firmware_file WHERE device_id IS NULL OR firmware_file_id IS NULL;
    ALTER TABLE device_firmware_file
        ALTER COLUMN device_id SET NOT NULL,
        ALTER COLUMN firmware_file_id SET NOT NULL;

    -- Make installation_date not null, because it is used to determine the latest firmware on a device.
    UPDATE device_firmware_file SET installation_date = creation_time WHERE installation_date IS NULL;
    ALTER TABLE device_firmware_file ALTER COLUMN installation_date SET NOT NULL;

    CREATE INDEX device_firmware_file_ix_device_id ON device_firmware_file (device_id);
    CREATE INDEX device_firmware_file_ix_firmware_file_id ON device_firmware_file (firmware_file_id);

    -- Add an id to manufacturer and use this as PK/FK instead of the 4 character code.

    CREATE SEQUENCE manufacturer_id_seq
        INCREMENT 1
        MINVALUE 1
        MAXVALUE 9223372036854775807
        START 1
        CACHE 1;
    ALTER TABLE manufacturer_id_seq
        OWNER TO osp_admin;

    ALTER TABLE manufacturer
        ADD id BIGINT NOT NULL DEFAULT nextval('manufacturer_id_seq'::regclass),
        ADD CONSTRAINT manufacturer_key UNIQUE (id);
    ALTER TABLE manufacturer RENAME manufacturer_id TO code;

    -- Use the new manufacturer id as FK instead of the 4 character code.
    -- Increase the size of model_code and create a unique index on the combination of manufacturer_id and model_code.
    ALTER TABLE device_model ADD COLUMN manufacturer_id_temp BIGINT;
    UPDATE device_model SET manufacturer_id_temp = (SELECT id FROM manufacturer WHERE code = manufacturer_id);
    ALTER TABLE device_model
        DROP CONSTRAINT model_manufacturer_fkey,
        ALTER COLUMN manufacturer_id TYPE BIGINT USING manufacturer_id_temp,
        DROP COLUMN manufacturer_id_temp,
        ALTER COLUMN model_code TYPE CHARACTER VARYING(255),
        ADD CONSTRAINT device_model_manufacturer_id_model_code_key UNIQUE (manufacturer_id, model_code);

    -- Replace the primary key on manufacturer.
    ALTER TABLE manufacturer DROP CONSTRAINT manufacturer_pkey;
    CREATE UNIQUE INDEX manufacturer_ix_id_temp ON manufacturer (id);
    ALTER TABLE manufacturer
        DROP CONSTRAINT manufacturer_key,
        ADD CONSTRAINT manufacturer_pkey PRIMARY KEY USING INDEX manufacturer_ix_id_temp,
        ADD CONSTRAINT manufacturer_code_key UNIQUE (code);

    ALTER TABLE device_model
        ADD CONSTRAINT device_model_manufacturer_id_fkey FOREIGN KEY (manufacturer_id) REFERENCES manufacturer(id);

END IF;

END;
$$