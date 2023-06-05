-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN


IF EXISTS (
    SELECT 1
    FROM   pg_constraint
    WHERE  conname = 'fk_ssld_to_light_measurement_device') THEN

    -- ReCreate fk_ssld_to_light_measurement_device (foreign key to light_measurement_device) constraint
    ALTER TABLE ONLY ssld
        DROP CONSTRAINT fk_ssld_to_light_measurement_device,
        ADD CONSTRAINT fk_ssld_to_light_measurement_device
        FOREIGN KEY (light_measurement_device_id)
        REFERENCES light_measurement_device(id)
        ON DELETE SET NULL;

END IF;

END;
$$
