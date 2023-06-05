-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

COMMENT ON COLUMN oslp_device.device_identification IS 'Business key; identification of this OSLP device.';
COMMENT ON COLUMN oslp_device.device_type IS 'The type [PSLD, SSLD] of this OSLP device.';
COMMENT ON COLUMN oslp_device.device_uid IS 'The unique ID of this OSLP device.';
COMMENT ON COLUMN oslp_device.sequence_number IS 'The last known sequence number of this OSLP device.';
COMMENT ON COLUMN oslp_device.random_device IS 'The received (from the device) random number of this OSLP device.';
COMMENT ON COLUMN oslp_device.random_platform IS 'The generated (by the protocol adapter) random number of this OSLP device.';
COMMENT ON COLUMN oslp_device.public_key IS 'The public key of this OSLP device.';
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