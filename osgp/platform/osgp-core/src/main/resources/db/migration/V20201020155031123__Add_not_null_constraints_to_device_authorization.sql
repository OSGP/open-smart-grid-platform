-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DELETE FROM device_authorization WHERE device IS NULL OR organisation IS NULL OR function_group IS NULL;

ALTER TABLE device_authorization ALTER COLUMN device SET NOT NULL;
ALTER TABLE device_authorization ALTER COLUMN organisation SET NOT NULL;
ALTER TABLE device_authorization ALTER COLUMN function_group SET NOT NULL;
