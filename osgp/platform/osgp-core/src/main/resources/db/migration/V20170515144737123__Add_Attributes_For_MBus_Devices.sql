-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
 	WHERE table_schema=current_schema 
 	AND table_name = 'smart_meter' 
 	AND column_name='mbus_identification_number') THEN
	ALTER TABLE ONLY "smart_meter" ADD COLUMN "mbus_identification_number" VARCHAR(8);
END IF;

IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
 	WHERE table_schema=current_schema 
 	AND table_name = 'smart_meter' 
 	AND column_name='mbus_manufacturer_identification') THEN
	ALTER TABLE ONLY "smart_meter" ADD COLUMN "mbus_manufacturer_identification" VARCHAR(3);
END IF;

IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
 	WHERE table_schema=current_schema 
 	AND table_name = 'smart_meter' 
 	AND column_name='mbus_version') THEN
	ALTER TABLE ONLY "smart_meter" ADD COLUMN "mbus_version" VARCHAR(3);
END IF;

IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
 	WHERE table_schema=current_schema 
 	AND table_name = 'smart_meter' 
 	AND column_name='mbus_device_type_identification') THEN
	ALTER TABLE ONLY "smart_meter" ADD COLUMN "mbus_device_type_identification" VARCHAR(2);
END IF;

END;
$$