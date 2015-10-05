CREATE TABLE synchronize_time_reads (
    id bigint NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    correlation_uid character varying(255),
    organisation_identification character varying(255),
    device_identification character varying(255),
    message_type character varying(255)
);

ALTER TABLE public.synchronize_time_reads OWNER TO osp_admin;

CREATE SEQUENCE synchronize_time_reads_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.synchronize_time_reads_id_seq OWNER TO osp_admin;

ALTER SEQUENCE synchronize_time_reads_id_seq OWNED BY synchronize_time_reads.id;

ALTER TABLE ONLY synchronize_time_reads ALTER COLUMN id SET DEFAULT nextval('synchronize_time_reads_id_seq'::regclass);
    
ALTER TABLE ONLY synchronize_time_reads
    ADD CONSTRAINT synchronize_time_reads_pkey PRIMARY KEY (id);