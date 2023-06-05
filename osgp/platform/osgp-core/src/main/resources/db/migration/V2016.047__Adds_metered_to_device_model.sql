-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE device_model ADD metered boolean;

UPDATE device_model SET metered = false;

ALTER TABLE device_model ALTER COLUMN metered SET NOT NULL;
