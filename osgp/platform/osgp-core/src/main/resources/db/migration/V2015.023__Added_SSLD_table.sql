-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

CREATE TABLE ssld (
    id bigserial NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    has_public_key boolean NOT NULL,
    has_schedule boolean NOT NULL
);

ALTER TABLE public.ssld OWNER TO osp_admin;

ALTER TABLE public.ssld_id_seq OWNER TO osp_admin;

ALTER SEQUENCE ssld_id_seq OWNED BY ssld.id;

ALTER TABLE ONLY ssld ALTER COLUMN id SET DEFAULT nextval('ssld_id_seq'::regclass);

ALTER TABLE ONLY ssld ADD CONSTRAINT ssld_pkey PRIMARY KEY (id);