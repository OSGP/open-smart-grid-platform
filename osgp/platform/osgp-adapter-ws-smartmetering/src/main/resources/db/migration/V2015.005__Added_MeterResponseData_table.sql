-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

SET default_with_oids = false;

CREATE TABLE meter_response_data (
    id bigint NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    organisation_identification character varying(255),
    device_identification character varying(255),
    message_type character varying(255),
    correlation_uid character varying(255),
    message_data bytea
);

ALTER TABLE public.meter_response_data OWNER TO osp_admin;

CREATE SEQUENCE meter_response_data_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
    
ALTER TABLE public.meter_response_data_seq OWNER TO osp_admin;

ALTER SEQUENCE meter_response_data_seq OWNED BY meter_response_data.id;

ALTER TABLE ONLY meter_response_data ALTER COLUMN id SET DEFAULT nextval('meter_response_data_seq'::regclass);

ALTER TABLE ONLY meter_response_data ADD CONSTRAINT meter_response_data_pkey PRIMARY KEY (id);
