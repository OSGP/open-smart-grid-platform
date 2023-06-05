-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO $$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'event_device_date_idx'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX event_device_date_idx ON event (device, date_time);
END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'event_date_time_idx'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX event_date_time_idx ON event (date_time);
END IF;

END;
$$
