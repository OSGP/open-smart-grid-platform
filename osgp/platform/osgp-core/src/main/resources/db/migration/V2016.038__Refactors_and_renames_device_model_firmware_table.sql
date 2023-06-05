-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE device_model_firmware RENAME TO firmware;

ALTER TABLE firmware DROP COLUMN model_code;
ALTER TABLE firmware RENAME COLUMN device_model TO device_model_id;

ALTER TABLE device_firmware RENAME COLUMN device_model_firmware_id TO firmware_id;
