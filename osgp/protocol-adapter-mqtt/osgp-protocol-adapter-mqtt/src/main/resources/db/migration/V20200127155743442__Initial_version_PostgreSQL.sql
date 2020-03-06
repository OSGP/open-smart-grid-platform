CREATE TABLE mqtt_device (
    id bigint NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    device_identification character varying(40) NOT NULL,
    host character varying(255),
    port integer,
    topics character varying(255),
    qos character varying(255),
    version bigint
);

ALTER TABLE public.mqtt_device OWNER TO osp_admin;

CREATE SEQUENCE mqtt_device_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.mqtt_device_id_seq OWNER TO osp_admin;

ALTER SEQUENCE mqtt_device_id_seq OWNED BY mqtt_device.id;

ALTER TABLE ONLY mqtt_device ALTER COLUMN id SET DEFAULT nextval('mqtt_device_id_seq'::regclass);

ALTER TABLE ONLY mqtt_device
    ADD CONSTRAINT mqtt_device_device_identification_key UNIQUE (device_identification);
    
ALTER TABLE ONLY mqtt_device
    ADD CONSTRAINT mqtt_device_pkey PRIMARY KEY (id);