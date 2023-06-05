-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE device ADD COLUMN protocol character varying(255);

-- set protocol initial to OSLP
UPDATE device SET protocol = 'OSLP';