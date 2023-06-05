-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'GET_MBUS_ENCRYPTION_KEY_STATUS' AND function_group = 'OWNER') THEN
	INSERT INTO device_function_mapping (function_group, "function") VALUES ('OWNER', 'GET_MBUS_ENCRYPTION_KEY_STATUS');
END IF;

END;
$$
