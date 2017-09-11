DO
$$
BEGIN
	
  IF NOT EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = current_schema AND table_name = 'device' AND column_name = 'device_lifecycle_status')
  THEN
  	ALTER TABLE device ADD COLUMN device_lifecycle_status VARCHAR(255);
  	ALTER TABLE device DROP COLUMN is_active;
  END IF;
  
  IF NOT EXISTS(SELECT 1 FROM device_function_mapping WHERE function = 'SET_DEVICE_LIFECYCLE_STATUS')
  THEN
  INSERT INTO device_function_mapping values ('OWNER', 'SET_DEVICE_LIFECYCLE_STATUS');
  END IF;
  
END;
$$ 