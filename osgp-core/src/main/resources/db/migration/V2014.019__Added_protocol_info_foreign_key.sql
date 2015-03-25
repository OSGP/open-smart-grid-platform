ALTER TABLE device_protocol_info RENAME TO protocol_info;

ALTER INDEX device_protocol_info_pkey RENAME TO protocol_info_pkey;

ALTER SEQUENCE device_protocol_info_id_seq RENAME TO protocol_info_id_seq;

ALTER TABLE device
	ADD COLUMN protocol_info_id BIGINT;

ALTER TABLE ONLY device
    ADD CONSTRAINT fk_device_protocol_info FOREIGN KEY (protocol_info_id) REFERENCES protocol_info(id);