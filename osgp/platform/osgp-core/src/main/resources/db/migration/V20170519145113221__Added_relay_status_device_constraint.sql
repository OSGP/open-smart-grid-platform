-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE ONLY relay_status
    ADD CONSTRAINT fk_relay_status_device FOREIGN KEY (device_id) REFERENCES device(id);
