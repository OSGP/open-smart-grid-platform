-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

CREATE TABLE smart_metering_device(
    id bigserial NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    device_identification character varying(40) NOT NULL,
    device_type character varying(255),
    supplier character varying(50),
    container_city character varying(255),
    container_street character varying(255),
    gps_latitude character varying(15),
    gps_longitude character varying(15),
    container_postal_code character varying(10),
    container_number character varying(10),
    protocol_info_id bigint
);

ALTER TABLE public.smart_metering_device OWNER TO osp_admin;

ALTER TABLE public.smart_metering_device_id_seq OWNER TO osp_admin;

ALTER SEQUENCE smart_metering_device_id_seq OWNED BY smart_metering_device.id;

ALTER TABLE ONLY smart_metering_device ALTER COLUMN id SET DEFAULT nextval('smart_metering_device_id_seq'::regclass);

ALTER TABLE ONLY smart_metering_device ADD CONSTRAINT smart_metering_device_pkey PRIMARY KEY (id);