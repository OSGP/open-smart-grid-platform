-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE security_key
   ALTER COLUMN valid_from DROP NOT NULL;

DROP INDEX IF EXISTS security_key_valid_idx;

CREATE UNIQUE INDEX security_key_valid_idx ON security_key (dlms_device_id, security_key_type, valid_from)
WHERE valid_from IS NOT NULL AND valid_to IS NULL;
