DO
$$
    BEGIN

        IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'UPDATE_PROTOCOL' AND function_group = 'OWNER') THEN
            INSERT INTO device_function_mapping (function_group, "function") VALUES ('OWNER', 'UPDATE_PROTOCOL');
        END IF;

    END;
$$