-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

COMMENT ON COLUMN dlms_device.device_identification IS 'Business key; identification of this device.';
COMMENT ON COLUMN dlms_device.icc_id IS 'International integrated circuit card identifier of the SIM chip of this device.';
COMMENT ON COLUMN dlms_device.communication_provider IS 'Communication provider for this device.';
COMMENT ON COLUMN dlms_device.communication_method IS 'Communication method with this device.';
COMMENT ON COLUMN dlms_device.hls3active IS 'Indicates whether or not HLS3 is active on this device.';
COMMENT ON COLUMN dlms_device.hls4active IS 'Indicates whether or not HLS4 is active on this device.';
COMMENT ON COLUMN dlms_device.hls5active IS 'Indicates whether or not HLS5 is active on this device.';
COMMENT ON COLUMN dlms_device.challenge_length IS 'Challenge length used while setting up the DLMS connection with this device.';
COMMENT ON COLUMN dlms_device.with_list_supported IS 'Indicates whether or not with-list requests are supported on this device.';
COMMENT ON COLUMN dlms_device.selective_access_supported IS 'Indicates whether or not filtering capture objects with selective access requests is supported on this device.';
COMMENT ON COLUMN dlms_device.ip_address_is_static IS 'Indicates whether or not the device has a static IP address.';
COMMENT ON COLUMN dlms_device.port IS 'Port number for the remote TCP connection to the device.';
COMMENT ON COLUMN dlms_device.client_id IS 'Client ID used to connect to the device.';
COMMENT ON COLUMN dlms_device.logical_id IS 'Logical device ID used to connect to the device.';
COMMENT ON COLUMN dlms_device.in_debug_mode IS 'Indicates whether or not debug logging of device communication is enabled.';
COMMENT ON COLUMN dlms_device.use_sn IS 'Indicates whether or not Short Naming is used for the device communication.';
COMMENT ON COLUMN dlms_device.use_hdlc IS 'Indicates whether or not HDLC is used for the device communication.';
COMMENT ON COLUMN dlms_device.lls1active IS 'Indicates whether or not LLS1 is active on this device.';
COMMENT ON COLUMN dlms_device.mbus_identification_number IS 'M-Bus identification number, in case the device is used as an M-Bus device.';
COMMENT ON COLUMN dlms_device.mbus_manufacturer_identification IS 'M-Bus Manufacturer identification.';

COMMENT ON COLUMN schema_version.installed_rank IS 'Installed rank indicates the order of applied migrations. Used by Flyway.';
COMMENT ON COLUMN schema_version.version IS 'Version indicates the numbered prefix of migration files. Used by Flyway.';
COMMENT ON COLUMN schema_version.description IS 'Description indicates the name of migration files. Used by Flyway.';
COMMENT ON COLUMN schema_version.type IS 'Type can be SQL in case of SQL migration files or JDBC in case of migrating programmatically using Java. Used by Flyway.';
COMMENT ON COLUMN schema_version.script IS 'Full name of the migration script; version and description are derived from the full name. Used by Flyway.';
COMMENT ON COLUMN schema_version.checksum IS 'Hash of the content of a migration script. Used by Flyway.';
COMMENT ON COLUMN schema_version.installed_by IS 'User name of the database user who has run the migration script. Used by Flyway.';
COMMENT ON COLUMN schema_version.installed_on IS 'Timestamp indicating when the migration has been applied. Used by Flyway.';
COMMENT ON COLUMN schema_version.execution_time IS 'Duration of the migration in milliseconds. Used by Flyway.';
COMMENT ON COLUMN schema_version.success IS 'State indicating whether or not the migration was successfully applied. Used by Flyway.';

COMMENT ON COLUMN security_key.dlms_device_id IS 'Primary key of the DLMS device this key belongs with.';
COMMENT ON COLUMN security_key.security_key_type IS 'Type of key [E_METER_MASTER, E_METER_AUTHENTICATION, E_METER_ENCRYPTION, G_METER_MASTER, G_METER_ENCRYPTION, PASSWORD].';
COMMENT ON COLUMN security_key.valid_from IS 'The time before which this key is not valid.';
COMMENT ON COLUMN security_key.valid_to IS 'The time after which this key is not valid.';
COMMENT ON COLUMN security_key.security_key IS 'Encrypted value of this security key.';
COMMENT ON COLUMN security_key.invocation_counter IS 'The value of the invocation counter for DLMS communication using this key.';
