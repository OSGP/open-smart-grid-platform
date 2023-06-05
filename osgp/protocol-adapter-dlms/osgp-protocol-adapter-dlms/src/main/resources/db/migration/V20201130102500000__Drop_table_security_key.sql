-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

  IF EXISTS(
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = current_schema AND table_name = 'security_key') THEN
      DROP TABLE security_key;
  END IF;

END;
$$
