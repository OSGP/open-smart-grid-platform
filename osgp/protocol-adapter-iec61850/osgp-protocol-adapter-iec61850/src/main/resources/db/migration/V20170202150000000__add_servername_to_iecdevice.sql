-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
 	WHERE table_schema=current_schema AND table_name = 'iec61850_device' AND column_name='server_name') THEN
	ALTER TABLE iec61850_device ADD COLUMN server_name character varying(25);
END IF;

               	
END;
$$
