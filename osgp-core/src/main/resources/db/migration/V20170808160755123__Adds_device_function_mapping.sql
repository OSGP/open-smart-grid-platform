DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'GET_LIGHT_SENSOR_STATUS' AND function_group = 'OWNER') THEN
	insert into device_function_mapping (function_group, "function") values ('OWNER', 'GET_LIGHT_SENSOR_STATUS');
END IF;

IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'GET_LIGHT_SENSOR_STATUS' AND function_group = 'AD_HOC') THEN
	insert into device_function_mapping (function_group, "function") values ('AD_HOC', 'GET_LIGHT_SENSOR_STATUS');
END IF;

IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'SET_LIGHT_MEASUREMENT_DEVICE' AND function_group = 'MANAGEMENT') THEN
	insert into device_function_mapping (function_group, "function") values ('MANAGEMENT', 'SET_LIGHT_MEASUREMENT_DEVICE');
END IF;

END;
$$
