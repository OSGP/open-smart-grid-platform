-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO $$
BEGIN

IF EXISTS (SELECT 1 FROM pg_class where relname = 'device_model_id_sequence' )

THEN

	-- device_model table
	DROP SEQUENCE device_model_id_sequence;
	
	ALTER TABLE device_model RENAME COLUMN manufacturer TO manufacturer_id;
	
	ALTER TABLE device_model RENAME COLUMN code TO model_code;
	ALTER TABLE device_model ALTER COLUMN model_code TYPE varchar(15);
	
	ALTER TABLE device_model RENAME COLUMN name TO description;
	ALTER TABLE device_model ALTER COLUMN description TYPE varchar(255);
	
	-- manufacturer table
	ALTER TABLE manufacturer RENAME COLUMN code TO manufacturer_id;

END IF;

END;
$$
