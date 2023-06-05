-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DROP TABLE firmware_history;

ALTER TABLE firmware DROP COLUMN device_model;

ALTER TABLE firmware DROP COLUMN installation_file;

ALTER TABLE firmware ADD COLUMN installation_date timestamp without time zone;

ALTER TABLE firmware ADD COLUMN installed_by character varying(255);

ALTER TABLE firmware ADD COLUMN active boolean;

ALTER TABLE firmware ADD COLUMN device bigint;

ALTER TABLE ONLY firmware
    ADD CONSTRAINT fk7e0c025199350fa3 FOREIGN KEY (device) REFERENCES device(id);

ALTER TABLE device DROP COLUMN firmware;

ALTER TABLE device ADD COLUMN device_model bigint;

ALTER TABLE ONLY device
    ADD CONSTRAINT fk7e2c025199350fa3 FOREIGN KEY (device_model) REFERENCES device_model(id);
    
ALTER TABLE firmware ADD COLUMN device_model_firmware bigint;    

ALTER TABLE ONLY firmware
    ADD CONSTRAINT fk8e1c015199350fa3 FOREIGN KEY (device_model_firmware) REFERENCES device_model_firmware(id);