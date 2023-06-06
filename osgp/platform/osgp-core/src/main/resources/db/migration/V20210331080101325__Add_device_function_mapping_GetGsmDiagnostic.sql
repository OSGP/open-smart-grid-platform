DO
$$
BEGIN

    IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'GET_GSM_DIAGNOSTIC') THEN
        insert into device_function_mapping (function_group, "function") values ('OWNER', 'GET_GSM_DIAGNOSTIC');
    END IF;

END;
$$
