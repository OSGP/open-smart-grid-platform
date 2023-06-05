-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN
    IF NOT EXISTS (
      SELECT 1 FROM information_schema.columns 
      WHERE TABLE_SCHEMA = current_schema AND TABLE_NAME = 'firmware_file' AND COLUMN_NAME = 'active') 
    THEN
        ALTER TABLE firmware_file ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE NOT NULL;
    END IF;
END;
$$
