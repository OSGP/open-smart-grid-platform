-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

ALTER TABLE dlms_device ALTER COLUMN invocation_counter SET DEFAULT 0;
UPDATE dlms_device SET invocation_counter = 0 WHERE invocation_counter IS NULL;

END;
$$


