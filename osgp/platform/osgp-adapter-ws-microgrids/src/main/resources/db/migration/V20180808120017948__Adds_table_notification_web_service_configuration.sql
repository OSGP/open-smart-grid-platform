DO
$$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   pg_tables
    WHERE  schemaname = current_schema
    AND    tablename  = 'notification_web_service_configuration') THEN

    CREATE TABLE notification_web_service_configuration (
        organisation_identification VARCHAR(40) NOT NULL,
        application_name VARCHAR(40) NOT NULL,
        marshaller_context_path VARCHAR(2000) NOT NULL,
        target_uri VARCHAR(2000) NOT NULL,
        use_key_store BOOLEAN NOT NULL DEFAULT FALSE,
        key_store_type VARCHAR(40),
        key_store_location VARCHAR(2000),
        key_store_password VARCHAR(2000),
        use_trust_store BOOLEAN NOT NULL DEFAULT FALSE,
        trust_store_type VARCHAR(40),
        trust_store_location VARCHAR(2000),
        trust_store_password VARCHAR(2000),
        max_connections_per_route INTEGER NOT NULL DEFAULT 2,
        max_connections_total INTEGER NOT NULL DEFAULT 20,
        connection_timeout INTEGER NOT NULL DEFAULT 120000,
        use_circuit_breaker BOOLEAN NOT NULL DEFAULT FALSE,
        circuit_breaker_threshold INTEGER NOT NULL DEFAULT 3,
        circuit_breaker_duration_initial INTEGER NOT NULL DEFAULT 15000,
        circuit_breaker_duration_maximum INTEGER NOT NULL DEFAULT 600000,
        circuit_breaker_duration_multiplier INTEGER NOT NULL DEFAULT 4,
        PRIMARY KEY (organisation_identification, application_name)
    );

    COMMENT ON TABLE notification_web_service_configuration IS 'Configuration data for web service calls.';
    COMMENT ON COLUMN notification_web_service_configuration.organisation_identification IS 'An OSGP organisation identification. Unique in combination with application_name as this combination is the functional lookup key for the web service configuration data.';
    COMMENT ON COLUMN notification_web_service_configuration.application_name IS 'The name of an application from the organisation with this record. Unique in combination with organisation_identification as this combination is the functional lookup key for the web service configuration data.';
    COMMENT ON COLUMN notification_web_service_configuration.marshaller_context_path IS 'The context path for the JAXB marshaller used with the XML for the web service message.';
    COMMENT ON COLUMN notification_web_service_configuration.target_uri IS 'The URI where the web service for this configuration is available.';
    COMMENT ON COLUMN notification_web_service_configuration.use_key_store IS 'Indicates whether a key store is configured for use with web service calls. If true values must be provided for key_store_type, key_store_location and key_store_password.';
    COMMENT ON COLUMN notification_web_service_configuration.key_store_type IS 'The type of key store for use with web service calls. Only applied when use_key_store is TRUE.';
    COMMENT ON COLUMN notification_web_service_configuration.key_store_location IS 'The location of the key store for use with web service calls. Only applied when use_key_store is TRUE.';
    COMMENT ON COLUMN notification_web_service_configuration.key_store_password IS 'The password of the key store for use with web service calls. Only applied when use_key_store is TRUE.';
    COMMENT ON COLUMN notification_web_service_configuration.use_trust_store IS 'Indicates whether a trust store is configured for use with web service calls. If true values must be provided for trust_store_type, trust_store_location and trust_store_password.';
    COMMENT ON COLUMN notification_web_service_configuration.trust_store_type IS 'The type of trust store for use with web service calls. Only applied when use_trust_store is TRUE.';
    COMMENT ON COLUMN notification_web_service_configuration.trust_store_location IS 'The location of the trust store for use with web service calls. Only applied when use_trust_store is TRUE.';
    COMMENT ON COLUMN notification_web_service_configuration.trust_store_password IS 'The password of the trust store for use with web service calls. Only applied when use_trust_store is TRUE.';
    COMMENT ON COLUMN notification_web_service_configuration.max_connections_per_route IS 'The maximum connections per route configured on the web service client.';
    COMMENT ON COLUMN notification_web_service_configuration.max_connections_total IS 'The maximum connections total configured on the web service client.';
    COMMENT ON COLUMN notification_web_service_configuration.connection_timeout IS 'The connection timeout configured on the web service client.';
    COMMENT ON COLUMN notification_web_service_configuration.use_circuit_breaker IS 'Indicates whether a circuit breaker is configured for use with web service calls. If true values must be provided for circuit_breaker_threshold, circuit_breaker_duration_initial, circuit_breaker_duration_maximum and circuit_breaker_duration_multiplier.';
    COMMENT ON COLUMN notification_web_service_configuration.circuit_breaker_threshold IS 'The number of failures before the circuit breaker opens. Only applied when use_circuit_breaker is TRUE.';
    COMMENT ON COLUMN notification_web_service_configuration.circuit_breaker_duration_initial IS 'The number of milliseconds after which an open circuit breaker will automatically close. Only applied when use_circuit_breaker is TRUE.';
    COMMENT ON COLUMN notification_web_service_configuration.circuit_breaker_duration_maximum IS 'The maximum time in milliseconds during which the circuit breaker is open. Only applied when use_circuit_breaker is TRUE.';
    COMMENT ON COLUMN notification_web_service_configuration.circuit_breaker_duration_multiplier IS 'The multiplier for the current duration, when a call fails while the circuit breaker is half open. Only applied when use_circuit_breaker is TRUE.';

END IF;

END;
$$
