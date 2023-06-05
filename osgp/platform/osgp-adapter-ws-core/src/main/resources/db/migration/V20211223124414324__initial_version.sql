-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

CREATE TABLE response_data (
     id bigint NOT NULL,
     creation_time timestamp without time zone NOT NULL,
     modification_time timestamp without time zone NOT NULL,
     version bigint,
     organisation_identification character varying(255),
     device_identification character varying(255),
     message_type character varying(255),
     correlation_uid character varying(255),
     message_data bytea,
     result_type character varying(255),
     number_of_notifications_sent smallint NOT NULL DEFAULT(0)
);
ALTER TABLE response_data OWNER TO osp_admin;

CREATE SEQUENCE response_data_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE response_data_seq OWNER TO osp_admin;
ALTER SEQUENCE response_data_seq OWNED BY response_data.id;

ALTER TABLE ONLY response_data ALTER COLUMN id SET DEFAULT nextval('response_data_seq'::regclass);
ALTER TABLE ONLY response_data ADD CONSTRAINT response_data_pkey PRIMARY KEY (id);

CREATE UNIQUE INDEX ON response_data (correlation_uid);

COMMENT ON COLUMN response_data.organisation_identification IS 'Identification of the organisation behind the request this response is for.';
COMMENT ON COLUMN response_data.device_identification IS 'Identification of the device the response data belongs with.';
COMMENT ON COLUMN response_data.message_type IS 'Indicates which device function was executed that leads to this response.';
COMMENT ON COLUMN response_data.correlation_uid IS 'Unique identifier correlating all actions related to the request this response is for.';
COMMENT ON COLUMN response_data.message_data IS 'Serialized response object.';
COMMENT ON COLUMN response_data.result_type IS 'Message result type [OK, NOT_FOUND, NOT_OK].';
COMMENT ON COLUMN response_data.number_of_notifications_sent IS 'Number of notifications that has been sent after the initial one to notify a response is available for the request with this records correlation_uid.';


CREATE TABLE response_url (
      id bigint NOT NULL,
      creation_time timestamp without time zone NOT NULL,
      modification_time timestamp without time zone NOT NULL,
      version bigint,
      correlation_uid character varying(255),
      response_url character varying(255)
);

ALTER TABLE response_url OWNER TO osp_admin;

CREATE SEQUENCE response_url_seq
        START WITH 1
        INCREMENT BY 1
        NO MINVALUE
        NO MAXVALUE
        CACHE 1;
ALTER SEQUENCE response_url_seq OWNER TO osp_admin;
ALTER SEQUENCE response_url_seq OWNED BY response_url.id;
ALTER TABLE ONLY response_url ALTER COLUMN id SET DEFAULT nextval('response_url_seq'::regclass);
ALTER TABLE ONLY response_url ADD CONSTRAINT response_url_pkey PRIMARY KEY (id);
CREATE INDEX resonse_url_correlation_uid_idx ON response_url (correlation_uid);

COMMENT ON COLUMN response_url.correlation_uid IS 'Unique identifier correlating all actions related to the request this response is for.';
COMMENT ON COLUMN response_url.response_url IS 'Custom notification response URL included by the sender as part of the request with this records correlation_uid.';


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


END;
$$
