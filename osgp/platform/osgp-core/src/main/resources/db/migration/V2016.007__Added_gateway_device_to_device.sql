-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE device
	ADD COLUMN gateway_device_id BIGINT;

ALTER TABLE ONLY device
    ADD CONSTRAINT fk_device_gateway_device FOREIGN KEY (gateway_device_id) REFERENCES device(id);
