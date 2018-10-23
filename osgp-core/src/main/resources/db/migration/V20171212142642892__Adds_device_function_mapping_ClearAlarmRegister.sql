DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'CLEAR_ALARM_REGISTER' AND function_group = 'OWNER') THEN
	INSERT INTO device_function_mapping (function_group, "function") VALUES ('OWNER', 'CLEAR_ALARM_REGISTER');
END IF;

END;
$$
