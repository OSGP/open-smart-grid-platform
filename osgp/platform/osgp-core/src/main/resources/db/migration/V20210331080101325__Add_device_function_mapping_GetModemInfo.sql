DO
$$
BEGIN

    IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'GET_MODEM_INFO') THEN
        insert into device_function_mapping (function_group, "function") values ('OWNER', 'GET_MODEM_INFO');
    END IF;

END;
$$
