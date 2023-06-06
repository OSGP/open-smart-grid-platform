DO
$$
    BEGIN
        IF NOT EXISTS(SELECT 1 FROM device_function_mapping WHERE function = 'GET_ACTUAL_POWER_QUALITY')
        THEN INSERT INTO device_function_mapping values ('OWNER', 'GET_ACTUAL_POWER_QUALITY');
        END IF;
    END;
$$