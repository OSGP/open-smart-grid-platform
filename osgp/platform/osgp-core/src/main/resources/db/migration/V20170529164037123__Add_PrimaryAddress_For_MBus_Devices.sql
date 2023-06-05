-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
 	WHERE table_schema=current_schema 
 	AND table_name = 'smart_meter' 
 	AND column_name='mbus_primary_address') THEN
	ALTER TABLE ONLY "smart_meter" ADD COLUMN "mbus_primary_address" smallint;
END IF;

END;
$$