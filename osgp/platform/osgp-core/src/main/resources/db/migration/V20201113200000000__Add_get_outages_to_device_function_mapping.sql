DO
$$
    BEGIN

        IF NOT EXISTS(SELECT 1 FROM device_function_mapping WHERE function = 'GET_OUTAGES')
        THEN INSERT INTO device_function_mapping values ('OWNER', 'GET_OUTAGES');
        END IF;

    END;
$$
