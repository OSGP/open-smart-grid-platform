-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'SET_CLOCK_CONFIGURATION') THEN
	insert into device_function_mapping (function_group, "function") values ('OWNER', 'SET_CLOCK_CONFIGURATION'); 
END IF;

END;
$$
