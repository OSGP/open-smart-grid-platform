CREATE USER osgp_reporting WITH PASSWORD '1234' NOSUPERUSER;

CREATE SCHEMA osgp_reporting AUTHORIZATION osgp_reporting;

-- Create a table to record each call to the platform.
-- Also creates a sequence and constraint for the new table.
CREATE TABLE osgp_reporting.platform_calls(
    id bigint NOT NULL,
    organisation_identification character varying(255),
    domain character varying(255) NOT NULL,
    class_name character varying(255) NOT NULL,
    method character varying(255) NOT NULL,
    device_identification character varying(40), 
    creation_time timestamp without time zone NOT NULL
);

ALTER TABLE osgp_reporting.platform_calls OWNER TO osgp_reporting;

CREATE SEQUENCE osgp_reporting.platform_calls_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE osgp_reporting.platform_calls_id_seq OWNER TO osgp_reporting;
ALTER SEQUENCE osgp_reporting.platform_calls_id_seq OWNED BY osgp_reporting.platform_calls.id;

ALTER TABLE ONLY osgp_reporting.platform_calls ALTER COLUMN id 
  SET DEFAULT nextval('osgp_reporting.platform_calls_id_seq'::regclass);

ALTER TABLE ONLY osgp_reporting.platform_calls ADD 
  CONSTRAINT platform_calls_pkey PRIMARY KEY (id);

CREATE INDEX platform_calls_i1 ON osgp_reporting.platform_calls(creation_time);

-- Create a table to record each call to a device.
-- Also creates a sequence and constraint for the new table.
CREATE TABLE osgp_reporting.device_calls(
    id bigint NOT NULL,
    device_identification character varying(40),
    message character varying(255) NOT NULL,
    bytes_in integer NOT NULL,
    bytes_out integer NOT NULL,
    creation_time timestamp without time zone NOT NULL
);

ALTER TABLE osgp_reporting.device_calls OWNER TO osgp_reporting;

CREATE SEQUENCE osgp_reporting.device_calls_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE osgp_reporting.device_calls_id_seq OWNER TO osgp_reporting;
ALTER SEQUENCE osgp_reporting.device_calls_id_seq OWNED BY osgp_reporting.device_calls.id;

ALTER TABLE ONLY osgp_reporting.device_calls ALTER COLUMN id 
  SET DEFAULT nextval('osgp_reporting.device_calls_id_seq'::regclass);

ALTER TABLE ONLY osgp_reporting.device_calls ADD 
  CONSTRAINT device_calls_pkey PRIMARY KEY (id);

CREATE INDEX device_calls_i1 ON osgp_reporting.device_calls(creation_time);
