-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE device ADD COLUMN manufacturer_id integer;
UPDATE device SET manufacturer_id=1;
-- The manufacturer_id column is used to populate 2 bytes in the OslpEnvelope.
-- Range is a 16 bit unsigened integer, from 0 to 65535.