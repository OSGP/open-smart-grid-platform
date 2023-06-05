-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

CREATE TABLE relay_status (
    id bigint NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    index smallint NOT NULL,
    last_known_state boolean NOT NULL,
    last_know_switching_time timestamp without time zone NOT NULL,
    device_id bigint NOT NULL
);

ALTER TABLE public.relay_status OWNER TO osp_admin;

CREATE SEQUENCE relay_status_id_seq
    START WITH 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.relay_status_id_seq OWNER TO osp_admin;

ALTER SEQUENCE relay_status_id_seq OWNED BY relay_status.id;

ALTER TABLE ONLY relay_status ALTER COLUMN id SET DEFAULT nextval('relay_status_id_seq'::regclass);

ALTER TABLE ONLY relay_status
    ADD CONSTRAINT relay_status_pkey PRIMARY KEY (id);
    
GRANT ALL ON TABLE relay_status TO osp_admin;
GRANT SELECT ON TABLE relay_status TO osgp_read_only_ws_user;