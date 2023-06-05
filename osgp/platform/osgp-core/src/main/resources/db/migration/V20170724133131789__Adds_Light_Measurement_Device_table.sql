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
    AND    tablename  = 'light_measurement_device') THEN

    -- Create the table for light_measurement_device along with the proper permissions
    CREATE TABLE light_measurement_device (
        id BIGINT NOT NULL,
        description VARCHAR(255),
        code VARCHAR(10),
        color VARCHAR(10),
        digital_input SMALLINT,
        last_communication_time TIMESTAMP WITHOUT TIME ZONE
    );
    
    ALTER TABLE ONLY public.light_measurement_device ADD CONSTRAINT light_measurement_device_pkey PRIMARY KEY (id);

    ALTER TABLE public.light_measurement_device OWNER TO osp_admin;

    -- Create grants for osp_admin and osgp_read_only_ws_user
    GRANT ALL ON TABLE light_measurement_device
        TO osp_admin;
    GRANT SELECT ON TABLE light_measurement_device
        TO osgp_read_only_ws_user;

END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   information_schema.columns
    WHERE  table_schema = current_schema
    AND    table_name   = 'ssld'
    AND    column_name  = 'light_measurement_device_id') THEN

    -- Create new column for foreign key to light_measurement_device
    ALTER TABLE ONLY ssld
        ADD COLUMN light_measurement_device_id BIGINT;
    
    -- Create constraint for foreign key
    ALTER TABLE ONLY ssld
        ADD CONSTRAINT fk_ssld_to_light_measurement_device
        FOREIGN KEY (light_measurement_device_id)
        REFERENCES light_measurement_device(id);

END IF;

IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'SET_LIGHT_MEASUREMENT_DEVICE') THEN
	insert into device_function_mapping (function_group, "function") values ('OWNER', 'SET_LIGHT_MEASUREMENT_DEVICE');
END IF;

END;
$$