-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

  IF NOT EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = current_schema AND table_name = 'device' AND column_name = 'mast_segment')
  THEN
    ALTER TABLE device ADD COLUMN mast_segment VARCHAR(20);
    ALTER TABLE device ADD COLUMN batch_number SMALLINT;

    COMMENT ON COLUMN device.mast_segment IS 'The mast/segment used for device communication over CDMA.';
    COMMENT ON COLUMN device.batch_number IS 'The batch number used for batched device communication over CDMA.';

  END IF;
  
  IF NOT EXISTS(SELECT 1 FROM device_function_mapping WHERE function = 'UPDATE_DEVICE_CDMA_SETTINGS')
    THEN INSERT INTO device_function_mapping values ('OWNER', 'UPDATE_DEVICE_CDMA_SETTINGS');
  END IF;
  
END;
$$ 