-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

CREATE TABLE manufacturer
(
  code character varying(3) NOT NULL,
  name character varying(255),
  CONSTRAINT manufacturer_pkey PRIMARY KEY (code)
);

ALTER TABLE public.manufacturer OWNER TO osp_admin;

CREATE TABLE device_model
(
  id bigserial NOT NULL,
  creation_time timestamp without time zone NOT NULL,
  modification_time timestamp without time zone NOT NULL,
  version bigint,
  manufacturer character varying(3) NOT NULL,
  code character varying(10) NOT NULL,
  name character varying(255),
  CONSTRAINT model_pkey PRIMARY KEY (id),
  CONSTRAINT model_manufacturer_fkey FOREIGN KEY (manufacturer)
      REFERENCES manufacturer (code)
);

ALTER TABLE public.device_model OWNER TO osp_admin;

CREATE SEQUENCE device_model_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.device_model_id_sequence OWNER TO osp_admin;

ALTER SEQUENCE device_model_id_sequence OWNED BY device_model.id;

CREATE TABLE firmware
(
  id bigserial NOT NULL,
  creation_time timestamp without time zone NOT NULL,
  modification_time timestamp without time zone NOT NULL,
  version bigint,
  device_model bigint NOT NULL,
  firmware_version integer NOT NULL,
  description character varying(255),
  installation_file bytea[],
  CONSTRAINT firmware_pkey PRIMARY KEY (id),
  CONSTRAINT firmware_model_fkey FOREIGN KEY (device_model)
    REFERENCES device_model (id)

);

ALTER TABLE public.firmware OWNER TO osp_admin;

CREATE SEQUENCE firmware_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.firmware_id_sequence OWNER TO osp_admin;

ALTER SEQUENCE firmware_id_sequence OWNED BY firmware.id;

ALTER TABLE public.device ADD COLUMN firmware bigint;
ALTER TABLE public.device ADD CONSTRAINT device_firmware_fkey FOREIGN KEY (firmware)
    REFERENCES firmware (id);

CREATE TABLE firmware_history
(
  device bigint NOT NULL,
  firmware bigint NOT NULL,
  installation_date date NOT NULL,
  installed_by character varying(255),
  CONSTRAINT firmware_history_pkey PRIMARY KEY (device, firmware)
);

ALTER TABLE public.firmware_history OWNER TO osp_admin;
