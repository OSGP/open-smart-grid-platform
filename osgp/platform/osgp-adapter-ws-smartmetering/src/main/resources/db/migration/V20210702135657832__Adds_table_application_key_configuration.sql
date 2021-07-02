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
        public_key_location VARCHAR(2000),
        PRIMARY KEY (organisation_identification, application_name)
    );

END IF;

END;
$$
