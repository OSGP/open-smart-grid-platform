ALTER TABLE device_model_firmware DROP COLUMN file;

ALTER TABLE device_model_firmware ADD COLUMN file oid;
