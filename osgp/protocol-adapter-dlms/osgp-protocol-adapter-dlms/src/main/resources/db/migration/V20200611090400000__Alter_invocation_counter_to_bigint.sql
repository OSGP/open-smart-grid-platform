-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

ALTER TABLE dlms_device ALTER COLUMN invocation_counter TYPE BIGINT;

END;
$$
