-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

COMMENT ON COLUMN iec61850_device.device_identification IS 'Business key; identification of this IEC61850 device.';
COMMENT ON COLUMN iec61850_device.icd_filename IS 'The name of the ICD file / server model of this IEC61850 device.';
COMMENT ON COLUMN iec61850_device.port IS 'The port of this IEC61850 device.';
COMMENT ON COLUMN iec61850_device.server_name IS 'The logical device name of this IEC61850 device.';
COMMENT ON COLUMN iec61850_device.enable_all_reports_on_connect IS 'Indicates whether or not all reporting control blocks will be enabled when connecting to this IEC61850 device.';
--COMMENT ON COLUMN iec61850_device.use_combined_load IS 'Not used.';
--COMMENT ON COLUMN iec61850_device_report_group.device_identification IS 'Not used.';
--COMMENT ON COLUMN iec61850_device_report_group.report_group_id IS 'Not used.';
--COMMENT ON COLUMN iec61850_device_report_group.enabled IS 'Not used.';
--COMMENT ON COLUMN iec61850_device_report_group.report_data_set IS 'Not used.';
--COMMENT ON COLUMN iec61850_device_report_group.domain IS 'Not used.';
--COMMENT ON COLUMN iec61850_device_report_group.domain_version IS 'Not used.';
--COMMENT ON COLUMN iec61850_last_report_entry.device_identification IS 'Not used.';
--COMMENT ON COLUMN iec61850_last_report_entry.report_id IS 'Not used.';
--COMMENT ON COLUMN iec61850_last_report_entry.entry_id IS 'Not used.';
--COMMENT ON COLUMN iec61850_last_report_entry.time_of_entry IS 'Not used.';
--COMMENT ON COLUMN iec61850_report.logical_device IS 'Not used.';
--COMMENT ON COLUMN iec61850_report.logical_node IS 'Not used.';
--COMMENT ON COLUMN iec61850_report.name IS 'Not used.';
--COMMENT ON COLUMN iec61850_report_group.name IS 'Not used.';
--COMMENT ON COLUMN iec61850_report_report_group.report_id IS 'Not used.';
--COMMENT ON COLUMN iec61850_report_report_group.report_group_id IS 'Not used.';
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