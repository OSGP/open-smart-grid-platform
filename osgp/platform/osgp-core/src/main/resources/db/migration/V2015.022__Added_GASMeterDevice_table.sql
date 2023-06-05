-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

CREATE TABLE gas_meter_device (
    id bigserial NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    device_identification character varying(40) NOT NULL unique,
    smart_meter_id character varying(40) NOT NULL,
    channel smallint not null
);

ALTER TABLE public.gas_meter_device OWNER TO osp_admin;

ALTER TABLE public.gas_meter_device_id_seq OWNER TO osp_admin;

ALTER SEQUENCE gas_meter_device_id_seq OWNED BY smart_metering_device.id;

ALTER TABLE ONLY gas_meter_device ALTER COLUMN id SET DEFAULT nextval('gas_meter_device_id_seq'::regclass);

ALTER TABLE ONLY gas_meter_device ADD CONSTRAINT gas_meter_device_pkey PRIMARY KEY (id);