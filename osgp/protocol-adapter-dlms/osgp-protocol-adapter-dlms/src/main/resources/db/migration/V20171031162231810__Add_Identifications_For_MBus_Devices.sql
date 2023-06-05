-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM information_schema.columns
 	WHERE table_schema=current_schema
 	AND table_name = 'dlms_device'
 	AND column_name='mbus_identification_number') THEN
	ALTER TABLE ONLY dlms_device ADD COLUMN mbus_identification_number INTEGER;
END IF;

IF NOT EXISTS (SELECT 1 FROM information_schema.columns
 	WHERE table_schema=current_schema
 	AND table_name = 'dlms_device'
 	AND column_name='mbus_manufacturer_identification') THEN
	ALTER TABLE ONLY dlms_device ADD COLUMN mbus_manufacturer_identification VARCHAR(3);
END IF;

END;
$$
