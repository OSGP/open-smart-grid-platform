-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'CONFIGURE_DEFINABLE_LOAD_PROFILE' AND function_group = 'OWNER') THEN
	INSERT INTO device_function_mapping (function_group, "function") VALUES ('OWNER', 'CONFIGURE_DEFINABLE_LOAD_PROFILE');
END IF;

END;
$$
