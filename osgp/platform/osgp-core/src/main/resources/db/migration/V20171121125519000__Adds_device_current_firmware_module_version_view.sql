DO
$$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   pg_views
    WHERE  viewname = 'device_current_firmware_module_version'
    AND    schemaname = 'public'
    ) THEN
    
    CREATE VIEW device_current_firmware_module_version AS
        SELECT device.id device_id
        , fm.description module_description
        , fffm.module_version module_version
        FROM device
        JOIN device_firmware_file dff ON device.id = dff.device_id
        JOIN firmware_file ff ON ff.id = dff.firmware_file_id
        JOIN firmware_file_firmware_module fffm ON ff.id = fffm.firmware_file_id
        JOIN firmware_module fm ON fm.id = fffm.firmware_module_id
        WHERE NOT EXISTS
        (SELECT 1
         FROM device_firmware_file dff2
         JOIN firmware_file ff2 ON ff2.id = dff2.firmware_file_id
         JOIN firmware_file_firmware_module fffm2 ON ff2.id = fffm2.firmware_file_id
         WHERE dff2.device_id = dff.device_id
         AND fffm2.firmware_module_id = fffm.firmware_module_id
         AND dff2.installation_date > dff.installation_date
        );

    GRANT SELECT ON device_current_firmware_module_version TO osgp_read_only_ws_user;

END IF;

END;
$$
