-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE device ADD COLUMN sequence_number integer;
ALTER TABLE device ADD COLUMN random_device integer;
ALTER TABLE device ADD COLUMN random_platform integer;