-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
    BEGIN

        IF NOT EXISTS (SELECT 1 FROM device_function_mapping 
		                WHERE "function" = 'DECOUPLE_MBUS_DEVICE_ADMINISTRATIVE'
						  AND function_group = 'OWNER') THEN
            INSERT INTO device_function_mapping (function_group, "function") VALUES ('OWNER', 'DECOUPLE_MBUS_DEVICE_ADMINISTRATIVE');
        END IF;

    END;
$$
