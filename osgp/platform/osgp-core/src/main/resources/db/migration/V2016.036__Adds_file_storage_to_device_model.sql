-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE device_model ADD COLUMN file_storage boolean;

UPDATE device_model SET file_storage=TRUE;

ALTER TABLE device_model ALTER COLUMN file_storage SET NOT NULL;
