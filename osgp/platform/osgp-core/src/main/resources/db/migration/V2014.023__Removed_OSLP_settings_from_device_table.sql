-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE device DROP COLUMN device_uid;
ALTER TABLE device DROP COLUMN sequence_number;
ALTER TABLE device DROP COLUMN random_device;
ALTER TABLE device DROP COLUMN random_platform;
ALTER TABLE device DROP COLUMN public_key;