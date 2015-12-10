CREATE TABLE gasmeter_device (
    id bigserial NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    device_identification character varying(40) NOT NULL unique,
    smart_meter_id character varying(40) NOT NULL,
    wired boolean not null,
    channel smallint not null
);

ALTER TABLE public.gasmeter_device OWNER TO osp_admin;

ALTER TABLE public.gasmeter_device_id_seq OWNER TO osp_admin;

ALTER SEQUENCE gasmeter_device_id_seq OWNED BY smart_metering_device.id;

ALTER TABLE ONLY gasmeter_device ALTER COLUMN id SET DEFAULT nextval('gas_meter_device_id_seq'::regclass);

ALTER TABLE ONLY gasmeter_device ADD CONSTRAINT gasmeter_device_pkey PRIMARY KEY (id);