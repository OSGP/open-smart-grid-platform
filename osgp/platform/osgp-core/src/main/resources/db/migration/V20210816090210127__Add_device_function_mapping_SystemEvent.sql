DO
$$
BEGIN

    IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'SYSTEM_EVENT') THEN
        insert into device_function_mapping (function_group, "function") values ('OWNER', 'SYSTEM_EVENT');
    END IF;

END;
$$
