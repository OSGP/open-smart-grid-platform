-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE device_output_setting ADD COLUMN alias character varying(255);

ALTER TABLE device_output_setting ADD COLUMN relay_function smallint;

