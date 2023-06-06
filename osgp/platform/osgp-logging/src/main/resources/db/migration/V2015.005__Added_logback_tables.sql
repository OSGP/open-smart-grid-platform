
-- Table: logging_event

-- DROP TABLE logging_event;

CREATE TABLE logging_event
(
  timestmp bigint NOT NULL,
  formatted_message text NOT NULL,
  logger_name character varying(254) NOT NULL,
  level_string character varying(254) NOT NULL,
  thread_name character varying(254),
  reference_flag smallint,
  arg0 character varying(254),
  arg1 character varying(254),
  arg2 character varying(254),
  arg3 character varying(254),
  caller_filename character varying(254) NOT NULL,
  caller_class character varying(254) NOT NULL,
  caller_method character varying(254) NOT NULL,
  caller_line character(4) NOT NULL,
  event_id bigint NOT NULL
);

CREATE SEQUENCE logging_event_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
    
ALTER TABLE ONLY logging_event ALTER COLUMN event_id SET DEFAULT nextval('logging_event_id_seq'::regclass);

ALTER TABLE logging_event OWNER TO osp_admin;
  
ALTER TABLE ONLY logging_event ADD CONSTRAINT logging_event_pkey PRIMARY KEY (event_id);

-- Table: logging_event_exception

-- DROP TABLE logging_event_exception;

CREATE TABLE logging_event_exception
(
  event_id bigint NOT NULL,
  i smallint NOT NULL,
  trace_line character varying NOT NULL
);

ALTER TABLE logging_event_exception OWNER TO osp_admin;

ALTER TABLE logging_event_exception ADD CONSTRAINT logging_event_exception_pkey PRIMARY KEY (event_id, i);

ALTER TABLE logging_event_exception ADD   CONSTRAINT logging_event_exception_event_id_fkey FOREIGN KEY (event_id)
      REFERENCES logging_event (event_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;


-- Table: logging_event_property

-- DROP TABLE logging_event_property;

CREATE TABLE logging_event_property
(
  event_id bigint NOT NULL,
  mapped_key character varying(254) NOT NULL,
  mapped_value character varying(1024)

);

ALTER TABLE logging_event_property OWNER TO osp_admin;

ALTER TABLE logging_event_property ADD CONSTRAINT logging_event_property_pkey PRIMARY KEY (event_id, mapped_key);

ALTER TABLE logging_event_property ADD  CONSTRAINT logging_event_property_event_id_fkey FOREIGN KEY (event_id)
      REFERENCES logging_event (event_id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE;
