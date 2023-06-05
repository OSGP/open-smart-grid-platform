-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

COMMENT ON COLUMN smart_meter.supplier IS 'Supplier of this smart meter.';
COMMENT ON COLUMN smart_meter.channel IS 'Channel of a smart meter this device is on, if it is an M-Bus device.';
COMMENT ON COLUMN smart_meter.mbus_identification_number IS 'M-Bus identification number, if this meter is an M-Bus device.';
COMMENT ON COLUMN smart_meter.mbus_manufacturer_identification IS 'M-Bus manufacturer identification, if this meter is an M-Bus device.';
COMMENT ON COLUMN smart_meter.mbus_version IS 'M-Bus version, if this meter is an M-Bus device.';
COMMENT ON COLUMN smart_meter.mbus_device_type_identification IS 'M-Bus device type identification, if this meter is an M-Bus device.';
COMMENT ON COLUMN smart_meter.mbus_primary_address IS 'M-Bus primary address, if this meter is an M-Bus device and the primary address is set.';
