ALTER TABLE firmware RENAME TO device_firmware;

ALTER TABLE device_firmware DROP COLUMN firmware_version;
ALTER TABLE device_firmware DROP COLUMN description;

ALTER TABLE device_firmware RENAME COLUMN device TO device_id;
ALTER TABLE device_firmware RENAME COLUMN device_model_firmware TO device_model_firmware_id;