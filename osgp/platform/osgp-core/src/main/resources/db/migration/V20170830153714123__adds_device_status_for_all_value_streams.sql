-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN
	
  IF NOT EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = current_schema AND table_name = 'device' AND column_name = 'device_lifecycle_status')
  THEN
  	ALTER TABLE device ADD COLUMN device_lifecycle_status VARCHAR(255);
  	ALTER TABLE device ALTER COLUMN device_lifecycle_status SET DEFAULT 'NEW_IN_INVENTORY';
  	
  	
  	-- Migrating publiclighting devices to appropriate device lifecycle status --
  	-- A public lighting device without a device_type is not yet registered with the platform, and therefor gets the status NEW_IN_INVENTORY --
  	UPDATE device SET device_lifecycle_status = 'NEW_IN_INVENTORY' 
  	WHERE (device.device_type = 'SSLD' OR device.device_type = 'LMD' OR device.device_type = 'PSLD' OR device.device_type IS NULL)
  	AND device.is_active = false 
  	AND device.is_activated = false 
  	AND device.technical_installation_date IS NULL;

  	-- Once a device is registered, it's device_type can not be null anymore --
  	UPDATE device SET device_lifecycle_status = 'REGISTERED' 
   	WHERE (device.device_type = 'SSLD' OR device.device_type = 'LMD' OR device.device_type = 'PSLD')
  	AND device.is_active = true 
  	AND device.is_activated = true 
  	AND device.technical_installation_date IS NULL;
  	
  	UPDATE device SET device_lifecycle_status = 'IN_USE'
  	WHERE (device.device_type = 'SSLD' OR device.device_type = 'LMD' OR device.device_type = 'PSLD')
  	AND device.is_active = true 
  	AND device.is_activated = true 
  	AND device.technical_installation_date IS NOT NULL;
  	
  	UPDATE device SET device_lifecycle_status = 'DESTROYED' 
  	WHERE (device.device_type = 'SSLD' OR device.device_type = 'LMD' OR device.device_type = 'PSLD')
  	AND device.is_active = false 
  	AND device.is_activated = false 
  	AND device.technical_installation_date IS NOT NULL;
  
  	-- Migrating rtu devices to appropriate device lifecycle status--
  	UPDATE device SET device_lifecycle_status = 'IN_USE' 
  	WHERE device.device_type = 'RTU' 
  	AND device.is_active = true 
  	AND device.is_activated = true; 
  	
  	UPDATE device SET device_lifecycle_status = 'READY_FOR_USE' 
  	WHERE device.device_type = 'RTU' 
  	AND device.is_active = false 
  	AND device.is_activated = false;
  	
  	-- Migrating smartmetering devices to appropriate device lifecycle status--
  	UPDATE device SET device_lifecycle_status = 'DESTROYED' 
  	WHERE (device.device_type = 'SMART_METER_E' OR device.device_type = 'SMART_METER_G')
  	AND device.is_active = false; 
  	
  	UPDATE device SET device_lifecycle_status = 'IN_USE' 
  	WHERE (device.device_type = 'SMART_METER_E' OR device.device_type = 'SMART_METER_G')
  	AND device.is_active = true;
  	
  	-- Assign a default value to every row where device_lifecycle_status is still null --
  	UPDATE device SET device_lifecycle_status = 'NEW_IN_INVENTORY' WHERE device_lifecycle_status IS NULL;
  	-- Make device_lifecycle_status non-nullable --
  	ALTER TABLE device ALTER COLUMN device_lifecycle_status SET NOT NULL;
  	
  END IF;
  
  -- Get rid of the is_active column, this is replaced by device_lifecycle_status --
  IF EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = current_schema AND table_name = 'device' AND column_name = 'is_active')
  THEN
  	ALTER TABLE device DROP COLUMN is_active;
  END IF;
  
  IF NOT EXISTS(SELECT 1 FROM device_function_mapping WHERE function = 'SET_DEVICE_LIFECYCLE_STATUS')
  THEN INSERT INTO device_function_mapping values ('OWNER', 'SET_DEVICE_LIFECYCLE_STATUS');
  END IF;
  
END;
$$ 