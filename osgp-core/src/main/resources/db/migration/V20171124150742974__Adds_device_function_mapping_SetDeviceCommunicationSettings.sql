DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'SET_DEVICE_COMMUNICATION_SETTINGS' AND function_group = 'OWNER') THEN
	INSERT INTO device_function_mapping (function_group, "function") VALUES ('OWNER', 'SET_DEVICE_COMMUNICATION_SETTINGS');
END IF;

END;
$$
