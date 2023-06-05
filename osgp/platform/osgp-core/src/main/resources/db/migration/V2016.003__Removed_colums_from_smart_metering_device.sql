-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE smart_metering_device DROP COLUMN IF EXISTS creation_time;
ALTER TABLE smart_metering_device DROP COLUMN IF EXISTS modification_time;
ALTER TABLE smart_metering_device DROP COLUMN IF EXISTS version;
ALTER TABLE smart_metering_device DROP COLUMN IF EXISTS device_type;
ALTER TABLE smart_metering_device DROP COLUMN IF EXISTS container_city;
ALTER TABLE smart_metering_device DROP COLUMN IF EXISTS container_street;
ALTER TABLE smart_metering_device DROP COLUMN IF EXISTS gps_latitude;
ALTER TABLE smart_metering_device DROP COLUMN IF EXISTS gps_longitude;
ALTER TABLE smart_metering_device DROP COLUMN IF EXISTS container_postal_code;
ALTER TABLE smart_metering_device DROP COLUMN IF EXISTS container_number;
ALTER TABLE smart_metering_device DROP COLUMN IF EXISTS protocol_info_id;
ALTER TABLE smart_metering_device DROP COLUMN IF EXISTS device_identification;
