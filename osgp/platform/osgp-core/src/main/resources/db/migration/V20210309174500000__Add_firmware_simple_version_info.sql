DO
$$
    BEGIN
        IF NOT EXISTS(SELECT 1 FROM firmware_module WHERE description = 'simple_version_info')
        THEN
            INSERT INTO firmware_module (description) values ('simple_version_info');
        END IF;
    END ;
$$
