-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

CREATE TABLE ean (
    id bigint NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    code character varying(255) NOT NULL,
    description character varying(255),
    device bigint
);

ALTER TABLE public.ean OWNER TO osp_admin;

--
-- Name: ean_id_seq; Type: SEQUENCE; Schema: public; Owner: osp_admin
--

CREATE SEQUENCE ean_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.ean_id_seq OWNER TO osp_admin;

ALTER SEQUENCE ean_id_seq OWNED BY ean.id;
 
ALTER TABLE ONLY ean ALTER COLUMN id SET DEFAULT nextval('ean_id_seq'::regclass);

ALTER TABLE ONLY ean
    ADD CONSTRAINT ean_pkey PRIMARY KEY (id);

ALTER TABLE ONLY ean
    ADD CONSTRAINT fk7e0c025199350fa2 FOREIGN KEY (device) REFERENCES device(id);
    
GRANT ALL ON TABLE ean TO osp_admin;
GRANT SELECT ON TABLE ean TO osgp_read_only_ws_user;