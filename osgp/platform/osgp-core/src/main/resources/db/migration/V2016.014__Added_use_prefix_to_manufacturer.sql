-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE manufacturer ADD COLUMN use_prefix boolean;

UPDATE manufacturer SET use_prefix = TRUE;

ALTER TABLE manufacturer ALTER COLUMN use_prefix SET NOT NULL;