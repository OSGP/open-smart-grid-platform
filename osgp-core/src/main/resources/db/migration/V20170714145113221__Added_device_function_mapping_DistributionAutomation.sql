DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'GET_HEALTH_STATUS') THEN
	insert into device_function_mapping (function_group, "function") values ('OWNER', 'GET_HEALTH_STATUS');
END IF;

IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'GET_POWER_QUALITY_VALUES') THEN
	insert into device_function_mapping (function_group, "function") values ('OWNER', 'GET_POWER_QUALITY_VALUES');
END IF;

IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'GET_POWER_QUALITY_VALUES_PERIODIC') THEN
	insert into device_function_mapping (function_group, "function") values ('OWNER', 'GET_POWER_QUALITY_VALUES_PERIODIC');
END IF;

IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'GET_DEVICE_MODEL') THEN
	insert into device_function_mapping (function_group, "function") values ('OWNER', 'GET_DEVICE_MODEL');
END IF;

END;
$$
