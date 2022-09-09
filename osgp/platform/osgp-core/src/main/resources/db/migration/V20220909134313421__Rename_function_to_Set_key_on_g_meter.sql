DO
$$
BEGIN

    IF EXISTS (SELECT 1 FROM device_function_mapping WHERE "function" = 'SET_ENCRYPTION_KEY_EXCHANGE_ON_G_METER') THEN
        UPDATE device_function_mapping SET "function" = 'SET_KEY_ON_G_METER' WHERE "function" = 'SET_ENCRYPTION_KEY_EXCHANGE_ON_G_METER';
    END IF;

END;
$$
