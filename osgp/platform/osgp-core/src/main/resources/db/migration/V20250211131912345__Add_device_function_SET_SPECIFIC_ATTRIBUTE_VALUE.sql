DO
$$
BEGIN

    IF NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'SET_SPECIFIC_ATTRIBUTE_VALUE') THEN
        insert into device_function_mapping (function_group, "function") values ('OWNER', 'SET_SPECIFIC_ATTRIBUTE_VALUE');
    END IF;

END;
$$
