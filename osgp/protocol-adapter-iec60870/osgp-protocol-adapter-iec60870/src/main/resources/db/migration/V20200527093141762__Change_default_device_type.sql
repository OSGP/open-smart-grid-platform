-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO $$
BEGIN

  ALTER TABLE iec60870_device ALTER COLUMN device_type SET DEFAULT 'DISTRIBUTION_AUTOMATION_DEVICE';
  UPDATE iec60870_device SET device_type = 'DISTRIBUTION_AUTOMATION_DEVICE' WHERE device_type = 'DA_DEVICE';

END$$;
