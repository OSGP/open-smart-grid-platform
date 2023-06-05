-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

CREATE TABLE device_model_firmware (
    id bigint NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    filename character varying(255),
    model_code character varying(15),
    description character varying(255),
    push_to_new_devices boolean,
    module_version_comm character varying(100),
    module_version_func character varying(100),
    module_version_ma character varying(100),
    module_version_mbus character varying(100),
    module_version_sec character varying(100),
    device_model bigint
);

ALTER TABLE public.device_model_firmware OWNER TO osp_admin;

--
-- Name: device_model_firmware_id_seq; Type: SEQUENCE; Schema: public; Owner: osp_admin
--

CREATE SEQUENCE device_model_firmware_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.device_model_firmware_id_seq OWNER TO osp_admin;

ALTER SEQUENCE device_model_firmware_id_seq OWNED BY device_model_firmware.id;
 
ALTER TABLE ONLY device_model_firmware ALTER COLUMN id SET DEFAULT nextval('device_model_firmware_id_seq'::regclass);

ALTER TABLE ONLY device_model_firmware
    ADD CONSTRAINT device_model_firmware_pkey PRIMARY KEY (id);

ALTER TABLE ONLY device_model_firmware
    ADD CONSTRAINT fk7e0c025199350fa1 FOREIGN KEY (device_model) REFERENCES device_model(id);
    
GRANT ALL ON TABLE device_model_firmware TO osp_admin;
GRANT SELECT ON TABLE device_model_firmware TO osgp_read_only_ws_user;