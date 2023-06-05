-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

IF NOT EXISTS (
    SELECT 1
	FROM   information_schema.columns
    WHERE  table_schema = current_schema
    AND    table_name  = 'dlms_device'
    AND    column_name = 'key_processing_start_time'
	) THEN

    ALTER TABLE dlms_device
      ADD COLUMN key_processing_start_time timestamp without time zone;

END IF;

END;
$$
