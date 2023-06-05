-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE event ADD COLUMN date_time timestamp without time zone;
UPDATE event SET date_time = creation_time;
ALTER TABLE event ALTER COLUMN date_time SET NOT NULL;
