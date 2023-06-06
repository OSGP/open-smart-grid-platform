DO
$$
    BEGIN
        IF NOT EXISTS(SELECT 1 FROM device_function_mapping WHERE function = 'CONNECT')
        THEN INSERT INTO device_function_mapping values ('OWNER', 'CONNECT');
        END IF;
    END;
$$