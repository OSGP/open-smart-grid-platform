CREATE TABLE synchronize_time_data (
    id bigint NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    device_identification character varying(17) NOT NULL
);

ALTER TABLE public.synchronize_time_data OWNER TO osp_admin;

CREATE SEQUENCE synchronize_time_data_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.synchronize_time_data_id_seq OWNER TO osp_admin;

ALTER SEQUENCE synchronize_time_data_id_seq OWNED BY synchronize_time_data.id;

ALTER TABLE ONLY synchronize_time_data ALTER COLUMN id SET DEFAULT nextval('synchronize_time_data_id_seq'::regclass);
    
ALTER TABLE ONLY synchronize_time_data
    ADD CONSTRAINT synchronize_time_data_pkey PRIMARY KEY (id);