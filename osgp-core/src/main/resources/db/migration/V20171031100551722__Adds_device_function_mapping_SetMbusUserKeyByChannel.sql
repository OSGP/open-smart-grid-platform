DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'SET_MBUS_USER_KEY_BY_CHANNEL' AND function_group = 'OWNER') THEN
	INSERT INTO device_function_mapping (function_group, "function") VALUES ('OWNER', 'SET_MBUS_USER_KEY_BY_CHANNEL');
END IF;

END;
$$
