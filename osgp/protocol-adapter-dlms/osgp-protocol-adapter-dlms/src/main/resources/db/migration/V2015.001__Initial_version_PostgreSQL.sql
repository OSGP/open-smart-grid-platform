-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

CREATE TABLE dlms_device (
    id bigint NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    device_identification character varying(40) NOT NULL,
    version bigint
);

ALTER TABLE public.dlms_device OWNER TO osp_admin;

CREATE SEQUENCE dlms_device_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.dlms_device_id_seq OWNER TO osp_admin;

ALTER SEQUENCE dlms_device_id_seq OWNED BY dlms_device.id;

ALTER TABLE ONLY dlms_device ALTER COLUMN id SET DEFAULT nextval('dlms_device_id_seq'::regclass);

ALTER TABLE ONLY dlms_device
    ADD CONSTRAINT dlms_device_device_identification_key UNIQUE (device_identification);
    
ALTER TABLE ONLY dlms_device
    ADD CONSTRAINT dlms_device_pkey PRIMARY KEY (id);