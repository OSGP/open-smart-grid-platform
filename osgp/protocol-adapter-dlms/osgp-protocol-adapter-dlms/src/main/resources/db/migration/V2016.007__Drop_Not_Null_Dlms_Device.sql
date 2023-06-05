-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE dlms_device ALTER COLUMN icc_id DROP NOT NULL;
ALTER TABLE dlms_device ALTER COLUMN communication_provider DROP NOT NULL;
