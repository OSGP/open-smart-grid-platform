DO
$$
BEGIN

    IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'GET_THD_FINGERPRINT') THEN
        insert into device_function_mapping (function_group, "function") values ('OWNER', 'GET_THD_FINGERPRINT');
    END IF;

END;
$$
