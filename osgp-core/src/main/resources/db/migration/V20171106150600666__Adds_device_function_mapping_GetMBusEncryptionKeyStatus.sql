DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'GET_M_BUS_ENCRYPTION_KEY_STATUS' AND function_group = 'OWNER') THEN
	INSERT INTO device_function_mapping (function_group, "function") VALUES ('OWNER', 'GET_M_BUS_ENCRYPTION_KEY_STATUS');
END IF;

END;
$$
