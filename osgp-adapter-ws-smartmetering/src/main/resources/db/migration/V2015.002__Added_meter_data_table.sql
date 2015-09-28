CREATE TABLE meter_data (
    id bigint NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    periodic_meter_data_id bigint NOT NULL,
    log_time timestamp without time zone NOT NULL,
    active_energy_import_tariff_one  bigint NOT NULL,
    active_energy_import_tariff_two  bigint NOT NULL,
    active_energy_export_tariff_one  bigint NOT NULL,
    active_energy_export_tariff_two  bigint NOT NULL
);

ALTER TABLE public.meter_data OWNER TO osp_admin;

CREATE SEQUENCE meter_data_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.meter_data_id_seq OWNER TO osp_admin;

ALTER SEQUENCE meter_data_id_seq OWNED BY meter_data.id;

ALTER TABLE ONLY meter_data ALTER COLUMN id SET DEFAULT nextval('meter_data_id_seq'::regclass);
    
ALTER TABLE ONLY meter_data
    ADD CONSTRAINT meter_data_pkey PRIMARY KEY (id);