-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

IF  EXISTS (SELECT 1 FROM information_schema.columns
  WHERE table_schema=current_schema AND table_name = 'encrypted_secret' AND column_name = 'encoded_secret') THEN

    ALTER TABLE "encrypted_secret" ALTER COLUMN "encoded_secret" type VARCHAR(96);

END IF;

END;
$$