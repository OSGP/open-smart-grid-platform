-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

-- Create table
CREATE TABLE domain_info(
	id bigint NOT NULL,
	creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    domain character varying(255) NOT NULL,
    domain_version character varying(255) NOT NULL,
    requests_queue character varying(255) NOT NULL,
    responses_queue character varying(255) NOT NULL
);

ALTER TABLE public.domain_info OWNER TO osp_admin;

CREATE SEQUENCE domain_info_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.domain_info_id_seq OWNER TO osp_admin;

ALTER SEQUENCE domain_info_id_seq OWNED BY domain_info.id;

ALTER TABLE ONLY domain_info ALTER COLUMN id SET DEFAULT nextval('domain_info_id_seq'::regclass);

ALTER TABLE ONLY domain_info ADD CONSTRAINT domain_info_pkey PRIMARY KEY (id);