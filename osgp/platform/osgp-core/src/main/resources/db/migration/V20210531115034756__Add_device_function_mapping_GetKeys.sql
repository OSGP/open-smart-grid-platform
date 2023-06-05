-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

    IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'GET_KEYS') THEN
        insert into device_function_mapping (function_group, "function") values ('OWNER', 'GET_KEYS');
    END IF;

END;
$$
