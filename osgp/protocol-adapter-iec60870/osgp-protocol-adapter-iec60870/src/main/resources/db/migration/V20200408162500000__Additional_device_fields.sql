-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO $$
BEGIN

  IF NOT EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = current_schema AND table_name = 'iec60870_device' AND column_name = 'device_type')
  THEN
  	ALTER TABLE iec60870_device ADD COLUMN device_type VARCHAR(255) NOT NULL DEFAULT 'DA_DEVICE';
  	ALTER TABLE iec60870_device ADD COLUMN gateway_device_identification VARCHAR(40);
  	ALTER TABLE iec60870_device ADD COLUMN information_object_address INTEGER;

  	ALTER TABLE iec60870_device ALTER COLUMN common_address SET DEFAULT 0;

    ALTER TABLE iec60870_device
      ADD CONSTRAINT iec60870_device_gateway_key
        UNIQUE (gateway_device_identification, information_object_address);

  	ALTER TABLE iec60870_device
  	  ADD CONSTRAINT iec60870_device_gateway_check
  	    CHECK ((gateway_device_identification IS NULL AND information_object_address IS NULL)
  		    OR (gateway_device_identification IS NOT NULL AND information_object_address IS NOT NULL));

    COMMENT ON COLUMN iec60870_device.device_type IS 'Device type of this IEC60870 device.';
    COMMENT ON COLUMN iec60870_device.gateway_device_identification IS 'Identification of the IEC60870 device acting as gateway for this IEC60870 device.';
    COMMENT ON COLUMN iec60870_device.information_object_address IS 'Information object address used for identifying this IEC60870 device when having a gateway device.';
  END IF;
  
END$$;
