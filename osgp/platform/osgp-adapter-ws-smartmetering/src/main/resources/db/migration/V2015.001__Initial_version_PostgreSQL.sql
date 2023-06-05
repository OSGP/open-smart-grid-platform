-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

CREATE TABLE periodic_meter_data (
    id bigint NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    device_identification character varying(17) NOT NULL
);

ALTER TABLE public.periodic_meter_data OWNER TO osp_admin;

CREATE SEQUENCE periodic_meter_data_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.periodic_meter_data_id_seq OWNER TO osp_admin;

ALTER SEQUENCE periodic_meter_data_id_seq OWNED BY periodic_meter_data.id;

ALTER TABLE ONLY periodic_meter_data ALTER COLUMN id SET DEFAULT nextval('periodic_meter_data_id_seq'::regclass);
    
ALTER TABLE ONLY periodic_meter_data
    ADD CONSTRAINT periodic_meter_data_pkey PRIMARY KEY (id);