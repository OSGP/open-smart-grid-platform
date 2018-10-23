ALTER TABLE ONLY relay_status
    ADD CONSTRAINT fk_relay_status_device FOREIGN KEY (device_id) REFERENCES device(id);
