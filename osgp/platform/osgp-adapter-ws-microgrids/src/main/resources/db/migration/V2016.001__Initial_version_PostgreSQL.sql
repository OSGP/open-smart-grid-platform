CREATE TABLE rtu_response_data (
    id bigint NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    organisation_identification character varying(255),
    device_identification character varying(255),
    message_type character varying(255),
    correlation_uid character varying(255),
    message_data bytea,
    result_type character varying(255)
);

ALTER TABLE public.rtu_response_data OWNER TO osp_admin;

CREATE SEQUENCE rtu_response_data_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.rtu_response_data_seq OWNER TO osp_admin;

ALTER SEQUENCE rtu_response_data_seq OWNED BY rtu_response_data.id;

ALTER TABLE ONLY rtu_response_data ALTER COLUMN id SET DEFAULT nextval('rtu_response_data_seq'::regclass);

ALTER TABLE ONLY rtu_response_data ADD CONSTRAINT rtu_response_data_pkey PRIMARY KEY (id);
