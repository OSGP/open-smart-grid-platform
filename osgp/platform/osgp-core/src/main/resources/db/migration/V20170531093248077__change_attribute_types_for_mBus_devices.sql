-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

IF EXISTS (SELECT 1 FROM information_schema.columns
 	WHERE table_schema = current_schema
 	AND table_name = 'smart_meter'
 	AND column_name = 'mbus_identification_number'
 	AND data_type = 'character varying') THEN
	ALTER TABLE smart_meter ALTER COLUMN mbus_identification_number TYPE integer USING CAST(NULLIF(TRIM(mbus_identification_number), '') AS integer);
END IF;

IF EXISTS (SELECT 1 FROM information_schema.columns
 	WHERE table_schema = current_schema
 	AND table_name = 'smart_meter'
 	AND column_name = 'mbus_version'
 	AND data_type = 'character varying') THEN
	ALTER TABLE smart_meter ALTER COLUMN mbus_version TYPE smallint USING CAST(NULLIF(TRIM(mbus_version), '') AS smallint);
END IF;

IF EXISTS (SELECT 1 FROM information_schema.columns
 	WHERE table_schema = current_schema
 	AND table_name = 'smart_meter'
 	AND column_name = 'mbus_device_type_identification'
 	AND data_type = 'character varying') THEN
	ALTER TABLE smart_meter ALTER COLUMN mbus_device_type_identification TYPE smallint USING CAST(NULLIF(TRIM(mbus_device_type_identification), '') AS smallint);
END IF;

END;
$$