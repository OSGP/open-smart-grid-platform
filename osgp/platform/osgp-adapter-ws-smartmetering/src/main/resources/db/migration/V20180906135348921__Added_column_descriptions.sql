COMMENT ON COLUMN response_data.organisation_identification IS 'Identification of the organisation behind the request this response is for.';
COMMENT ON COLUMN response_data.device_identification IS 'Identification of the device the response data belongs with.';
COMMENT ON COLUMN response_data.message_type IS 'Indicates which device function was executed that lead to this response.';
COMMENT ON COLUMN response_data.correlation_uid IS 'Unique identifier correlating all actions related to the request this response is for.';
COMMENT ON COLUMN response_data.message_data IS 'Serialized response object.';
COMMENT ON COLUMN response_data.result_type IS 'Message result type [OK, NOT_FOUND, NOT_OK].';
COMMENT ON COLUMN response_data.number_of_notifications_sent IS 'Number of notifications that has been sent after the initial one to notify a response is available for the request with this records correlation_uid.';

COMMENT ON COLUMN response_url.correlation_uid IS 'Unique identifier correlating all actions related to the request this response is for.';
COMMENT ON COLUMN response_url.response_url IS 'Custom notification response URL included by the sender as part of the request with this records correlation_uid.';

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
