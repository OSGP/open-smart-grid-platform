DO
$$
    BEGIN

        IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'SCHEDULE_TEST_ALARM' AND function_group = 'OWNER') THEN
            INSERT INTO device_function_mapping (function_group, "function") VALUES ('OWNER', 'SCHEDULE_TEST_ALARM');
        END IF;

    END;
$$
