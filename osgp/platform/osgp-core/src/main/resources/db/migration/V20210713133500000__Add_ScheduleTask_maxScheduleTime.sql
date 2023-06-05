-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN
    IF NOT EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE TABLE_SCHEMA = current_schema AND TABLE_NAME = 'scheduled_task' AND COLUMN_NAME = 'max_schedule_time')
    THEN
        ALTER TABLE scheduled_task ADD COLUMN max_schedule_time timestamp without time zone;
    END IF;

    COMMENT ON COLUMN scheduled_task.max_schedule_time IS 'A time after which the task should not be scheduled, because it is no longer useful to the requesting system.';
END;
$$
