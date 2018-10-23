
SET default_with_oids = false;
--
-- Name: scheduled_task; Type: TABLE; Schema: public; Owner: osp_admin; Tablespace: 
--

CREATE TABLE scheduled_task (
    id bigint NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    domain character varying(255),
    domain_version character varying(255),    
    correlation_uid character varying(255),
    organisation_identification character varying(255),
    device_identification character varying(255),
    message_type character varying(255),
    message_data bytea,
    scheduled_time timestamp without time zone,
    error_log character varying(255)
);

ALTER TABLE public.scheduled_task OWNER TO osp_admin;

CREATE SEQUENCE scheduled_task_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.scheduled_task_seq OWNER TO osp_admin;

ALTER SEQUENCE scheduled_task_seq OWNED BY scheduled_task.id;

ALTER TABLE ONLY scheduled_task ALTER COLUMN id SET DEFAULT nextval('scheduled_task_seq'::regclass);

ALTER TABLE ONLY scheduled_task ADD CONSTRAINT scheduled_task__pkey PRIMARY KEY (id);