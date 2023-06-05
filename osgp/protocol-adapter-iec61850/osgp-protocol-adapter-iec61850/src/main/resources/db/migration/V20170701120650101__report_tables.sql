-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO $$
BEGIN

IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=current_schema AND table_name = 'iec61850_report') THEN
  CREATE TABLE IF NOT EXISTS iec61850_report (
    id bigint NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    logical_device character varying(255) NOT NULL,
    logical_node character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    CONSTRAINT iec61850_report_pkey PRIMARY KEY (id),
    CONSTRAINT iec61850_report_ukey_name UNIQUE (name)
  );

  ALTER TABLE iec61850_report OWNER TO osp_admin;

  CREATE SEQUENCE iec61850_report_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

  ALTER TABLE public.iec61850_report_seq OWNER TO osp_admin;

  ALTER SEQUENCE iec61850_report_seq OWNED BY iec61850_report.id;

  ALTER TABLE ONLY iec61850_report ALTER COLUMN id SET DEFAULT nextval('iec61850_report_seq'::regclass);
END IF;

IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=current_schema AND table_name = 'iec61850_report_group') THEN
  CREATE TABLE IF NOT EXISTS iec61850_report_group (
    id bigint NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    name character varying(255) NOT NULL,
    CONSTRAINT iec61850_report_group_pkey PRIMARY KEY (id),
    CONSTRAINT iec61850_report_group_ukey_name UNIQUE (name)
  );

  ALTER TABLE iec61850_report_group OWNER TO osp_admin;

  CREATE SEQUENCE iec61850_report_group_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

  ALTER TABLE public.iec61850_report_group_seq OWNER TO osp_admin;

  ALTER SEQUENCE iec61850_report_group_seq OWNED BY iec61850_report_group.id;

  ALTER TABLE ONLY iec61850_report_group ALTER COLUMN id SET DEFAULT nextval('iec61850_report_group_seq'::regclass);
END IF;

IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=current_schema AND table_name = 'iec61850_report_report_group') THEN
  CREATE TABLE IF NOT EXISTS iec61850_report_report_group (
    id bigint NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    report_id bigint NOT NULL,
    report_group_id bigint NOT NULL,
    CONSTRAINT iec61850_report_report_group_pkey PRIMARY KEY (id),
    CONSTRAINT iec61850_report_report_group_ukey UNIQUE (report_id, report_group_id)
  );

  ALTER TABLE iec61850_report_report_group OWNER TO osp_admin;

  CREATE SEQUENCE iec61850_report_report_group_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

  ALTER TABLE public.iec61850_report_report_group_seq OWNER TO osp_admin;

  ALTER SEQUENCE iec61850_report_report_group_seq OWNED BY iec61850_report_report_group.id;

  ALTER TABLE ONLY iec61850_report_report_group ALTER COLUMN id SET DEFAULT nextval('iec61850_report_report_group_seq'::regclass);
END IF;

IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=current_schema AND table_name = 'iec61850_device_report_group') THEN
  CREATE TABLE IF NOT EXISTS iec61850_device_report_group (
    id bigint NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    device_identification character varying(40) NOT NULL,
    report_group_id bigint NOT NULL,
    enabled boolean NOT NULL,
    report_data_set character varying(255) NOT NULL,
    domain character varying(255) NOT NULL,
    domain_version character varying(255) NOT NULL,
    CONSTRAINT iec61850_device_report_group_pkey PRIMARY KEY (id),
    CONSTRAINT iec61850_device_report_group_ukey UNIQUE (device_identification, report_group_id),
    CONSTRAINT iec61850_device_report_group_fkey_report_group FOREIGN KEY (report_group_id)
      REFERENCES iec61850_report_group (id)
  );

  ALTER TABLE iec61850_device_report_group OWNER TO osp_admin;

  CREATE SEQUENCE iec61850_device_report_group_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


  ALTER TABLE public.iec61850_device_report_group_seq OWNER TO osp_admin;

  ALTER SEQUENCE iec61850_device_report_group_seq OWNED BY iec61850_device_report_group.id;

  ALTER TABLE ONLY iec61850_device_report_group ALTER COLUMN id SET DEFAULT nextval('iec61850_device_report_group_seq'::regclass);
END IF;

END;
$$