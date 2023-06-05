-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DROP INDEX IF EXISTS device_log_item_device_identification_upper;
CREATE INDEX IF NOT EXISTS device_log_item_ix_upper_device_identification_modification_time ON device_log_item(UPPER(device_identification), modification_time);
ANALYZE device_log_item;
