-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE dlms_device ADD COLUMN port bigint;
ALTER TABLE dlms_device ADD COLUMN client_id bigint;
ALTER TABLE dlms_device ADD COLUMN logical_id bigint;