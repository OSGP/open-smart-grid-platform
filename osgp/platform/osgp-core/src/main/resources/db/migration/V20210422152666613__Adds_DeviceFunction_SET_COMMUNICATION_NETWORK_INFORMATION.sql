DO
$$
    BEGIN
        IF NOT EXISTS(SELECT 1 FROM device_function_mapping WHERE function = 'SET_COMMUNICATION_NETWORK_INFORMATION')
        THEN INSERT INTO device_function_mapping values ('OWNER', 'SET_COMMUNICATION_NETWORK_INFORMATION');
        END IF;
    END;
$$