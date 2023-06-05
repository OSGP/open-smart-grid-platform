-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

    IF EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'DE_COUPLE_MBUS_DEVICE') THEN
        UPDATE device_function_mapping SET "function" = 'DECOUPLE_MBUS_DEVICE' WHERE "function" = 'DE_COUPLE_MBUS_DEVICE';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'DECOUPLE_MBUS_DEVICE_BY_CHANNEL') THEN
        insert into device_function_mapping (function_group, "function") values ('OWNER', 'DECOUPLE_MBUS_DEVICE_BY_CHANNEL');
    END IF;


END;
$$
