DO
$$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   pg_tables
    WHERE  schemaname = current_schema
    AND    tablename  = 'application_key_configuration') THEN

    CREATE TABLE application_key_configuration (
        organisation_identification VARCHAR(40) NOT NULL,
        application_name VARCHAR(40) NOT NULL,
        public_key_location VARCHAR(2000) NOT NULL,
        PRIMARY KEY (organisation_identification, application_name)
    );

    COMMENT ON TABLE application_key_configuration IS 'Configuration data for public keys of applications using GXF.';
    COMMENT ON COLUMN application_key_configuration.organisation_identification IS 'A GXF organisation identification. Unique in combination with application_name as this combination is the functional lookup key for the application key configuration data.';
    COMMENT ON COLUMN application_key_configuration.application_name IS 'The name of an application from the organisation with this record. Unique in combination with organisation_identification as this combination is the functional lookup key for the application key configuration data.';
    COMMENT ON COLUMN application_key_configuration.public_key_location IS 'The location of the public key used for encrypting data to the specific application.';

END IF;

END;
$$
