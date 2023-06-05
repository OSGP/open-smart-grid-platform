-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

-- Create table
CREATE TABLE device_protocol_info(
	id bigint NOT NULL,
	creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    protocol character varying(255) NOT NULL,
    protocol_version character varying(255) NOT NULL,
    requests_queue character varying(255) NOT NULL,
    responses_queue character varying(255) NOT NULL
);

ALTER TABLE public.device_protocol_info OWNER TO osp_admin;

CREATE SEQUENCE device_protocol_info_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.device_protocol_info_id_seq OWNER TO osp_admin;

ALTER SEQUENCE device_protocol_info_id_seq OWNED BY device_protocol_info.id;

ALTER TABLE ONLY device_protocol_info ALTER COLUMN id SET DEFAULT nextval('device_protocol_info_id_seq'::regclass);

ALTER TABLE ONLY device_protocol_info ADD CONSTRAINT device_protocol_info_pkey PRIMARY KEY (id);