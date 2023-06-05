-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO $$
BEGIN

IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema=current_schema
    AND table_name = 'security_key'
    AND column_name='invocation_counter'
) THEN
    ALTER TABLE ONLY security_key ADD COLUMN invocation_counter INTEGER;
END IF;

END;
$$
