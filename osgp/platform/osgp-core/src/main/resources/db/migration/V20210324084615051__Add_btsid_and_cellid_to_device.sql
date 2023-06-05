-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN
    IF NOT EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE TABLE_SCHEMA = current_schema AND TABLE_NAME = 'device' AND COLUMN_NAME = 'bts_id')
    THEN
        ALTER TABLE device ADD COLUMN bts_id INTEGER;
    END IF;

    COMMENT ON COLUMN device.bts_id IS 'The ID of the Base Transceiver Station to which this device is linked.';

    IF NOT EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE TABLE_SCHEMA = current_schema AND TABLE_NAME = 'device' AND COLUMN_NAME = 'cell_id')
    THEN
        ALTER TABLE device ADD COLUMN cell_id INTEGER;
    END IF;

    COMMENT ON COLUMN device.cell_id IS 'The ID of the Cell to which this device is linked.';

END;
$$