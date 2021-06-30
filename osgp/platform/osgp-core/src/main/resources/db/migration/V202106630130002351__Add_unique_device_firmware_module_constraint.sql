ALTER TABLE ONLY device_firmware_module
    ADD CONSTRAINT device_firmware_module_ix_device_id_firmware_module_id UNIQUE (device_id, firmware_module_id);
