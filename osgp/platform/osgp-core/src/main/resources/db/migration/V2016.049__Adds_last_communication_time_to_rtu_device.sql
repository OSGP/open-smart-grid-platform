-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE rtu_device ADD last_communication_time TIMESTAMP WITHOUT TIME ZONE DEFAULT '2000-01-01 00:00:00';
