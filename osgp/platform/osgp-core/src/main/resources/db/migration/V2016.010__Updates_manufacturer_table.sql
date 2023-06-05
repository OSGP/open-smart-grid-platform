-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE manufacturer ALTER COLUMN code TYPE character varying(4);
ALTER TABLE manufacturer ALTER COLUMN name TYPE character varying(50);

ALTER TABLE manufacturer ALTER COLUMN name SET NOT NULL;

ALTER TABLE device_model ALTER COLUMN code TYPE character varying(4);

