DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'CLEAN_UP_MBUS_DEVICE_BY_CHANNEL') THEN
	insert into device_function_mapping (function_group, "function") values ('OWNER', 'CLEAN_UP_MBUS_DEVICE_BY_CHANNEL');
END IF;

END;
$$
