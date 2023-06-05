-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

IF NOT EXISTS (
  SELECT
    1
  FROM
    information_schema.columns 
  WHERE TABLE_SCHEMA = current_schema AND TABLE_NAME = 'rtu_device' AND COLUMN_NAME = 'domain_info_id') THEN
	ALTER TABLE rtu_device ADD COLUMN domain_info_id bigint,
		ADD CONSTRAINT domain_info_id_constraint FOREIGN KEY (domain_info_id) REFERENCES domain_info(id);
END IF;
END;
$$
